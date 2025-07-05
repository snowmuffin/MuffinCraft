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
}
