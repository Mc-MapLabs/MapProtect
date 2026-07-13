package com.maplabs.mapprotect.tasks;

import com.maplabs.mapprotect.MapProtect;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TimerTask implements Runnable {

    private final MapProtect plugin;
    private int secondsUntilCleanup;
    private final int intervalSeconds;
    private final List<Integer> warningTimes = Arrays.asList(60, 30, 15, 10, 5, 4, 3, 2, 1);

    public TimerTask(MapProtect plugin) {
        this.plugin = plugin;
        this.intervalSeconds = plugin.getConfig().getInt("cleanup.interval-minutes", 10) * 60;
        this.secondsUntilCleanup = this.intervalSeconds;
    }

    public void resetTimer() {
        this.secondsUntilCleanup = this.intervalSeconds;
    }

    @Override
    public void run() {
        secondsUntilCleanup--;

        if (warningTimes.contains(secondsUntilCleanup)) {
            String rawMsg = plugin.getMessages().getString("messages.action-bar-warning", "<gradient:#2F80ED:#56CCF2>&lMap Clearing in %time% &nSeconds</gradient>");
            rawMsg = rawMsg.replace("%time%", String.valueOf(secondsUntilCleanup));
            rawMsg = rawMsg.replace("\uFE0F", ""); // Strip variation selector that causes 'VS16' boxes in Minecraft
            String warningMessage = com.maplabs.mapprotect.utils.ColorUtils.color(rawMsg);
            TextComponent textComponent = new TextComponent(warningMessage);
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);
            }
        }

        if (secondsUntilCleanup <= 0) {
            new CleanupTask(plugin).run();
            resetTimer();
        }
    }
}
