package com.maplabs.mapprotect;

import com.maplabs.mapprotect.data.BlockCache;
import com.maplabs.mapprotect.data.DatabaseManager;
import com.maplabs.mapprotect.listeners.BlockListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MapProtect extends JavaPlugin {

    private DatabaseManager databaseManager;
    private BlockCache blockCache;

    private com.maplabs.mapprotect.tasks.TimerTask timerTask;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    @Override
    public void onEnable() {
        // Save default configs
        saveDefaultConfig();
        createMessagesConfig();

        // Initialize Data
        blockCache = new BlockCache();
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new BlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new com.maplabs.mapprotect.listeners.PlayerListener(this), this);

        // Register Commands
        getCommand("mapprotect").setExecutor(new com.maplabs.mapprotect.commands.FFAClearCommand(this));

        // Start the Main Timer Task
        timerTask = new com.maplabs.mapprotect.tasks.TimerTask(this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, timerTask, 20L, 20L); // run every 1 second

        getLogger().info("MapProtect has been enabled successfully.");
    }

    private void createMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getMessages() {
        return messagesConfig;
    }

    @Override
    public void onDisable() {
        // Disconnect DB
        if (databaseManager != null) {
            databaseManager.disconnect();
        }

        getLogger().info("MapProtect has been disabled.");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public BlockCache getBlockCache() {
        return blockCache;
    }

    public com.maplabs.mapprotect.tasks.TimerTask getTimerTask() {
        return timerTask;
    }
}
