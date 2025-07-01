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
        if (args.length == 0) {
            sender.sendMessage(new String[] {
                "§6===== MuffinCraft Commands =====",
                "§e/" + label + " reload §7- 설정 파일을 다시 로드합니다",
                "§e/" + label + " sync §7- 인벤토리를 강제로 동기화합니다",
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

            default:
                sender.sendMessage("§c알 수 없는 명령어입니다. /" + label + " 를 입력하여 도움말을 확인하세요.");
                return true;
        }
    }
}
