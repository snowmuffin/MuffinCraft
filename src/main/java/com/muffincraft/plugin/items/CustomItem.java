package com.muffincraft.plugin.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 커스텀 아이템을 정의하는 클래스
 */
public class CustomItem {
    private final String id;
    private final String displayName;
    private final Material baseMaterial;
    private final int customModelData;
    private final List<String> lore;
    private final NamespacedKey customItemKey;

    public CustomItem(Plugin plugin, String id, String displayName, Material baseMaterial, int customModelData, List<String> lore) {
        this.id = id;
        this.displayName = displayName;
        this.baseMaterial = baseMaterial;
        this.customModelData = customModelData;
        this.lore = lore;
        this.customItemKey = new NamespacedKey(plugin, "custom_item_id");
    }

    /**
     * 커스텀 아이템의 ItemStack을 생성합니다
     */
    public ItemStack createItemStack(int amount) {
        ItemStack item = new ItemStack(baseMaterial, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.displayName(Component.text(displayName).color(NamedTextColor.WHITE));
            meta.setCustomModelData(customModelData);
            meta.lore(lore.stream()
                .map(line -> Component.text(line).color(NamedTextColor.GRAY))
                .collect(Collectors.toList()));
            
            // 커스텀 아이템 식별자 저장
            meta.getPersistentDataContainer().set(customItemKey, PersistentDataType.STRING, id);
            
            item.setItemMeta(meta);
            
            // 디버깅을 위한 로그
            System.out.println("[DEBUG] 커스텀 아이템 생성됨: " + id + 
                ", CustomModelData: " + customModelData + 
                ", Material: " + baseMaterial + 
                ", Amount: " + amount);
        }
        
        return item;
    }

    /**
     * ItemStack이 이 커스텀 아이템인지 확인합니다
     */
    public boolean isCustomItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        String customId = meta.getPersistentDataContainer().get(customItemKey, PersistentDataType.STRING);
        return id.equals(customId);
    }

    /**
     * ItemStack에서 커스텀 아이템 ID를 추출합니다
     */
    public static String getCustomItemId(Plugin plugin, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        NamespacedKey key = new NamespacedKey(plugin, "custom_item_id");
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    // Getters
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Material getBaseMaterial() { return baseMaterial; }
    public int getCustomModelData() { return customModelData; }
    public List<String> getLore() { return lore; }
}
