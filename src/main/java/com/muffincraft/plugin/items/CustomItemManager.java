package com.muffincraft.plugin.items;

import com.muffincraft.plugin.MuffinCraftPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 커스텀 아이템들을 관리하는 매니저 클래스
 */
public class CustomItemManager {
    private final MuffinCraftPlugin plugin;
    private final Map<String, CustomItem> customItems;

    public CustomItemManager(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
        this.customItems = new HashMap<>();
        registerCustomItems();
    }

    /**
     * 커스텀 아이템들을 등록합니다
     */
    private void registerCustomItems() {
        // 머핀 아이템 등록 (서버 화폐)
        CustomItem muffin = new CustomItem(
            plugin,
            "muffin",
            "§6머핀",
            Material.BREAD,  // 베이스 아이템은 빵
            1001,  // CustomModelData - 리소스팩에서 사용할 모델 번호
            Arrays.asList(
                "§e머핀크래프트의 공식 화폐",
                "§7달콤하고 맛있는 머핀!",
                "§a우클릭하여 사용"
            )
        );
        customItems.put("muffin", muffin);

        // 향후 다른 커스텀 아이템들도 여기에 추가 가능
        // 예: 특별한 도구, 장비, 소비 아이템 등
        
        plugin.getLogger().info("커스텀 아이템 " + customItems.size() + "개가 등록되었습니다.");
    }

    /**
     * 커스텀 아이템을 ID로 가져옵니다
     */
    public CustomItem getCustomItem(String id) {
        return customItems.get(id);
    }

    /**
     * 커스텀 아이템의 ItemStack을 생성합니다
     */
    public ItemStack createCustomItemStack(String id, int amount) {
        CustomItem customItem = customItems.get(id);
        if (customItem == null) {
            plugin.getLogger().warning("존재하지 않는 커스텀 아이템 ID: " + id);
            return null;
        }
        return customItem.createItemStack(amount);
    }

    /**
     * ItemStack이 커스텀 아이템인지 확인하고 ID를 반환합니다
     */
    public String getCustomItemId(ItemStack item) {
        return CustomItem.getCustomItemId(plugin, item);
    }

    /**
     * ItemStack이 특정 커스텀 아이템인지 확인합니다
     */
    public boolean isCustomItem(ItemStack item, String id) {
        CustomItem customItem = customItems.get(id);
        if (customItem == null) {
            return false;
        }
        return customItem.isCustomItem(item);
    }

    /**
     * 등록된 모든 커스텀 아이템 ID 목록을 반환합니다
     */
    public String[] getCustomItemIds() {
        return customItems.keySet().toArray(new String[0]);
    }

    /**
     * 머핀 아이템을 생성합니다 (편의 메서드)
     */
    public ItemStack createMuffin(int amount) {
        return createCustomItemStack("muffin", amount);
    }
}
