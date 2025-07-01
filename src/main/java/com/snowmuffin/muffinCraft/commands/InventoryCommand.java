package com.snowmuffin.muffincraft.commands;

import com.snowmuffin.muffincraft.MuffinCraft;
import com.snowmuffin.muffincraft.inventory.CustomInventoryGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventoryCommand implements CommandExecutor {
    private final MuffinCraft plugin;

    public InventoryCommand(MuffinCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다!");
            return true;
        }

        Player player = (Player) sender;
        CustomInventoryGUI gui = new CustomInventoryGUI(plugin, player);
        gui.openInventory();
        return true;
    }
}
