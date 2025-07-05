package com.muffincraft.plugin.services;

import com.muffincraft.plugin.MuffinCraftPlugin;
import com.muffincraft.plugin.api.GameHubAPI;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private final MuffinCraftPlugin plugin;
    private final String baseUrl;

    public AuthService(MuffinCraftPlugin plugin, GameHubAPI gameHubAPI) {
        this.plugin = plugin;
        this.baseUrl = plugin.getConfigManager().getApiUrl();
    }

    /**
     * 플레이어의 인증 코드를 생성하고 반환합니다.
     * @param player 인증 코드를 요청하는 플레이어
     * @return CompletableFuture<String> 생성된 인증 코드 또는 에러 메시지
     */
    public CompletableFuture<String> generateAuthCode(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String apiUrl = baseUrl + "/auth/generate-code";
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // POST 요청 설정
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // 요청 바디 생성
                JSONObject requestBody = new JSONObject();
                requestBody.put("minecraftUsername", player.getName());
                requestBody.put("minecraftUuid", player.getUniqueId().toString());

                // 요청 전송
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // 응답 처리
                int responseCode = connection.getResponseCode();
                if (responseCode == 200 || responseCode == 201) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // JSON 응답 파싱
                    JSONParser parser = new JSONParser();
                    JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());

                    if ((Boolean) jsonResponse.get("success")) {
                        String authCode = (String) jsonResponse.get("authCode");
                        String message = (String) jsonResponse.get("message");
                        return "§a인증 코드: §e" + authCode + "\n§7" + (message != null ? message : "");
                    } else {
                        String error = (String) jsonResponse.get("message");
                        return "§c인증 코드 생성 실패: " + (error != null ? error : "알 수 없는 오류");
                    }
                } else {
                    return "§c서버 오류: HTTP " + responseCode;
                }

            } catch (ParseException e) {
                plugin.getLogger().warning("JSON 파싱 오류: " + e.getMessage());
                return "§c응답 처리 중 오류가 발생했습니다.";
            } catch (Exception e) {
                plugin.getLogger().severe("인증 코드 생성 중 오류: " + e.getMessage());
                return "§c인증 코드 생성 중 오류가 발생했습니다: " + e.getMessage();
            }
        });
    }

    /**
     * 플레이어의 계정 연동 상태를 확인합니다.
     * @param player 상태를 확인할 플레이어
     * @return CompletableFuture<String> 연동 상태 메시지
     */
    public CompletableFuture<String> getAccountStatus(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String apiUrl = baseUrl + "/auth/player/" + player.getName();
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONParser parser = new JSONParser();
                    JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());

                    if ((Boolean) jsonResponse.get("success")) {
                        JSONObject data = (JSONObject) jsonResponse.get("data");
                        if (data != null && data.get("isLinked") != null && (Boolean) data.get("isLinked")) {
                            return "§a계정이 연동되어 있습니다.";
                        } else {
                            return "§e계정이 연동되지 않았습니다. §7/muffincraft auth §e명령어로 인증 코드를 발급받으세요.";
                        }
                    } else {
                        return "§e계정이 연동되지 않았습니다. §7/muffincraft auth §e명령어로 인증 코드를 발급받으세요.";
                    }
                } else if (responseCode == 404) {
                    return "§e계정이 연동되지 않았습니다. §7/muffincraft auth §e명령어로 인증 코드를 발급받으세요.";
                } else {
                    return "§c서버 오류: HTTP " + responseCode;
                }

            } catch (Exception e) {
                plugin.getLogger().severe("계정 상태 확인 중 오류: " + e.getMessage());
                return "§c계정 상태 확인 중 오류가 발생했습니다: " + e.getMessage();
            }
        });
    }
}
