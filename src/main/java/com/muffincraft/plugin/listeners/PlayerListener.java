package com.muffincraft.plugin.listeners;

import com.muffincraft.plugin.MuffinCraftPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
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
        
        // 토큰 자동 갱신 체크 (비동기)
        plugin.getAuthService().refreshPlayerToken(player);
        
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 플레이어 접속 시 토큰 자동 갱신 (비동기)
        plugin.getAuthService().refreshPlayerToken(player).thenAccept(success -> {
            if (success) {
                plugin.getLogger().info("플레이어 " + player.getName() + " 접속 - 토큰 준비 완료");
            } else {
                plugin.getLogger().warning("플레이어 " + player.getName() + " 접속 - 토큰 갱신 실패");
            }
        });
        
        // 플레이어 접속 시 온라인 인벤토리 로드 (토큰 갱신 후 약간의 지연)
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
        }, 20L); // 1초 후 실행
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // 토큰 자동 갱신 체크 (비동기)
        plugin.getAuthService().refreshPlayerToken(player);
        

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // 토큰 자동 갱신 체크 (비동기)
        plugin.getAuthService().refreshPlayerToken(event.getPlayer());
        

    }
}
