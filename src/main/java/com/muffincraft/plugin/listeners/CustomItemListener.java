package com.muffincraft.plugin.listeners;

import com.muffincraft.plugin.MuffinCraftPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;

/**
 * 커스텀 아이템 사용 이벤트를 처리하는 리스너
 */
public class CustomItemListener implements Listener {
    private final MuffinCraftPlugin plugin;

    public CustomItemListener(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 플레이어가 아이템을 사용할 때 처리
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 우클릭만 처리
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // 메인 핸드만 처리 (중복 이벤트 방지)
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        // 커스텀 아이템 ID 확인
        String customItemId = plugin.getCustomItemManager().getCustomItemId(item);
        if (customItemId == null) {
            return;
        }

        // 커스텀 아이템별 처리
        switch (customItemId) {
            case "muffin":
                handleMuffinUse(player, item, event);
                break;
            // 향후 다른 커스텀 아이템들도 여기에 추가
            default:
                break;
        }
    }

    /**
     * 머핀 아이템 사용 처리
     */
    private void handleMuffinUse(Player player, ItemStack muffin, PlayerInteractEvent event) {
        event.setCancelled(true); // 기본 상호작용 방지

        // 머핀 먹기 효과
        player.sendMessage("§a맛있는 머핀을 먹었습니다! §e+2 ❤");
        
        // 체력 회복 (하트 2개, 4 health points)
        double currentHealth = player.getHealth();
        double maxHealth = player.getMaxHealth();
        double newHealth = Math.min(maxHealth, currentHealth + 4.0);
        player.setHealth(newHealth);

        // 배고픔 회복
        int currentFood = player.getFoodLevel();
        int newFood = Math.min(20, currentFood + 6);
        player.setFoodLevel(newFood);

        // 포화도 증가
        float currentSaturation = player.getSaturation();
        float newSaturation = Math.min(20.0f, currentSaturation + 8.0f);
        player.setSaturation(newSaturation);

        // 아이템 개수 감소
        if (muffin.getAmount() > 1) {
            muffin.setAmount(muffin.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        // 머핀 사용 로그
        plugin.getLogger().info(player.getName() + "이(가) 머핀을 사용했습니다.");

        // 향후 여기에 서버 이코노미 연동이나 특별한 효과를 추가할 수 있습니다
        // 예: 임시 버프, 경험치 증가, 운 증가 등
    }
}
