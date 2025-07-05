package com.muffincraft.plugin.services;

import com.muffincraft.plugin.MuffinCraftPlugin;
import com.muffincraft.plugin.api.GameHubAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @deprecated 인벤토리 자동 동기화는 더 이상 사용하지 않습니다.
 * 대신 WarehouseService를 사용하여 외부 창고 시스템을 이용하세요.
 */
@Deprecated
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

    /**
     * @deprecated 자동 인벤토리 동기화는 더 이상 사용하지 않습니다.
     * 플레이어는 /warehouse 명령어를 통해 수동으로 창고를 관리해야 합니다.
     */
    @Deprecated
    public void handleInventoryChange(Player player, List<ItemStack> items) {
        // 더 이상 자동 동기화하지 않음
        plugin.getLogger().info("인벤토리 자동 동기화는 비활성화되었습니다. /warehouse 명령어를 사용하세요.");
    }

    /**
     * @deprecated 자동 인벤토리 동기화는 더 이상 사용하지 않습니다.
     */
    @Deprecated
    public void handleInventoryChange(Player player, ItemStack item) {
        // 더 이상 자동 동기화하지 않음
    }

    /**
     * @deprecated 자동 인벤토리 로드는 더 이상 사용하지 않습니다.
     * 플레이어는 /warehouse 명령어를 통해 창고에 접근해야 합니다.
     */
    @Deprecated
    public void loadPlayerInventory(Player player) {
        // 더 이상 자동으로 인벤토리를 로드하지 않음
        player.sendMessage("§e이제 /warehouse 명령어를 사용하여 외부 창고에 접근할 수 있습니다!");
        plugin.getLogger().info("플레이어 " + player.getName() + "에게 외부 창고 시스템 안내 완료");
    }
}
