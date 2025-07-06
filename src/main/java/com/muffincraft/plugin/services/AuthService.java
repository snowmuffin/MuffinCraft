package com.muffincraft.plugin.services;

import com.muffincraft.plugin.MuffinCraftPlugin;
import com.muffincraft.plugin.api.GameHubAPI;
import org.bukkit.entity.Player;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

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
    private final Gson gson = new Gson(); // Gson 인스턴스 추가

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
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("minecraftUsername", player.getName());
                requestBody.addProperty("minecraftUuid", player.getUniqueId().toString());

                // 요청 전송
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = gson.toJson(requestBody).getBytes(StandardCharsets.UTF_8);
                    os.write(input);
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
                    JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

                    if (jsonResponse.get("success").getAsBoolean()) {
                        String authCode = jsonResponse.get("authCode").getAsString();
                        String message = jsonResponse.has("message") ? jsonResponse.get("message").getAsString() : "";
                        return "§a인증 코드: §e" + authCode + "\n§7" + message;
                    } else {
                        String error = jsonResponse.has("message") ? jsonResponse.get("message").getAsString() : "알 수 없는 오류";
                        return "§c인증 코드 생성 실패: " + error;
                    }
                } else {
                    return "§c서버 오류: HTTP " + responseCode;
                }

            } catch (JsonSyntaxException e) {
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

                    JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

                    if (jsonResponse.get("success").getAsBoolean()) {
                        JsonObject data = jsonResponse.has("data") ? jsonResponse.get("data").getAsJsonObject() : null;
                        if (data != null && data.has("isLinked") && data.get("isLinked").getAsBoolean()) {
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

    /**
     * 플레이어 토큰을 생성하고 반환합니다 (연동 여부 무관).
     * @param player 토큰을 요청하는 플레이어
     * @return CompletableFuture<String> 생성된 토큰 또는 에러 메시지
     */
    public CompletableFuture<String> generatePlayerToken(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String apiUrl = baseUrl + "/player/token";
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // POST 요청 설정
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // 요청 바디 생성
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("minecraftUsername", player.getName());
                requestBody.addProperty("minecraftUuid", player.getUniqueId().toString());

                // 요청 전송
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = gson.toJson(requestBody).getBytes(StandardCharsets.UTF_8);
                    os.write(input);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

                    if (jsonResponse.get("success").getAsBoolean()) {
                        String token = jsonResponse.get("token").getAsString();
                        String expiresIn = jsonResponse.has("expiresIn") ? jsonResponse.get("expiresIn").getAsString() : "";
                        String message = jsonResponse.has("message") ? jsonResponse.get("message").getAsString() : "";
                        
                        JsonObject playerInfo = jsonResponse.has("player") ? jsonResponse.get("player").getAsJsonObject() : null;
                        boolean isLinked = playerInfo != null && playerInfo.has("isLinked") && playerInfo.get("isLinked").getAsBoolean();
                        
                        StringBuilder result = new StringBuilder();
                        result.append("§a토큰 발급 완료!\n");
                        result.append("§e토큰: §f").append(token.substring(0, 20)).append("...\n");
                        result.append("§e유효기간: §f").append(expiresIn).append("\n");
                        result.append("§e연동 상태: §f").append(isLinked ? "연동됨" : "비연동").append("\n");
                        result.append("§7").append(message);
                        
                        // 플레이어 데이터에 토큰 저장 (임시)
                        player.setMetadata("muffincraft_token", new org.bukkit.metadata.FixedMetadataValue(plugin, token));
                        
                        return result.toString();
                    } else {
                        String error = jsonResponse.has("error") ? jsonResponse.get("error").getAsString() : "알 수 없는 오류";
                        return "§c토큰 발급 실패: " + error;
                    }
                } else {
                    return "§c서버 오류: HTTP " + responseCode;
                }

            } catch (JsonSyntaxException e) {
                plugin.getLogger().warning("JSON 파싱 오류: " + e.getMessage());
                return "§c응답 처리 중 오류가 발생했습니다.";
            } catch (Exception e) {
                plugin.getLogger().severe("토큰 발급 중 오류: " + e.getMessage());
                return "§c토큰 발급 중 오류가 발생했습니다: " + e.getMessage();
            }
        });
    }

    /**
     * 플레이어 토큰 갱신 (기존 토큰이 만료되었거나 없을 때)
     * @param player 토큰을 갱신할 플레이어
     * @return CompletableFuture<Boolean> 갱신 성공 여부
     */
    public CompletableFuture<Boolean> refreshPlayerToken(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 기존 토큰 확인
                String existingToken = getPlayerToken(player);
                
                // 토큰이 있고 유효한지 확인
                if (existingToken != null && isTokenValid(existingToken)) {
                    plugin.getLogger().info("플레이어 " + player.getName() + "의 토큰이 여전히 유효합니다.");
                    return true;
                }
                
                // 토큰이 없거나 만료된 경우 새로 발급
                plugin.getLogger().info("플레이어 " + player.getName() + "의 토큰을 갱신합니다...");
                
                String apiUrl = baseUrl + "/player/token";
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // POST 요청 설정
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // 요청 바디 생성
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("minecraftUsername", player.getName());
                requestBody.addProperty("minecraftUuid", player.getUniqueId().toString());

                // 요청 전송
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = gson.toJson(requestBody).getBytes(StandardCharsets.UTF_8);
                    os.write(input);
                }

                int responseCode = connection.getResponseCode();
                // HTTP 200 (OK) 또는 201 (Created) 모두 성공으로 처리
                if (responseCode == 200 || responseCode == 201) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

                    if (jsonResponse.get("success").getAsBoolean()) {
                        String token = jsonResponse.get("token").getAsString();
                        
                        // 플레이어 데이터에 토큰 저장
                        player.setMetadata("muffincraft_token", new org.bukkit.metadata.FixedMetadataValue(plugin, token));
                        player.setMetadata("muffincraft_token_time", new org.bukkit.metadata.FixedMetadataValue(plugin, System.currentTimeMillis()));
                        
                        plugin.getLogger().info("플레이어 " + player.getName() + "의 토큰이 성공적으로 갱신되었습니다.");
                        return true;
                    }
                }
                
                plugin.getLogger().warning("플레이어 " + player.getName() + "의 토큰 갱신에 실패했습니다. HTTP " + responseCode);
                return false;

            } catch (Exception e) {
                plugin.getLogger().severe("토큰 갱신 중 오류: " + e.getMessage());
                return false;
            }
        });
    }

    /**
     * 플레이어의 저장된 토큰 가져오기
     * @param player 토큰을 가져올 플레이어
     * @return 저장된 토큰 또는 null
     */
    public String getPlayerToken(Player player) {
        if (player.hasMetadata("muffincraft_token")) {
            return player.getMetadata("muffincraft_token").get(0).asString();
        }
        return null;
    }

    /**
     * 토큰 유효성 간단 체크 (시간 기반)
     * @param token 확인할 토큰
     * @return 유효 여부
     */
    private boolean isTokenValid(String token) {
        // 간단한 시간 기반 체크 (실제로는 JWT 파싱이 더 정확하지만 플러그인에서는 간소화)
        // 토큰이 5시간 이상 된 경우 갱신
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }
            
            // 메타데이터에서 토큰 생성 시간 확인
            long tokenTime = 0;
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.hasMetadata("muffincraft_token") && 
                    token.equals(p.getMetadata("muffincraft_token").get(0).asString())) {
                    if (p.hasMetadata("muffincraft_token_time")) {
                        tokenTime = p.getMetadata("muffincraft_token_time").get(0).asLong();
                        break;
                    }
                }
            }
            
            if (tokenTime == 0) {
                return false; // 시간 정보가 없으면 무효로 처리
            }
            
            // 5시간 = 5 * 60 * 60 * 1000 밀리초
            long fiveHours = 5 * 60 * 60 * 1000L;
            return (System.currentTimeMillis() - tokenTime) < fiveHours;
            
        } catch (Exception e) {
            plugin.getLogger().warning("토큰 유효성 검사 중 오류: " + e.getMessage());
            return false;
        }
    }

    /**
     * 플레이어의 API 요청에 사용할 Authorization 헤더 값 가져오기
     * @param player API 요청을 보낼 플레이어
     * @return "Bearer TOKEN" 형태의 문자열 또는 null
     */
    public String getAuthorizationHeader(Player player) {
        String token = getPlayerToken(player);
        if (token != null && !token.isEmpty()) {
            return "Bearer " + token;
        }
        return null;
    }
}
