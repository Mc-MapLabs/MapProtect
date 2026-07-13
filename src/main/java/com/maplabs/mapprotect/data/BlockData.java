package com.maplabs.mapprotect.data;

import java.util.UUID;

public class BlockData {

    private final String worldName;
    private final int x;
    private final int y;
    private final int z;
    private final UUID ownerUUID;
    private final boolean creativePlaced;

    public BlockData(String worldName, int x, int y, int z, UUID ownerUUID, boolean creativePlaced) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ownerUUID = ownerUUID;
        this.creativePlaced = creativePlaced;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public boolean isCreativePlaced() {
        return creativePlaced;
    }
}
