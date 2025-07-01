package com.muffincraft.plugin.services;

import com.muffincraft.plugin.MuffinCraftPlugin;
import com.muffincraft.plugin.api.GameHubAPI;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
        gameHubAPI.getBalance(playerId)
            .thenAccept(balance -> {
                player.sendMessage(String.format("§a현재 잔액: %s%,.0f", currencySymbol, balance));
            })
            .exceptionally(e -> {
                player.sendMessage("§c잔액 조회 중 오류가 발생했습니다.");
                plugin.getLogger().warning("Failed to check balance for " + player.getName() + ": " + e.getMessage());
                return null;
            });
    }

    public void transferCurrency(Player from, Player to, double amount) {
        UUID fromId = from.getUniqueId();
        UUID toId = to.getUniqueId();

        gameHubAPI.transferCurrency(fromId, toId, amount)
            .thenAccept(success -> {
                if (success) {
                    from.sendMessage(String.format("§a%s님에게 %s%,.0f을(를) 전송했습니다.",
                        to.getName(), currencySymbol, amount));
                    to.sendMessage(String.format("§a%s님으로부터 %s%,.0f을(를) 받았습니다.",
                        from.getName(), currencySymbol, amount));
                } else {
                    from.sendMessage("§c잔액이 부족합니다.");
                }
            })
            .exceptionally(e -> {
                from.sendMessage("§c재화 전송 중 오류가 발생했습니다.");
                plugin.getLogger().warning("Failed to transfer currency from " +
                    from.getName() + " to " + to.getName() + ": " + e.getMessage());
                return null;
            });
    }

    public void addCurrency(Player player, double amount) {
        gameHubAPI.addCurrency(player.getUniqueId(), amount)
            .thenAccept(success -> {
                if (success) {
                    player.sendMessage(String.format("§a%s%,.0f이(가) 추가되었습니다.",
                        currencySymbol, amount));
                }
            })
            .exceptionally(e -> {
                plugin.getLogger().warning("Failed to add currency for " +
                    player.getName() + ": " + e.getMessage());
                return null;
            });
    }
}
