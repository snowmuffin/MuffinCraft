package com.muffincraft.plugin.services;

import com.muffincraft.plugin.MuffinCraftPlugin;
import com.muffincraft.plugin.api.GameHubAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryService {
    private final MuffinCraftPlugin plugin;
    private final GameHubAPI gameHubAPI;
    private final Map<UUID, Long> lastSyncTime;
    private static final long SYNC_COOLDOWN = 1000; // 1초 동안 중복 동기화 방지

    public InventoryService(MuffinCraftPlugin plugin, GameHubAPI gameHubAPI) {
        this.plugin = plugin;
        this.gameHubAPI = gameHubAPI;
        this.lastSyncTime = new ConcurrentHashMap<>();
    }

    public void handleInventoryChange(Player player, List<ItemStack> items) {
        UUID playerId = player.getUniqueId();

        // 중복 동기화 방지
        long currentTime = System.currentTimeMillis();
        Long lastSync = lastSyncTime.get(playerId);
        if (lastSync != null && currentTime - lastSync < SYNC_COOLDOWN) {
            return;
        }

        lastSyncTime.put(playerId, currentTime);

        // 각 아이템에 대해 동기화 수행
        for (ItemStack item : items) {
            if (item != null) {
                gameHubAPI.syncInventory(playerId, item)
                    .thenAccept(success -> {
                        if (!success) {
                            plugin.getLogger().warning("Failed to sync item for player: " + player.getName());
                        }
                    });
            }
        }
    }

    public void handleInventoryChange(Player player, ItemStack item) {
        if (item != null) {
            handleInventoryChange(player, List.of(item));
        }
    }

    public void loadPlayerInventory(Player player) {
        gameHubAPI.getInventory(player.getUniqueId())
            .thenAccept(inventory -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    // TODO: 서버에서 받아온 인벤토리 데이터를 플레이어 인벤토리에 적용
                    // 이 부분은 실제 구현 시 아이템 변환 로직이 필요합니다
                });
            });
    }
}
