package com.maplabs.mapprotect.tasks;

import com.maplabs.mapprotect.MapProtect;
import com.maplabs.mapprotect.data.BlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;

public class CleanupTask implements Runnable {

    private final MapProtect plugin;

    public CleanupTask(MapProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Fetch all survival placed blocks
        List<BlockData> survivalBlocks = plugin.getBlockCache().getSurvivalBlocks();

        if (survivalBlocks.isEmpty()) {
            return;
        }

        // Must remove blocks on the main thread in batches
        int blocksPerTick = plugin.getConfig().getInt("performance.blocks-per-tick", 50);

        new org.bukkit.scheduler.BukkitRunnable() {
            private int index = 0;

            @Override
            public void run() {
                int processed = 0;

                while (index < survivalBlocks.size() && processed < blocksPerTick) {
                    BlockData blockData = survivalBlocks.get(index);
                    World world = Bukkit.getWorld(blockData.getWorldName());
                    if (world != null) {
                        Block block = world.getBlockAt(blockData.getX(), blockData.getY(), blockData.getZ());
                        if (block.getType() != Material.AIR) {
                            block.setType(Material.AIR, false); // false to not apply physics if possible to avoid lag
                        }
                        // Remove from cache
                        plugin.getBlockCache().removeBlock(block);
                    }
                    
                    index++;
                    processed++;
                }

                if (index >= survivalBlocks.size()) {
                    // Remove all survival blocks from DB
                    plugin.getDatabaseManager().removeSurvivalBlocksAsync();
                    plugin.getLogger().info("Cleaned up " + survivalBlocks.size() + " survival placed blocks.");
                    
                    for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
                    }
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
}
