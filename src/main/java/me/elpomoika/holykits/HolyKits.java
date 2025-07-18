package me.elpomoika.holykits;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import me.elpomoika.holykits.command.KitCommand;
import me.elpomoika.holykits.config.CustomConfig;
import me.elpomoika.holykits.menu.PreviewMenu;
import me.elpomoika.holykits.command.tabcomplete.KitTabCompleter;
import me.elpomoika.holykits.listener.MenuListener;
import me.elpomoika.holykits.model.Kit;
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

    private InventoryUtil inventoryUtil;
    private CooldownManager cooldownManager;
    private Config defaultConfig;
    private PreviewMenu previewMenu;
    private Cache<String, Map<Integer, ItemStack>> itemCache;
    private Cache<String, Kit> kitCache;
    private CustomConfig customConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.kitCache = Caffeine.newBuilder()
                .maximumSize(50)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
        this.itemCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
        this.customConfig = new CustomConfig(this);
        this.inventoryUtil = new InventoryUtil(this);
        this.defaultConfig = new Config(this);
        this.previewMenu = new PreviewMenu(defaultConfig);
        this.cooldownManager = new CooldownManager(this);

        cooldownManager.cleanExpiredCooldowns();

        getCommand("kit").setTabCompleter(new KitTabCompleter(this));
        getCommand("kit").setExecutor(new KitCommand(this));

        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
    }

    @Override
    public void onDisable() {
        cooldownManager.cleanExpiredCooldowns();
    }
}
