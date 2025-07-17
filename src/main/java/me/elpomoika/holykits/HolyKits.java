package me.elpomoika.holykits;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import me.elpomoika.holykits.command.KitCommand;
import me.elpomoika.holykits.menu.PreviewMenu;
import me.elpomoika.holykits.command.tabcomplete.KitTabCompleter;
import me.elpomoika.holykits.listener.MenuListener;
import me.elpomoika.holykits.util.Config;
import me.elpomoika.holykits.util.InventoryUtil;
import me.elpomoika.holykits.util.cooldown.CooldownManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public final class HolyKits extends JavaPlugin {

    private File customConfigFile;
    private FileConfiguration customConfig;
    private InventoryUtil inventoryUtil;
    private CooldownManager cooldownManager;
    private Config defaultConfig;
    private PreviewMenu previewMenu;
    private Cache<String, Map<Integer, ItemStack>> itemCache;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.itemCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
        this.defaultConfig = new Config(this);
        this.previewMenu = new PreviewMenu(defaultConfig);
        this.cooldownManager = new CooldownManager(this);
        this.inventoryUtil = new InventoryUtil(this);

        createCustomConfig();
        cooldownManager.cleanExpiredCooldowns();

        getCommand("kit").setTabCompleter(new KitTabCompleter(this));
        getCommand("kit").setExecutor(new KitCommand(this));

        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
    }

    @Override
    public void onDisable() {
        cooldownManager.cleanExpiredCooldowns();
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "kits.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("kits.yml", false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Can't create custom defaultConfig file " + e.getMessage());
        }
    }
}
