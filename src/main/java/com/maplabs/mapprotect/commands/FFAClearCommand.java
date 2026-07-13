package com.maplabs.mapprotect.commands;

import com.maplabs.mapprotect.MapProtect;
import com.maplabs.mapprotect.tasks.CleanupTask;
import com.maplabs.mapprotect.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class FFAClearCommand implements CommandExecutor, TabCompleter {

    private final MapProtect plugin;

    public FFAClearCommand(MapProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getMessages().getString("messages.prefix", "");
        
        if (!sender.hasPermission("MapProtect.admin.clear")) {
            String msg = plugin.getMessages().getString("messages.no-permission", "&cYou don't have permission to use this command.");
            sender.sendMessage(ColorUtils.color(msg.replace("%prefix%", prefix)));
            return true;
        }

        String startMsg = plugin.getMessages().getString("messages.cleanup-force", "&aForce-running Map cleanup task...");
        sender.sendMessage(ColorUtils.color(startMsg.replace("%prefix%", prefix)));
        
        new CleanupTask(plugin).run();
        if (plugin.getTimerTask() != null) {
            plugin.getTimerTask().resetTimer();
        }
        
        String endMsg = plugin.getMessages().getString("messages.cleanup-complete", "&aCleanup complete.");
        sender.sendMessage(ColorUtils.color(endMsg.replace("%prefix%", prefix)));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>(); // No subcommands, return empty
    }
}
