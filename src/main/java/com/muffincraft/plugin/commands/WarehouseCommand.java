package com.muffincraft.plugin.commands;

import com.muffincraft.plugin.MuffinCraftPlugin;
import com.muffincraft.plugin.services.WarehouseService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarehouseCommand implements CommandExecutor {
    private final WarehouseService warehouseService;

    public WarehouseCommand(MuffinCraftPlugin plugin, WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // 창고 GUI 열기
            warehouseService.openWarehouseGUI(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
            case "도움말":
                sendHelpMessage(player);
                break;
            case "list":
            case "목록":
                warehouseService.showWarehouseList(player);
                break;
            case "deposit":
            case "입금":
                if (args.length < 2) {
                    player.sendMessage("§c사용법: /warehouse deposit <수량>");
                    return true;
                }
                try {
                    int quantity = Integer.parseInt(args[1]);
                    warehouseService.depositItemFromHand(player, quantity);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c올바른 숫자를 입력해주세요.");
                }
                break;
            case "withdraw":
            case "출금":
                if (args.length < 3) {
                    player.sendMessage("§c사용법: /warehouse withdraw <아이템ID> <수량>");
                    return true;
                }
                try {
                    String itemId = args[1];
                    int quantity = Integer.parseInt(args[2]);
                    warehouseService.withdrawItem(player, itemId, quantity);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c올바른 숫자를 입력해주세요.");
                }
                break;
            default:
                sendHelpMessage(player);
                break;
        }

        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("§6=== MuffinCraft 외부 창고 ===");
        player.sendMessage("§e/warehouse §7- 창고 GUI 열기");
        player.sendMessage("§e/warehouse list §7- 창고 아이템 목록 보기");
        player.sendMessage("§e/warehouse deposit <수량> §7- 손에 든 아이템 입금");
        player.sendMessage("§e/warehouse withdraw <아이템ID> <수량> §7- 아이템 출금");
        player.sendMessage("§e/warehouse help §7- 이 도움말 보기");
        player.sendMessage("§7창고는 게임과 웹사이트에서 공유됩니다.");
    }
}
