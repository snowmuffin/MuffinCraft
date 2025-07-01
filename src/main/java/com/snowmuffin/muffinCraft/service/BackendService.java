package com.snowmuffin.muffincraft.service;

import com.google.gson.Gson;
import com.snowmuffin.muffincraft.MuffinCraft;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class BackendService {
    private final MuffinCraft plugin;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private static final String API_URL = "http://localhost:3000"; // config로 이동 예정

    public CompletableFuture<List<Map<String, Object>>> getPlayerInventory(Player player, String inventoryType) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(API_URL + "/api/inventory/" + player.getUniqueId() + "?type=" + inventoryType)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("API 요청 실패: " + response);

                String json = response.body().string();
                return gson.fromJson(json, List.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> updatePlayerInventory(Player player, String inventoryType, List<Map<String, Object>> items) {
        return CompletableFuture.supplyAsync(() -> {
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                gson.toJson(items)
            );

            Request request = new Request.Builder()
                    .url(API_URL + "/api/inventory/" + player.getUniqueId() + "?type=" + inventoryType)
                    .put(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}
