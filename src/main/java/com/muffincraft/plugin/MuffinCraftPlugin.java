package com.muffincraft.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import com.muffincraft.plugin.listeners.PlayerListener;
import com.muffincraft.plugin.services.InventoryService;
import com.muffincraft.plugin.services.CurrencyService;
import com.muffincraft.plugin.services.AuthService;
import com.muffincraft.plugin.services.WarehouseService;
import com.muffincraft.plugin.api.GameHubAPI;
import com.muffincraft.plugin.config.Config;
import com.muffincraft.plugin.commands.InventoryCommand;
import com.muffincraft.plugin.commands.MuffinCraftCommand;
import com.muffincraft.plugin.commands.BalanceCommand;
import com.muffincraft.plugin.commands.WarehouseCommand;
import com.muffincraft.plugin.listeners.InventoryListener;
import com.muffincraft.plugin.listeners.WarehouseListener;
import lombok.Getter;

public class MuffinCraftPlugin extends JavaPlugin {
    private Config configManager;
    @Getter private GameHubAPI gameHubAPI;
    @Getter private InventoryService inventoryService;
    @Getter private CurrencyService currencyService;
    @Getter private AuthService authService;
    @Getter private WarehouseService warehouseService;

    @Override
    public void onEnable() {
        // 설정 파일 로드
        saveDefaultConfig();
        this.configManager = new Config(this);

        // API 초기화
        gameHubAPI = new GameHubAPI(this, configManager.getApiUrl());

        // 서비스 초기화
        inventoryService = new InventoryService(this, gameHubAPI);  // Deprecated
        currencyService = new CurrencyService(this, gameHubAPI);
        authService = new AuthService(this, gameHubAPI);
        warehouseService = new WarehouseService(this, gameHubAPI);  // 새로운 창고 시스템

        // 이벤트 리스너 등록
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);  // Deprecated
        getServer().getPluginManager().registerEvents(new WarehouseListener(this), this);  // 새로운 창고 리스너

        // 명령어 등록
        getCommand("muffincraft").setExecutor(new MuffinCraftCommand(this));
        getCommand("inventory").setExecutor(new InventoryCommand(this));  // Deprecated
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("warehouse").setExecutor(new WarehouseCommand(this, warehouseService));  // 새로운 창고 명령어

        getLogger().info("MuffinCraft Plugin has been enabled!");
        getLogger().info("외부 창고 시스템이 활성화되었습니다. /warehouse 명령어를 사용하세요!");
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
