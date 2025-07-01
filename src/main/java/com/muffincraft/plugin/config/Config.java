package com.muffincraft.plugin.config;

import com.muffincraft.plugin.MuffinCraftPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import lombok.Getter;

public class Config {
    private final MuffinCraftPlugin plugin;
    private FileConfiguration config;
    @Getter private final String apiUrl;
    @Getter private final String apiToken;

    public Config(MuffinCraftPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();

        this.apiUrl = config.getString("api.url", "http://localhost:3000");
        this.apiToken = config.getString("api.token", "");
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
