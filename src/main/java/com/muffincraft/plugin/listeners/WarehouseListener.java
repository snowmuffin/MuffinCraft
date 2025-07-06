package com.muffincraft.plugin.listeners;

import com.muffincraft.plugin.MuffinCraftPlugin;
import com.muffincraft.plugin.services.WarehouseService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.entity.Player;

public class WarehouseListener implements Listener {
    private final MuffinCraftPlugin plugin;
    private final WarehouseService warehouseService;

    public WarehouseListener(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
        this.warehouseService = plugin.getWarehouseService();
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String inventoryTitle = event.getView().getTitle();
        
        // 창고 GUI가 열려있는지 확인
        if (inventoryTitle.equals("§6MuffinCraft 외부 창고")) {
            event.setCancelled(true); // 아이템 이동 방지
            
            // 창고 GUI에서 클릭한 경우 (출금)
            if (event.getClickedInventory() != null && 
                event.getClickedInventory().equals(event.getView().getTopInventory())) {
                
                if (event.getCurrentItem() != null) {
                    boolean isShiftClick = event.isShiftClick();
                    boolean isRightClick = event.isRightClick();
                    
                    warehouseService.handleWarehouseClick(player, event.getCurrentItem(), isShiftClick, isRightClick);
                }
            }
            // 플레이어 인벤토리에서 클릭한 경우 (입금)
            else if (event.getClickedInventory() != null && 
                     event.getClickedInventory().equals(player.getInventory())) {
                
                if (event.getCurrentItem() != null) {
                    boolean isShiftClick = event.isShiftClick();
                    boolean isRightClick = event.isRightClick();
                    
                    warehouseService.handleDepositClick(player, event.getCurrentItem(), isShiftClick, isRightClick);
                }
            }
        }
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        String inventoryTitle = event.getView().getTitle();
        
        // 창고 GUI를 닫는 경우
        if (inventoryTitle.equals("§6MuffinCraft 외부 창고")) {
            warehouseService.closeWarehouse(player);
            player.sendMessage("§7창고가 닫혔습니다.");
        }
    }
}
