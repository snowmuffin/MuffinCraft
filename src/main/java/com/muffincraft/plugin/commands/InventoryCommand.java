package com.muffincraft.plugin.commands;

import com.muffincraft.plugin.MuffinCraftPlugin;
import com.muffincraft.plugin.inventory.CustomInventoryGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventoryCommand implements CommandExecutor {
    private final MuffinCraftPlugin plugin;

    public InventoryCommand(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        CustomInventoryGUI gui = new CustomInventoryGUI(plugin, player);
        gui.open();
        return true;
    }
}
