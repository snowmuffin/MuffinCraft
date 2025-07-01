package com.snowmuffin.muffincraft.inventory;

import com.snowmuffin.muffincraft.MuffinCraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class CustomInventoryGUI {
    private final MuffinCraft plugin;
    private final Player player;
    private Inventory inventory;

    public CustomInventoryGUI(MuffinCraft plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void openInventory() {
        inventory = Bukkit.createInventory(null, 54, "커스텀 인벤토리");

        // 백엔드에서 인벤토리 데이터 가져오기
        plugin.getBackendService().getPlayerInventory(player, "main")
            .thenAccept(items -> {
                if (items != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        loadItems(items);
                        player.openInventory(inventory);
                    });
                }
            });
    }

    private void loadItems(List<Map<String, Object>> items) {
        for (Map<String, Object> item : items) {
            int slot = ((Double) item.get("slot")).intValue();
            String materialName = (String) item.get("material");
            int amount = ((Double) item.get("amount")).intValue();

            ItemStack itemStack = new ItemStack(Material.valueOf(materialName), amount);
            if (item.containsKey("displayName")) {
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName((String) item.get("displayName"));
                itemStack.setItemMeta(meta);
            }

            inventory.setItem(slot, itemStack);
        }
    }

    public static ItemStack createGuiItem(Material material, String name) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
