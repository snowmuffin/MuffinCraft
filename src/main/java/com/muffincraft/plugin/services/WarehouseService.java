package com.muffincraft.plugin.services;

import com.muffincraft.plugin.MuffinCraftPlugin;
import com.muffincraft.plugin.api.GameHubAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarehouseService {
    private final MuffinCraftPlugin plugin;
    private final GameHubAPI gameHubAPI;
    private final Map<UUID, Inventory> openWarehouses;

    public WarehouseService(MuffinCraftPlugin plugin, GameHubAPI gameHubAPI) {
        this.plugin = plugin;
        this.gameHubAPI = gameHubAPI;
        this.openWarehouses = new HashMap<>();
    }

    /**
     * 플레이어에게 창고 GUI를 열어줍니다
     */
    public void openWarehouseGUI(Player player) {
        String authHeader = plugin.getAuthService().getAuthorizationHeader(player);
        
        if (authHeader == null) {
            player.sendMessage("유효하지 않은 접근이 감지되었습니다. 다시 시도해 주세요.");
        }

        // 창고 데이터 가져오기
        gameHubAPI.getWarehouseItems(player.getUniqueId(), authHeader)
            .thenAccept(warehouseData -> {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    createAndShowWarehouseGUI(player, warehouseData);
                });
            })
            .exceptionally(throwable -> {
                player.sendMessage("§c창고 데이터를 불러오는데 실패했습니다: " + throwable.getMessage());
                return null;
            });
    }

    /**
     * 창고 GUI를 생성하고 보여줍니다
     */
    private void createAndShowWarehouseGUI(Player player, JSONArray warehouseData) {
        Inventory warehouse = Bukkit.createInventory(null, 54, "§6MuffinCraft 외부 창고");
        
        if (warehouseData != null) {
            for (Object item : warehouseData) {
                JSONObject itemObj = (JSONObject) item;
                String itemId = (String) itemObj.get("itemId");
                String itemName = (String) itemObj.get("itemName");
                Long quantity = (Long) itemObj.get("quantity");
                
                // Minecraft 아이템으로 변환
                Material material = Material.getMaterial(itemId.toUpperCase());
                if (material != null) {
                    ItemStack itemStack = new ItemStack(material, Math.min(quantity.intValue(), 64));
                    ItemMeta meta = itemStack.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName("§f" + itemName);
                        meta.setLore(Arrays.asList(
                            "§7창고 보유량: §e" + quantity,
                            "§7좌클릭: §a1개 출금",
                            "§7우클릭: §a64개 출금",
                            "§7Shift+클릭: §a전체 출금"
                        ));
                        itemStack.setItemMeta(meta);
                    }
                    warehouse.addItem(itemStack);
                }
            }
        }
        
        openWarehouses.put(player.getUniqueId(), warehouse);
        player.openInventory(warehouse);
        player.sendMessage("§a창고가 열렸습니다. 아이템을 클릭하여 출금하세요.");
    }

    /**
     * 손에 든 아이템을 창고에 입금합니다
     */
    public void depositItemFromHand(Player player, int quantity) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        
        if (handItem == null || handItem.getType() == Material.AIR) {
            player.sendMessage("§c손에 아이템을 들고 사용해주세요.");
            return;
        }
        
        if (quantity <= 0) {
            player.sendMessage("§c1개 이상의 수량을 입력해주세요.");
            return;
        }
        
        if (handItem.getAmount() < quantity) {
            player.sendMessage("§c손에 든 아이템이 부족합니다. (보유: " + handItem.getAmount() + "개)");
            return;
        }
        
        String authHeader = plugin.getAuthService().getAuthorizationHeader(player);
        if (authHeader == null) {
            player.sendMessage("§c토큰이 없어 창고를 사용할 수 없습니다.");
            return;
        }
        
        // 백엔드에 입금 요청
        gameHubAPI.depositWarehouseItem(player.getUniqueId(), handItem, quantity, authHeader)
            .thenAccept(success -> {
                if (success) {
                    // 플레이어 인벤토리에서 아이템 제거
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (handItem.getAmount() == quantity) {
                            player.getInventory().setItemInMainHand(null);
                        } else {
                            handItem.setAmount(handItem.getAmount() - quantity);
                        }
                        
                        String itemName = handItem.getType().name().toLowerCase().replace("_", " ");
                        player.sendMessage("§a" + itemName + " " + quantity + "개를 창고에 입금했습니다.");
                    });
                } else {
                    player.sendMessage("§c아이템 입금에 실패했습니다.");
                }
            })
            .exceptionally(throwable -> {
                player.sendMessage("§c아이템 입금 중 오류가 발생했습니다: " + throwable.getMessage());
                return null;
            });
    }

    /**
     * 창고에서 특정 아이템을 출금합니다
     */
    public void withdrawItem(Player player, String itemId, int quantity) {
        String authHeader = plugin.getAuthService().getAuthorizationHeader(player);
        if (authHeader == null) {
            player.sendMessage("§c토큰이 없어 창고를 사용할 수 없습니다.");
            return;
        }
        
        gameHubAPI.withdrawWarehouseItem(player.getUniqueId(), itemId, quantity, authHeader)
            .thenAccept(success -> {
                if (success) {
                    // 플레이어 인벤토리에 아이템 추가
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Material material = Material.getMaterial(itemId.toUpperCase());
                        if (material != null) {
                            ItemStack itemStack = new ItemStack(material, quantity);
                            
                            // 인벤토리 공간 확인 및 아이템 추가
                            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(itemStack);
                            
                            if (!leftover.isEmpty()) {
                                // 인벤토리가 가득 찬 경우 남은 아이템을 땅에 드롭
                                for (ItemStack item : leftover.values()) {
                                    player.getWorld().dropItem(player.getLocation(), item);
                                }
                                player.sendMessage("§e인벤토리가 가득 차서 일부 아이템이 땅에 떨어졌습니다.");
                            }
                            
                            String itemName = material.name().toLowerCase().replace("_", " ");
                            player.sendMessage("§a" + itemName + " " + quantity + "개를 창고에서 출금했습니다.");
                            
                            // 출금 성공 후 즉시 창고 GUI 갱신
                            refreshWarehouseGUI(player);
                        }
                    });
                } else {
                    player.sendMessage("§c아이템 출금에 실패했습니다.");
                }
            })
            .exceptionally(throwable -> {
                player.sendMessage("§c아이템 출금 중 오류가 발생했습니다: " + throwable.getMessage());
                return null;
            });
    }

    /**
     * 창고 아이템 목록을 채팅으로 보여줍니다
     */
    public void showWarehouseList(Player player) {
        String authHeader = plugin.getAuthService().getAuthorizationHeader(player);
        if (authHeader == null) {
            player.sendMessage("§c토큰이 없어 창고 정보를 조회할 수 없습니다.");
            return;
        }
        
        gameHubAPI.getWarehouseItems(player.getUniqueId(), authHeader)
            .thenAccept(warehouseData -> {
                player.sendMessage("§6=== 창고 아이템 목록 ===");
                
                if (warehouseData == null || warehouseData.isEmpty()) {
                    player.sendMessage("§7창고가 비어있습니다.");
                    return;
                }
                
                for (Object item : warehouseData) {
                    JSONObject itemObj = (JSONObject) item;
                    String itemId = (String) itemObj.get("itemId");
                    String itemName = (String) itemObj.get("itemName");
                    Long quantity = (Long) itemObj.get("quantity");
                    
                    player.sendMessage("§e" + itemName + " §7(ID: " + itemId + ") §f- §a" + quantity + "개");
                }
                
                player.sendMessage("§7총 " + warehouseData.size() + "종류의 아이템이 보관중입니다.");
            })
            .exceptionally(throwable -> {
                player.sendMessage("§c창고 정보 조회 중 오류가 발생했습니다: " + throwable.getMessage());
                return null;
            });
    }

    /**
     * 플레이어가 창고 GUI를 닫을 때 호출됩니다
     */
    public void closeWarehouse(Player player) {
        UUID playerId = player.getUniqueId();
        openWarehouses.remove(playerId);
    }

    /**
     * 플레이어가 창고 GUI에서 아이템을 클릭했을 때 처리합니다
     */
    public void handleWarehouseClick(Player player, ItemStack clickedItem, boolean isShiftClick, boolean isRightClick) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        // 아이템 메타에서 실제 아이템 ID 추출
        String itemId = clickedItem.getType().name();
        
        int withdrawQuantity;
        if (isShiftClick) {
            // Shift+클릭: 전체 출금 (최대 2304개 = 36스택)
            withdrawQuantity = 2304;
        } else if (isRightClick) {
            // 우클릭: 64개 출금
            withdrawQuantity = 64;
        } else {
            // 좌클릭: 1개 출금
            withdrawQuantity = 1;
        }
        
        withdrawItem(player, itemId, withdrawQuantity);
        
        // withdrawItem 메서드에서 API 응답 후 자동으로 갱신하므로 여기서는 제거
    }

    /**
     * 플레이어가 인게임 인벤토리에서 아이템을 클릭했을 때 창고에 입금 처리합니다
     */
    public void handleDepositClick(Player player, ItemStack clickedItem, boolean isShiftClick, boolean isRightClick) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        // 입금할 수량 결정
        final int depositQuantity;
        if (isShiftClick) {
            // Shift+클릭: 전체 입금
            depositQuantity = clickedItem.getAmount();
        } else if (isRightClick) {
            // 우클릭: 절반 입금 (최소 1개)
            depositQuantity = Math.max(1, clickedItem.getAmount() / 2);
        } else {
            // 좌클릭: 1개 입금
            depositQuantity = 1;
        }
        
        // 실제 보유량보다 많이 입금하려고 하는 경우 조정
        final int finalDepositQuantity = Math.min(depositQuantity, clickedItem.getAmount());
        
        String authHeader = plugin.getAuthService().getAuthorizationHeader(player);
        if (authHeader == null) {
            player.sendMessage("§c토큰이 없어 창고를 사용할 수 없습니다.");
            return;
        }
        
        // 백엔드에 입금 요청
        gameHubAPI.depositWarehouseItem(player.getUniqueId(), clickedItem, finalDepositQuantity, authHeader)
            .thenAccept(success -> {
                if (success) {
                    // 플레이어 인벤토리에서 아이템 제거
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (clickedItem.getAmount() == finalDepositQuantity) {
                            // 전체 수량 입금: 아이템 제거
                            player.getInventory().removeItem(clickedItem);
                        } else {
                            // 일부 수량 입금: 수량 감소
                            clickedItem.setAmount(clickedItem.getAmount() - finalDepositQuantity);
                        }
                        
                        String itemName = clickedItem.getType().name().toLowerCase().replace("_", " ");
                        player.sendMessage("§a" + itemName + " " + finalDepositQuantity + "개를 창고에 입금했습니다.");
                        
                        // 입금 성공 후 즉시 창고 GUI 갱신
                        refreshWarehouseGUI(player);
                    });
                } else {
                    player.sendMessage("§c아이템 입금에 실패했습니다.");
                }
            })
            .exceptionally(throwable -> {
                player.sendMessage("§c아이템 입금 중 오류가 발생했습니다: " + throwable.getMessage());
                return null;
            });
    }

    /**
     * 창고 GUI를 즉시 갱신합니다.
     */
    private void refreshWarehouseGUI(Player player) {
        UUID playerId = player.getUniqueId();
        
        // 창고가 열려있고 플레이어가 온라인인 경우에만 갱신
        if (openWarehouses.containsKey(playerId) && player.isOnline()) {
            plugin.getLogger().info("플레이어 " + player.getName() + "의 창고 GUI 즉시 갱신");
            
            // 현재 열린 창고 인벤토리를 가져와서 내용만 업데이트
            Inventory currentWarehouse = openWarehouses.get(playerId);
            if (currentWarehouse != null) {
                // 비동기 로딩으로 내용 업데이트 (인벤토리를 닫지 않음)
                updateWarehouseContents(player, currentWarehouse);
            }
        }
    }
    
    /**
     * 창고 내용을 업데이트합니다 (기존 인벤토리를 닫지 않음).
     */
    private void updateWarehouseContents(Player player, Inventory warehouse) {
        String authHeader = plugin.getAuthService().getAuthorizationHeader(player);
        if (authHeader == null) {
            return;
        }
        
        gameHubAPI.getWarehouseItems(player.getUniqueId(), authHeader)
            .thenAccept(warehouseData -> {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    // 현재 인벤토리 내용을 지우고 새로운 내용으로 채움
                    warehouse.clear();
                    
                    if (warehouseData != null && !warehouseData.isEmpty()) {
                        int slot = 0;
                        for (Object item : warehouseData) {
                            if (slot >= 54) break; // 창고 크기 제한
                            
                            JSONObject itemObj = (JSONObject) item;
                            String itemId = (String) itemObj.get("itemId");
                            String itemName = (String) itemObj.get("itemName");
                            Long quantity = (Long) itemObj.get("quantity");
                            
                            Material material = Material.getMaterial(itemId.toUpperCase());
                            if (material != null && quantity > 0) {
                                ItemStack itemStack = new ItemStack(material, Math.min(quantity.intValue(), 64));
                                ItemMeta meta = itemStack.getItemMeta();
                                if (meta != null) {
                                    meta.setDisplayName("§f" + itemName);
                                    meta.setLore(Arrays.asList(
                                        "§7수량: §e" + quantity,
                                        "§7좌클릭: §a1개 출금",
                                        "§7우클릭: §a64개 출금",
                                        "§7Shift+클릭: §a전체 출금"
                                    ));
                                    itemStack.setItemMeta(meta);
                                }
                                warehouse.setItem(slot++, itemStack);
                            }
                        }
                    }
                    
                    plugin.getLogger().info("플레이어 " + player.getName() + "의 창고 내용 업데이트 완료");
                });
            })
            .exceptionally(throwable -> {
                plugin.getLogger().warning("창고 내용 업데이트 실패: " + throwable.getMessage());
                return null;
            });
    }
}
