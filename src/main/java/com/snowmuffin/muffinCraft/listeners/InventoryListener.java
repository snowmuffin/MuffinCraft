package com.snowmuffin.muffincraft.listeners;

import com.snowmuffin.muffincraft.MuffinCraft;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryListener implements Listener {
    private final MuffinCraft plugin;

    public InventoryListener(MuffinCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals("커스텀 인벤토리")) return;

        // 여기서 클릭 이벤트를 처리할 수 있습니다.
        // 예: 특정 슬롯 클릭 금지, 아이템 이동 제한 등
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!event.getView().getTitle().equals("커스텀 인벤토리")) return;

        Player player = (Player) event.getPlayer();
        List<Map<String, Object>> items = new ArrayList<>();

        // 인벤토리의 모든 아이템을 저장 형식으로 변환
        for (int i = 0; i < event.getInventory().getSize(); i++) {
            ItemStack item = event.getInventory().getItem(i);
            if (item != null) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("slot", i);
                itemData.put("material", item.getType().name());
                itemData.put("amount", item.getAmount());

                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    itemData.put("displayName", item.getItemMeta().getDisplayName());
                }

                items.add(itemData);
            }
        }

        // 백엔드에 인벤토리 상태 업데이트
        plugin.getBackendService().updatePlayerInventory(player, "main", items)
            .thenAccept(success -> {
                if (!success) {
                    player.sendMessage("§c인벤토리 저장에 실패했습니다!");
                }
            });
    }
}
