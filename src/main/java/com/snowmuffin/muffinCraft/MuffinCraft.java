package com.snowmuffin.muffincraft;

import com.snowmuffin.muffincraft.commands.InventoryCommand;
import com.snowmuffin.muffincraft.config.Config;
import com.snowmuffin.muffincraft.service.BackendService;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class MuffinCraft extends JavaPlugin {
    @Getter
    private static MuffinCraft instance;
    @Getter
    private BackendService backendService;
    @Getter
    private Config config;

    @Override
    public void onEnable() {
        instance = this;

        // Config 초기화
        saveDefaultConfig();
        this.config = new Config(this);

        // 백엔드 서비스 초기화
        this.backendService = new BackendService(this);

        // 커맨드 등록
        getCommand("inventory").setExecutor(new InventoryCommand(this));

        getLogger().info("MuffinCraft 플러그인이 활성화되었습니다!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MuffinCraft 플러그인이 비활성화되었습니다!");
    }
}
