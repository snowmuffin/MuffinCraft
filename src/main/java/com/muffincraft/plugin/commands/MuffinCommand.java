package com.muffincraft.plugin.commands;

import com.muffincraft.plugin.MuffinCraftPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 머핀 아이템 관련 명령어 처리
 */
public class MuffinCommand implements CommandExecutor, TabCompleter {
    private final MuffinCraftPlugin plugin;

    public MuffinCommand(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give":
                handleGiveCommand(player, args);
                break;
            case "help":
                sendHelp(player);
                break;
            case "reload-resourcepack":
                handleReloadResourcePack(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    /**
     * 머핀 지급 명령어 처리
     */
    private void handleGiveCommand(Player player, String[] args) {
        // 권한 확인
        if (!player.hasPermission("muffincraft.admin.give")) {
            player.sendMessage("§c이 명령어를 사용할 권한이 없습니다.");
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§c사용법: /muffin give <수량>");
            return;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            
            if (amount <= 0) {
                player.sendMessage("§c수량은 1 이상이어야 합니다.");
                return;
            }

            if (amount > 64) {
                player.sendMessage("§c한 번에 최대 64개까지만 지급할 수 있습니다.");
                return;
            }

            // 머핀 아이템 생성
            ItemStack muffin = plugin.getCustomItemManager().createMuffin(amount);
            if (muffin == null) {
                player.sendMessage("§c머핀 아이템 생성에 실패했습니다.");
                return;
            }

            // 디버깅 정보 출력
            player.sendMessage("§7[DEBUG] 아이템 타입: " + muffin.getType());
            if (muffin.hasItemMeta() && muffin.getItemMeta().hasCustomModelData()) {
                player.sendMessage("§7[DEBUG] CustomModelData: " + muffin.getItemMeta().getCustomModelData());
            } else {
                player.sendMessage("§7[DEBUG] CustomModelData가 설정되지 않음!");
            }

            // 플레이어 인벤토리에 추가
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage("§c인벤토리에 공간이 부족합니다.");
                return;
            }

            player.getInventory().addItem(muffin);
            player.sendMessage("§a머핀 " + amount + "개를 지급받았습니다!");
            player.sendMessage("§e리소스팩이 적용되지 않으면 빵으로 표시됩니다.");
            
            plugin.getLogger().info(player.getName() + "에게 머핀 " + amount + "개를 지급했습니다.");

        } catch (NumberFormatException e) {
            player.sendMessage("§c올바른 숫자를 입력해주세요.");
        }
    }

    /**
     * 리소스팩 재로드 명령어 처리
     */
    private void handleReloadResourcePack(Player player) {
        // 권한 확인
        if (!player.hasPermission("muffincraft.admin.reload")) {
            player.sendMessage("§c이 명령어를 사용할 권한이 없습니다.");
            return;
        }

        player.sendMessage("§e리소스팩을 다시 적용합니다...");
        plugin.getResourcePackService().applyResourcePack(player);
    }

    /**
     * 도움말 메시지 전송
     */
    private void sendHelp(Player player) {
        player.sendMessage("§6=== 머핀 명령어 도움말 ===");
        player.sendMessage("§e/muffin help §7- 이 도움말을 봅니다");
        
        if (player.hasPermission("muffincraft.admin.give")) {
            player.sendMessage("§e/muffin give <수량> §7- 머핀 아이템을 지급합니다 (관리자 전용)");
        }
        
        if (player.hasPermission("muffincraft.admin.reload")) {
            player.sendMessage("§e/muffin reload-resourcepack §7- 리소스팩을 다시 적용합니다 (관리자 전용)");
        }
        
        player.sendMessage("§7머핀은 MuffinCraft의 공식 화폐입니다!");
        player.sendMessage("§7우클릭으로 사용하거나 상점에서 거래할 수 있습니다.");
        player.sendMessage("§c※ 리소스팩이 적용되지 않으면 빵으로 표시됩니다.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 첫 번째 인수: 하위 명령어
            List<String> subCommands = new ArrayList<>();
            subCommands.add("help");
            
            if (sender.hasPermission("muffincraft.admin.give")) {
                subCommands.add("give");
            }
            
            if (sender.hasPermission("muffincraft.admin.reload")) {
                subCommands.add("reload-resourcepack");
            }
            
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            // give 명령어의 두 번째 인수: 수량
            if (sender.hasPermission("muffincraft.admin.give")) {
                completions.addAll(Arrays.asList("1", "8", "16", "32", "64"));
            }
        }

        return completions;
    }
}
