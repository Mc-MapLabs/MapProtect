package com.maplabs.mapprotect.listeners;

import com.maplabs.mapprotect.MapProtect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    private final MapProtect plugin;

    public PlayerListener(MapProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location loc = player.getLocation();

        // Wait 1 tick for the items to spawn in the world
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            List<Entity> dropsToDespawn = new ArrayList<>();
            for (Entity e : loc.getWorld().getNearbyEntities(loc, 3, 3, 3)) {
                if (e instanceof Item) {
                    // Check if it was just spawned (ticksLived <= 2)
                    if (e.getTicksLived() <= 2) {
                        dropsToDespawn.add(e);
                    }
                }
            }

            if (!dropsToDespawn.isEmpty()) {
                // Remove them after 15 seconds
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Entity e : dropsToDespawn) {
                        if (e.isValid()) {
                            e.remove();
                        }
                    }
                }, 15 * 20L); // 15 seconds
            }
        }, 1L);
    }
}
