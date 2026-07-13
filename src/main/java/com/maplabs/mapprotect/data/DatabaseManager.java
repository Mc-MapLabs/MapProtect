package com.maplabs.mapprotect.data;

import com.maplabs.mapprotect.MapProtect;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class DatabaseManager {

    private final MapProtect plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(MapProtect plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File dbFile = new File(dataFolder, "blocks.db");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        config.setPoolName("MapProtect-SQLitePool");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTestQuery("SELECT 1");

        dataSource = new HikariDataSource(config);
        createTable();
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS placed_blocks (" +
                "world VARCHAR(255) NOT NULL," +
                "x INTEGER NOT NULL," +
                "y INTEGER NOT NULL," +
                "z INTEGER NOT NULL," +
                "owner_uuid VARCHAR(36) NOT NULL," +
                "creative BOOLEAN NOT NULL," +
                "PRIMARY KEY (world, x, y, z)" +
                ");";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create table", e);
        }
    }

    public void saveBlockAsync(BlockData blockData) {
        CompletableFuture.runAsync(() -> {
            String sql = "INSERT OR REPLACE INTO placed_blocks (world, x, y, z, owner_uuid, creative) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, blockData.getWorldName());
                stmt.setInt(2, blockData.getX());
                stmt.setInt(3, blockData.getY());
                stmt.setInt(4, blockData.getZ());
                stmt.setString(5, blockData.getOwnerUUID().toString());
                stmt.setBoolean(6, blockData.isCreativePlaced());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save block async", e);
            }
        });
    }

    public void removeBlockAsync(String worldName, int x, int y, int z) {
        CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM placed_blocks WHERE world = ? AND x = ? AND y = ? AND z = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, worldName);
                stmt.setInt(2, x);
                stmt.setInt(3, y);
                stmt.setInt(4, z);
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to remove block async", e);
            }
        });
    }

    public void removeSurvivalBlocksAsync() {
        CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM placed_blocks WHERE creative = 0";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to clear survival blocks async", e);
            }
        });
    }

    public CompletableFuture<List<BlockData>> loadChunkBlocksAsync(String worldName, int chunkX, int chunkZ) {
        return CompletableFuture.supplyAsync(() -> {
            List<BlockData> list = new ArrayList<>();
            int minX = chunkX << 4;
            int maxX = minX + 15;
            int minZ = chunkZ << 4;
            int maxZ = minZ + 15;

            String sql = "SELECT * FROM placed_blocks WHERE world = ? AND x >= ? AND x <= ? AND z >= ? AND z <= ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, worldName);
                stmt.setInt(2, minX);
                stmt.setInt(3, maxX);
                stmt.setInt(4, minZ);
                stmt.setInt(5, maxZ);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        list.add(new BlockData(
                                rs.getString("world"),
                                rs.getInt("x"),
                                rs.getInt("y"),
                                rs.getInt("z"),
                                UUID.fromString(rs.getString("owner_uuid")),
                                rs.getBoolean("creative")
                        ));
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load chunk blocks async", e);
            }
            return list;
        });
    }
}
