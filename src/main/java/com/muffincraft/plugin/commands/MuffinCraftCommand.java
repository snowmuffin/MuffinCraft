package com.muffincraft.plugin.commands;

import com.muffincraft.plugin.MuffinCraftPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Arrays;

public class MuffinCraftCommand implements CommandExecutor {
    private final MuffinCraftPlugin plugin;

    public MuffinCraftCommand(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 플레이어가 명령어를 실행하는 경우 토큰 자동 갱신
        if (sender instanceof Player) {
            Player player = (Player) sender;
            plugin.getAuthService().refreshPlayerToken(player);
        }
        
        if (args.length == 0) {
            sender.sendMessage(new String[] {
                "§6===== MuffinCraft Commands =====",
                "§e/" + label + " reload §7- 설정 파일을 다시 로드합니다",
                "§e/" + label + " sync §7- 인벤토리를 강제로 동기화합니다",
                "§e/" + label + " auth §7- 계정 연동용 인증 코드를 발급받습니다",
                "§e/" + label + " token §7- API 사용을 위한 플레이어 토큰을 발급받습니다",
                "§e/" + label + " status §7- 계정 연동 상태를 확인합니다",
                "§e/inventory §7- 온라인 인벤토리를 엽니다",
                "§e/balance §7- 재화 잔액을 확인합니다",
                "§e/balance send <플레이어> <금액> §7- 재화를 전송합니다"
            });
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("muffincraft.admin")) {
                    sender.sendMessage("§c이 명령어를 사용할 권한이 없습니다.");
                    return true;
                }
                plugin.getConfigManager().reload();
                sender.sendMessage("§a설정 파일을 다시 로드했습니다.");
                return true;

            case "sync":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
                    return true;
                }
                Player player = (Player) sender;
                plugin.getInventoryService().handleInventoryChange(player, Arrays.asList(player.getInventory().getContents()));
                sender.sendMessage("§a인벤토리 동기화를 시작했습니다.");
                return true;

            case "auth":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
                    return true;
                }
                Player authPlayer = (Player) sender;
                sender.sendMessage("§e인증 코드를 생성하고 있습니다...");

                plugin.getAuthService().generateAuthCode(authPlayer).thenAccept(result -> {
                    authPlayer.sendMessage("§6===== 계정 연동 인증 코드 =====");
                    authPlayer.sendMessage(result);
                    if (result.contains("§a")) { // 성공한 경우
                        authPlayer.sendMessage("§7웹사이트에서 이 코드를 입력하여 계정을 연동하세요.");
                        authPlayer.sendMessage("§7인증 코드는 10분간 유효합니다.");
                    }
                }).exceptionally(throwable -> {
                    authPlayer.sendMessage("§c인증 코드 생성 중 오류가 발생했습니다.");
                    plugin.getLogger().severe("Auth code generation error: " + throwable.getMessage());
                    return null;
                });
                return true;

            case "status":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
                    return true;
                }
                Player statusPlayer = (Player) sender;
                sender.sendMessage("§e계정 연동 상태를 확인하고 있습니다...");

                plugin.getAuthService().getAccountStatus(statusPlayer).thenAccept(result -> {
                    statusPlayer.sendMessage("§6===== 계정 연동 상태 =====");
                    statusPlayer.sendMessage(result);
                }).exceptionally(throwable -> {
                    statusPlayer.sendMessage("§c계정 상태 확인 중 오류가 발생했습니다.");
                    plugin.getLogger().severe("Account status check error: " + throwable.getMessage());
                    return null;
                });
                return true;

            case "token":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
                    return true;
                }
                Player tokenPlayer = (Player) sender;
                sender.sendMessage("§e플레이어 토큰을 발급하고 있습니다...");

                plugin.getAuthService().generatePlayerToken(tokenPlayer).thenAccept(result -> {
                    tokenPlayer.sendMessage("§6===== 플레이어 토큰 발급 =====");
                    tokenPlayer.sendMessage(result);
                    if (result.contains("§a")) { // 성공한 경우
                        tokenPlayer.sendMessage("§7이 토큰으로 API를 사용할 수 있습니다.");
                        tokenPlayer.sendMessage("§7토큰은 연동 상태에 따라 6-24시간 유효합니다.");
                    }
                }).exceptionally(throwable -> {
                    tokenPlayer.sendMessage("§c토큰 발급 중 오류가 발생했습니다.");
                    plugin.getLogger().severe("Player token generation error: " + throwable.getMessage());
                    return null;
                });
                return true;

            default:
                sender.sendMessage("§c알 수 없는 명령어입니다. /" + label + " 를 입력하여 도움말을 확인하세요.");
                return true;
        }
    }
}
