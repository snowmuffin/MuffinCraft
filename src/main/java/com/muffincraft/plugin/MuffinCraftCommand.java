package com.muffincraft.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuffinCraftCommand implements CommandExecutor {
    private final MuffinCraftPlugin plugin;

    public MuffinCraftCommand(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6MuffinCraft §7- §fUsage: /muffincraft [reload|sync]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("muffincraft.admin")) {
                    sender.sendMessage("§cYou don't have permission to use this command.");
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage("§aMuffinCraft configuration reloaded!");
                return true;

            case "sync":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cThis command can only be used by players.");
                    return true;
                }
                Player player = (Player) sender;
                // TODO: Implement manual sync
                sender.sendMessage("§aSyncing inventory...");
                return true;

            default:
                sender.sendMessage("§cUnknown command. Use /muffincraft for help.");
                return true;
        }
    }
}
