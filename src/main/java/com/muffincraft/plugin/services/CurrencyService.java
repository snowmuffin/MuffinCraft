package com.muffincraft.plugin.services;

import com.muffincraft.plugin.MuffinCraftPlugin;
import com.muffincraft.plugin.api.GameHubAPI;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CurrencyService {
    private final MuffinCraftPlugin plugin;
    private final GameHubAPI gameHubAPI;
    private final String currencySymbol;

    public CurrencyService(MuffinCraftPlugin plugin, GameHubAPI gameHubAPI) {
        this.plugin = plugin;
        this.gameHubAPI = gameHubAPI;
        this.currencySymbol = plugin.getConfig().getString("currency.symbol", "₩");
    }

    public void checkBalance(Player player) {
        UUID playerId = player.getUniqueId();
        
        // 플레이어 토큰 가져오기
        String authHeader = plugin.getAuthService().getAuthorizationHeader(player);
        
        if (authHeader == null) {
            player.sendMessage("§c인증 토큰이 없습니다. /muffincraft token 명령어로 토큰을 발급받으세요.");
            return;
        }
        
        gameHubAPI.getCurrencyWithPlayerToken(playerId, authHeader)
            .thenAccept(responseJson -> {
                if (responseJson != null) {
                    // JSON 파싱해서 잔액 표시 (향후 구체적인 파싱 로직 필요)
                    player.sendMessage("§a통화 정보를 성공적으로 조회했습니다.");
                    player.sendMessage("§7응답: " + responseJson);
                } else {
                    player.sendMessage("§c잔액 조회에 실패했습니다.");
                }
            })
            .exceptionally(e -> {
                player.sendMessage("§c잔액 조회 중 오류가 발생했습니다.");
                plugin.getLogger().warning("Failed to check balance for " + player.getName() + ": " + e.getMessage());
                return null;
            });
    }

    public void transferCurrency(Player from, Player to, double amount) {
        // 송신자 토큰 가져오기
        String fromAuthHeader = plugin.getAuthService().getAuthorizationHeader(from);
        
        if (fromAuthHeader == null) {
            from.sendMessage("§c인증 토큰이 없습니다. /muffincraft token 명령어로 토큰을 발급받으세요.");
            return;
        }

        // 새로운 플레이어 토큰 기반 전송 로직 (향후 구현)
        // 현재는 메시지만 표시
        from.sendMessage("§e통화 전송 기능은 플레이어 토큰 시스템으로 업그레이드 중입니다.");
        from.sendMessage("§7요청: " + from.getName() + " -> " + to.getName() + " : " + amount + currencySymbol);
        
        // gameHubAPI.transferCurrencyWithPlayerToken(fromId, toId, amount, fromAuthHeader)
        //     .thenAccept(success -> {
        //         if (success) {
        //             from.sendMessage(String.format("§a%s님에게 %s%,.0f을(를) 전송했습니다.",
        //                 to.getName(), currencySymbol, amount));
        //             to.sendMessage(String.format("§a%s님으로부터 %s%,.0f을(를) 받았습니다.",
        //                 from.getName(), currencySymbol, amount));
        //         } else {
        //             from.sendMessage("§c잔액이 부족합니다.");
        //         }
        //     })
        //     .exceptionally(e -> {
        //         from.sendMessage("§c재화 전송 중 오류가 발생했습니다.");
        //         plugin.getLogger().warning("Failed to transfer currency from " +
        //             from.getName() + " to " + to.getName() + ": " + e.getMessage());
        //         return null;
        //     });
    }

    public void addCurrency(Player player, double amount) {
        // 플레이어 토큰 가져오기
        String authHeader = plugin.getAuthService().getAuthorizationHeader(player);
        
        if (authHeader == null) {
            player.sendMessage("§c인증 토큰이 없습니다. /muffincraft token 명령어로 토큰을 발급받으세요.");
            return;
        }
        
        gameHubAPI.updateCurrencyWithPlayerToken(player.getUniqueId(), "coins", (int)amount, "Admin add currency", authHeader)
            .thenAccept(success -> {
                if (success) {
                    player.sendMessage(String.format("§a%s%,.0f이(가) 추가되었습니다.",
                        currencySymbol, amount));
                } else {
                    player.sendMessage("§c통화 추가에 실패했습니다.");
                }
            })
            .exceptionally(e -> {
                player.sendMessage("§c통화 추가 중 오류가 발생했습니다.");
                plugin.getLogger().warning("Failed to add currency for " + player.getName() + ": " + e.getMessage());
                return null;
            });
    }
}
