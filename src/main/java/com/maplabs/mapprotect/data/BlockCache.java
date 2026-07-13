package com.maplabs.mapprotect.data;

import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockCache {

    private final Map<String, BlockData> cache = new ConcurrentHashMap<>();
    private final Map<String, Boolean> loadedChunks = new ConcurrentHashMap<>();

    private String getBlockKey(String world, int x, int y, int z) {
        return world + ":" + x + ":" + y + ":" + z;
    }

    private String getChunkKey(String world, int chunkX, int chunkZ) {
        return world + ":" + chunkX + ":" + chunkZ;
    }

    public void addBlock(BlockData blockData) {
        cache.put(getBlockKey(blockData.getWorldName(), blockData.getX(), blockData.getY(), blockData.getZ()), blockData);
    }

    public BlockData getBlockData(Block block) {
        return cache.get(getBlockKey(block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
    }

    public void removeBlock(Block block) {
        cache.remove(getBlockKey(block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
    }

    public boolean isChunkLoadedInCache(String worldName, int chunkX, int chunkZ) {
        return loadedChunks.containsKey(getChunkKey(worldName, chunkX, chunkZ));
    }

    public void addChunkData(String worldName, int chunkX, int chunkZ, List<BlockData> blocks) {
        for (BlockData b : blocks) {
            addBlock(b);
        }
        loadedChunks.put(getChunkKey(worldName, chunkX, chunkZ), true);
    }

    public void removeChunkData(String worldName, int chunkX, int chunkZ) {
        // Technically, keeping blocks in cache when chunk unloads is fine if memory is not an issue.
        // For a large server, we should remove them.
        List<String> keysToRemove = new ArrayList<>();
        int minX = chunkX << 4;
        int maxX = minX + 15;
        int minZ = chunkZ << 4;
        int maxZ = minZ + 15;

        for (Map.Entry<String, BlockData> entry : cache.entrySet()) {
            BlockData data = entry.getValue();
            if (data.getWorldName().equals(worldName) &&
                data.getX() >= minX && data.getX() <= maxX &&
                data.getZ() >= minZ && data.getZ() <= maxZ) {
                keysToRemove.add(entry.getKey());
            }
        }

        for (String key : keysToRemove) {
            cache.remove(key);
        }
        
        loadedChunks.remove(getChunkKey(worldName, chunkX, chunkZ));
    }

    public List<BlockData> getSurvivalBlocks() {
        List<BlockData> survivalBlocks = new ArrayList<>();
        for (BlockData data : cache.values()) {
            if (!data.isCreativePlaced()) {
                survivalBlocks.add(data);
            }
        }
        return survivalBlocks;
    }
}
