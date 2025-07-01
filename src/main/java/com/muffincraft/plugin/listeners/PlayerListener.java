package com.muffincraft.plugin.listeners;

import com.muffincraft.plugin.MuffinCraftPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.entity.Player;

public class PlayerListener implements Listener {
    private final MuffinCraftPlugin plugin;

    public PlayerListener(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        plugin.getInventoryService().handleInventoryChange(player, event.getCurrentItem());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 플레이어 접속 시 온라인 인벤토리 로드
        plugin.getInventoryService().loadPlayerInventory(event.getPlayer());
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        plugin.getInventoryService().handleInventoryChange(
            event.getPlayer(),
            event.getItem().getItemStack()
        );
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        plugin.getInventoryService().handleInventoryChange(
            event.getPlayer(),
            event.getItemDrop().getItemStack()
        );
    }
}
