package com.muffincraft.plugin.listeners;

import com.muffincraft.plugin.MuffinCraftPlugin;
import com.muffincraft.plugin.services.WarehouseService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.entity.Player;

public class WarehouseListener implements Listener {
    private final WarehouseService warehouseService;

    public WarehouseListener(MuffinCraftPlugin plugin) {
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
        
        if (inventoryTitle.equals("§6MuffinCraft 외부 창고")) {
            event.setCancelled(true);
            
            if (event.getClickedInventory() != null && 
                event.getClickedInventory().equals(event.getView().getTopInventory())) {
                
                if (event.getCurrentItem() != null) {
                    boolean isShiftClick = event.isShiftClick();
                    boolean isRightClick = event.isRightClick();
                    
                    warehouseService.handleWarehouseClick(player, event.getCurrentItem(), isShiftClick, isRightClick);
                }
            }
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
