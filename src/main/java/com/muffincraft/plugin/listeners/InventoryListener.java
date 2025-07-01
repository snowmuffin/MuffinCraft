package com.muffincraft.plugin.listeners;

import com.muffincraft.plugin.MuffinCraftPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Arrays;

public class InventoryListener implements Listener {
    private final MuffinCraftPlugin plugin;

    public InventoryListener(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (!event.getView().getTitle().equals("MuffinCraft Inventory")) {
            return;
        }

        event.setCancelled(true);
        // 추가적인 인벤토리 클릭 처리 로직
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        if (!event.getView().getTitle().equals("MuffinCraft Inventory")) {
            return;
        }

        Player player = (Player) event.getPlayer();
        ItemStack[] items = event.getInventory().getContents();

        // GameHub API를 통해 인벤토리 상태 업데이트
        plugin.getInventoryService().handleInventoryChange(player, Arrays.asList(items));
    }
}
