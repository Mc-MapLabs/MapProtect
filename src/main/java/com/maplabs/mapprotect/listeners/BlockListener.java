package com.maplabs.mapprotect.listeners;

import com.maplabs.mapprotect.MapProtect;
import com.maplabs.mapprotect.data.BlockData;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockListener implements Listener {

    private final MapProtect plugin;
    private final Map<UUID, Long> lastWarningTimes = new HashMap<>();

    public BlockListener(MapProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        boolean creative = player.getGameMode() == GameMode.CREATIVE;

        BlockData blockData = new BlockData(
                block.getWorld().getName(),
                block.getX(),
                block.getY(),
                block.getZ(),
                player.getUniqueId(),
                creative
        );

        plugin.getBlockCache().addBlock(blockData);
        plugin.getDatabaseManager().saveBlockAsync(blockData);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Check if player has creative bypass
        if (player.getGameMode() == GameMode.CREATIVE && player.hasPermission("MapProtect.creative.bypass")) {
            return; // Allow breaking anything
        }

        BlockData blockData = plugin.getBlockCache().getBlockData(block);

        boolean allow = false;

        if (blockData == null) {
            // Original map block
            allow = false;
        } else {
            if (blockData.isCreativePlaced()) {
                // Creative placed block
                allow = false;
            } else {
                // Survival placed block
                allow = true;
            }
        }

        if (!allow) {
            event.setCancelled(true);
            sendWarning(player);
        } else {
            // If allowed and block is broken, remove from tracking
            plugin.getBlockCache().removeBlock(block);
            plugin.getDatabaseManager().removeBlockAsync(
                    block.getWorld().getName(),
                    block.getX(),
                    block.getY(),
                    block.getZ()
            );
        }
    }

    private void sendWarning(Player player) {
        int cooldownSeconds = plugin.getConfig().getInt("cooldown.seconds", 3);
        long now = System.currentTimeMillis();
        long lastTime = lastWarningTimes.getOrDefault(player.getUniqueId(), 0L);

        if (now - lastTime >= cooldownSeconds * 1000L) {
            String prefix = plugin.getMessages().getString("messages.prefix", "");
            String message = plugin.getMessages().getString("messages.warning");
            if (message != null && !message.isEmpty()) {
                message = message.replace("%prefix%", prefix);
                player.sendMessage(com.maplabs.mapprotect.utils.ColorUtils.color(message));
            }
            lastWarningTimes.put(player.getUniqueId(), now);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        String worldName = event.getWorld().getName();
        int chunkX = event.getChunk().getX();
        int chunkZ = event.getChunk().getZ();

        if (!plugin.getBlockCache().isChunkLoadedInCache(worldName, chunkX, chunkZ)) {
            plugin.getDatabaseManager().loadChunkBlocksAsync(worldName, chunkX, chunkZ).thenAccept(blocks -> {
                if (!blocks.isEmpty()) {
                    plugin.getBlockCache().addChunkData(worldName, chunkX, chunkZ, blocks);
                }
            });
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        plugin.getBlockCache().removeChunkData(
                event.getWorld().getName(),
                event.getChunk().getX(),
                event.getChunk().getZ()
        );
    }
}
