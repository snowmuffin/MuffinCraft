package com.muffincraft.plugin.inventory;

import com.muffincraft.plugin.MuffinCraftPlugin;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CustomInventoryGUI {
    private final MuffinCraftPlugin plugin;
    private final Player player;
    private final Inventory inventory;

    public CustomInventoryGUI(MuffinCraftPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, InventoryType.CHEST, Component.text("MuffinCraft Inventory"));
    }

    public void open() {
        if (player == null || !player.isOnline()) {
            plugin.getLogger().warning("Cannot open inventory for offline player: " + 
                (player != null ? player.getName() : "null"));
            return;
        }
        player.openInventory(inventory);
    }


    public void updateInventory(ItemStack[] items) {
        inventory.setContents(items);
    }
}
