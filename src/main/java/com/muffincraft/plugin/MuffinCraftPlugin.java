package com.muffincraft.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import com.muffincraft.plugin.listeners.PlayerListener;
import com.muffincraft.plugin.services.CurrencyService;
import com.muffincraft.plugin.services.AuthService;
import com.muffincraft.plugin.services.WarehouseService;
import com.muffincraft.plugin.services.ResourcePackService;
import com.muffincraft.plugin.items.CustomItemManager;
import com.muffincraft.plugin.api.GameHubAPI;
import com.muffincraft.plugin.config.Config;
import com.muffincraft.plugin.commands.MuffinCraftCommand;
import com.muffincraft.plugin.commands.BalanceCommand;
import com.muffincraft.plugin.commands.WarehouseCommand;
import com.muffincraft.plugin.listeners.WarehouseListener;
import com.muffincraft.plugin.commands.MuffinCommand;
import com.muffincraft.plugin.listeners.CustomItemListener;
import lombok.Getter;

public class MuffinCraftPlugin extends JavaPlugin {
    private Config configManager;
    @Getter private GameHubAPI gameHubAPI;
    @Getter private CurrencyService currencyService;
    @Getter private AuthService authService;
    @Getter private WarehouseService warehouseService;
    @Getter private ResourcePackService resourcePackService;
    @Getter private CustomItemManager customItemManager;

    @Override
    public void onEnable() {
        // 설정 파일 로드
        saveDefaultConfig();
        this.configManager = new Config(this);

        // API 초기화
        gameHubAPI = new GameHubAPI(this, configManager.getApiUrl());

        currencyService = new CurrencyService(this, gameHubAPI);
        authService = new AuthService(this, gameHubAPI);
        warehouseService = new WarehouseService(this, gameHubAPI);  // 새로운 창고 시스템
        
        // 커스텀 아이템 매니저 초기화
        customItemManager = new CustomItemManager(this);
        
        // 리소스팩 서비스 초기화
        resourcePackService = new ResourcePackService(this);
        resourcePackService = new ResourcePackService(this); // 리소스 팩 서비스
        customItemManager = new CustomItemManager(this); // 커스텀 아이템 매니저

        // 이벤트 리스너 등록
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new WarehouseListener(this), this);  // 새로운 창고 리스너
        getServer().getPluginManager().registerEvents(new CustomItemListener(this), this);  // 커스텀 아이템 리스너
        getServer().getPluginManager().registerEvents(new CustomItemListener(this), this); // 커스텀 아이템 리스너

        // 명령어 등록
        getCommand("muffincraft").setExecutor(new MuffinCraftCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("warehouse").setExecutor(new WarehouseCommand(this, warehouseService));  // 새로운 창고 명령어
        getCommand("muffin").setExecutor(new MuffinCommand(this));  // 머핀 명령어
        getCommand("muffin").setExecutor(new MuffinCommand(this)); // 새로운 머핀 명령어

        getLogger().info("MuffinCraft Plugin has been enabled!");
        getLogger().info("외부 창고 시스템이 활성화되었습니다. /warehouse 명령어를 사용하세요!");
        getLogger().info("커스텀 아이템 시스템이 활성화되었습니다!");
        getLogger().info("리소스팩 자동 다운로드가 활성화되었습니다!");
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
