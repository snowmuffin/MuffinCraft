package com.muffincraft.plugin.commands;

import com.muffincraft.plugin.MuffinCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {
    private final MuffinCraftPlugin plugin;

    public BalanceCommand(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // 잔액 확인
            plugin.getCurrencyService().checkBalance(player);
            return true;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("send")) {
            // 다른 플레이어에게 재화 전송
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
                return true;
            }

            try {
                double amount = Double.parseDouble(args[2]);
                if (amount <= 0) {
                    sender.sendMessage("§c0보다 큰 금액을 입력해주세요.");
                    return true;
                }

                plugin.getCurrencyService().transferCurrency(player, target, amount);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c올바른 금액을 입력해주세요.");
            }
            return true;
        }

        sender.sendMessage("§c사용법: /" + label + " [send <플레이어> <금액>]");
        return true;
    }
}
