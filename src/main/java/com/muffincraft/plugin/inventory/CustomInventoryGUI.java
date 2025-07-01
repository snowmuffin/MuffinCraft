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
        loadInventory();
        player.openInventory(inventory);
    }

    private void loadInventory() {
        // GameHub API를 통해 플레이어의 인벤토리 데이터를 가져옵니다
        plugin.getInventoryService().loadPlayerInventory(player);
    }

    public void updateInventory(ItemStack[] items) {
        inventory.setContents(items);
    }
}
