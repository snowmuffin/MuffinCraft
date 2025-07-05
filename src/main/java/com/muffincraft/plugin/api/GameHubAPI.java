package com.muffincraft.plugin.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.muffincraft.plugin.MuffinCraftPlugin;
import okhttp3.*;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GameHubAPI {
    private final MuffinCraftPlugin plugin;
    private final String baseUrl;
    private final OkHttpClient client;
    private final Gson gson;
    private String authToken;

    public GameHubAPI(MuffinCraftPlugin plugin, String baseUrl) {
        this.plugin = plugin;
        this.baseUrl = baseUrl;
        this.client = new OkHttpClient();
        this.gson = new GsonBuilder().create();

        // 설정에서 인증 토큰 로드
        this.authToken = plugin.getConfigManager().getString("api.token", "");
    }

    public CompletableFuture<Boolean> syncInventory(UUID playerId, ItemStack item) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("itemId", item.getType().toString());
            itemData.put("quantity", item.getAmount());
            itemData.put("metadata", serializeItemMetadata(item));

            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                gson.toJson(itemData)
            );

            Request request = new Request.Builder()
                .url(baseUrl + "/inventory/sync/" + playerId.toString())
                .post(body)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to sync inventory: " + e.getMessage());
                return false;
            }
        });
    }

    private Map<String, Object> serializeItemMetadata(ItemStack item) {
        Map<String, Object> metadata = new HashMap<>();
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) {
                metadata.put("displayName", item.getItemMeta().getDisplayName());
            }
            if (item.getItemMeta().hasLore()) {
                metadata.put("lore", item.getItemMeta().getLore());
            }
        }
        return metadata;
    }

    public CompletableFuture<Map<String, Object>> getInventory(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(baseUrl + "/inventory/" + playerId.toString())
                .get()
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new IOException("Failed to get inventory");
                }
                return gson.fromJson(response.body().string(), Map.class);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to get inventory: " + e.getMessage());
                return new HashMap<>();
            }
        });
    }

    public CompletableFuture<Double> getBalance(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(baseUrl + "/currency/balance/" + playerId.toString())
                .get()
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new IOException("Failed to get balance");
                }
                Map<String, Object> result = gson.fromJson(response.body().string(), Map.class);
                return ((Number) result.get("balance")).doubleValue();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to get balance: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Boolean> transferCurrency(UUID fromId, UUID toId, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> transferData = new HashMap<>();
            transferData.put("fromId", fromId.toString());
            transferData.put("toId", toId.toString());
            transferData.put("amount", amount);

            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                gson.toJson(transferData)
            );

            Request request = new Request.Builder()
                .url(baseUrl + "/currency/transfer")
                .post(body)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to transfer currency: " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> addCurrency(UUID playerId, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> addData = new HashMap<>();
            addData.put("playerId", playerId.toString());
            addData.put("amount", amount);

            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                gson.toJson(addData)
            );

            Request request = new Request.Builder()
                .url(baseUrl + "/currency/add")
                .post(body)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to add currency: " + e.getMessage());
                return false;
            }
        });
    }

    /**
     * 플레이어 토큰을 사용하여 인벤토리 동기화
     */
    public CompletableFuture<Boolean> syncInventoryWithPlayerToken(UUID playerId, ItemStack item, String playerToken) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("itemId", item.getType().toString());
            itemData.put("quantity", item.getAmount());
            itemData.put("metadata", serializeItemMetadata(item));

            RequestBody body = RequestBody.create(
                gson.toJson(itemData),
                MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                .url(baseUrl + "/muffincraft/inventory/sync")
                .post(body)
                .addHeader("Authorization", playerToken != null ? playerToken : "Bearer " + authToken)
                .addHeader("Content-Type", "application/json")
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    plugin.getLogger().info("인벤토리 동기화 성공: " + playerId + " - " + item.getType());
                    return true;
                } else {
                    plugin.getLogger().warning("인벤토리 동기화 실패: " + response.code() + " - " + response.message());
                    return false;
                }
            } catch (IOException e) {
                plugin.getLogger().severe("인벤토리 동기화 중 오류: " + e.getMessage());
                return false;
            }
        });
    }

    /**
     * 플레이어 토큰을 사용하여 통화 정보 조회
     */
    public CompletableFuture<String> getCurrencyWithPlayerToken(UUID playerId, String playerToken) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(baseUrl + "/muffincraft/currency")
                .get()
                .addHeader("Authorization", playerToken != null ? playerToken : "Bearer " + authToken)
                .addHeader("Content-Type", "application/json")
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    plugin.getLogger().info("통화 조회 성공: " + playerId);
                    return responseBody;
                } else {
                    plugin.getLogger().warning("통화 조회 실패: " + response.code() + " - " + response.message());
                    return null;
                }
            } catch (IOException e) {
                plugin.getLogger().severe("통화 조회 중 오류: " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * 플레이어 토큰을 사용하여 통화 업데이트
     */
    public CompletableFuture<Boolean> updateCurrencyWithPlayerToken(UUID playerId, String currencyType, int amount, String reason, String playerToken) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> currencyData = new HashMap<>();
            currencyData.put("currencyType", currencyType);
            currencyData.put("amount", amount);
            currencyData.put("reason", reason);

            RequestBody body = RequestBody.create(
                gson.toJson(currencyData),
                MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                .url(baseUrl + "/muffincraft/currency")
                .post(body)
                .addHeader("Authorization", playerToken != null ? playerToken : "Bearer " + authToken)
                .addHeader("Content-Type", "application/json")
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    plugin.getLogger().info("통화 업데이트 성공: " + playerId + " - " + currencyType + ": " + amount);
                    return true;
                } else {
                    plugin.getLogger().warning("통화 업데이트 실패: " + response.code() + " - " + response.message());
                    return false;
                }
            } catch (IOException e) {
                plugin.getLogger().severe("통화 업데이트 중 오류: " + e.getMessage());
                return false;
            }
        });
    }

    /**
     * 플레이어의 창고 아이템 목록을 가져옵니다
     */
    public CompletableFuture<org.json.simple.JSONArray> getWarehouseItems(UUID playerId, String authHeader) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = new Request.Builder()
                    .url(baseUrl + "/warehouse/my-warehouse")
                    .get()
                    .addHeader("Authorization", authHeader)
                    .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
                        return (org.json.simple.JSONArray) parser.parse(responseBody);
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("창고 아이템 조회 실패: " + e.getMessage());
            }
            return null;
        });
    }

    /**
     * 창고에 아이템을 입금합니다
     */
    public CompletableFuture<Boolean> depositWarehouseItem(UUID playerId, ItemStack item, int quantity, String authHeader) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("itemId", item.getType().toString());
                itemData.put("itemName", item.getType().name().toLowerCase().replace("_", " "));
                itemData.put("quantity", quantity);
                itemData.put("metadata", serializeItemMetadata(item));

                RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    gson.toJson(itemData)
                );

                Request request = new Request.Builder()
                    .url(baseUrl + "/warehouse/deposit")
                    .post(body)
                    .addHeader("Authorization", authHeader)
                    .build();

                try (Response response = client.newCall(request).execute()) {
                    return response.isSuccessful();
                }
            } catch (Exception e) {
                plugin.getLogger().warning("창고 입금 실패: " + e.getMessage());
                return false;
            }
        });
    }

    /**
     * 창고에서 아이템을 출금합니다
     */
    public CompletableFuture<Boolean> withdrawWarehouseItem(UUID playerId, String itemId, int quantity, String authHeader) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> withdrawData = new HashMap<>();
                withdrawData.put("itemId", itemId);
                withdrawData.put("quantity", quantity);

                RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    gson.toJson(withdrawData)
                );

                Request request = new Request.Builder()
                    .url(baseUrl + "/warehouse/withdraw")
                    .post(body)
                    .addHeader("Authorization", authHeader)
                    .build();

                try (Response response = client.newCall(request).execute()) {
                    return response.isSuccessful();
                }
            } catch (Exception e) {
                plugin.getLogger().warning("창고 출금 실패: " + e.getMessage());
                return false;
            }
        });
    }
}
