package com.muffincraft.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import com.muffincraft.plugin.listeners.PlayerListener;
import com.muffincraft.plugin.services.InventoryService;
import com.muffincraft.plugin.services.CurrencyService;
import com.muffincraft.plugin.api.GameHubAPI;
import com.muffincraft.plugin.config.Config;
import com.muffincraft.plugin.commands.InventoryCommand;
import com.muffincraft.plugin.commands.MuffinCraftCommand;
import com.muffincraft.plugin.commands.BalanceCommand;
import com.muffincraft.plugin.listeners.InventoryListener;
import lombok.Getter;

public class MuffinCraftPlugin extends JavaPlugin {
    private Config configManager;
    @Getter private GameHubAPI gameHubAPI;
    @Getter private InventoryService inventoryService;
    @Getter private CurrencyService currencyService;

    @Override
    public void onEnable() {
        // 설정 파일 로드
        saveDefaultConfig();
        this.configManager = new Config(this);

        // API 초기화
        gameHubAPI = new GameHubAPI(this, configManager.getApiUrl());

        // 서비스 초기화
        inventoryService = new InventoryService(this, gameHubAPI);
        currencyService = new CurrencyService(this, gameHubAPI);

        // 이벤트 리스너 등록
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

        // 명령어 등록
        getCommand("muffincraft").setExecutor(new MuffinCraftCommand(this));
        getCommand("inventory").setExecutor(new InventoryCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));

        getLogger().info("MuffinCraft Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MuffinCraft Plugin has been disabled!");
    }

    @Override
    public FileConfiguration getConfig() {
        return super.getConfig();
    }

    public Config getConfigManager() {
        return configManager;
    }
}
