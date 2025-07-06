package com.muffincraft.plugin.inventory;

import com.muffincraft.plugin.MuffinCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CustomInventoryGUI {
    private final MuffinCraftPlugin plugin;
    private final Player player;
    private final Inventory inventory;

    public CustomInventoryGUI(MuffinCraftPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54, "MuffinCraft Inventory");
    }

    public void open() {
        player.openInventory(inventory);
    }


    public void updateInventory(ItemStack[] items) {
        inventory.setContents(items);
    }
}
