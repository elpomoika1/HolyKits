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
import me.elpomoika.holykits.config.Config;
import me.elpomoika.holykits.util.InventoryUtil;
import me.elpomoika.holykits.util.cooldown.CooldownManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public final class HolyKits extends JavaPlugin {

    private InventoryUtil inventoryUtil;
    private CooldownManager cooldownManager;
    private Config defaultConfig;
    private PreviewMenu previewMenu;
    private Cache<@NotNull String, Map<Integer, ItemStack>> itemCache;
    private Cache<@NotNull String, Kit> kitCache;
    private CustomConfig customConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeCaches();
        init();

        cooldownManager.cleanExpiredCooldowns();

        getCommand("kit").setTabCompleter(new KitTabCompleter(this));
        getCommand("kit").setExecutor(new KitCommand(this));

        getServer().getPluginManager().registerEvents(new MenuListener(customConfig), this);
    }

    @Override
    public void onDisable() {
        cooldownManager.cleanExpiredCooldowns();
    }

    private void initializeCaches() {
        this.kitCache = Caffeine.newBuilder()
                .maximumSize(50)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
        this.itemCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
    }

    private void init() {
        this.customConfig = new CustomConfig(this);
        this.inventoryUtil = new InventoryUtil(this);
        this.defaultConfig = new Config(this);
        this.previewMenu = new PreviewMenu(defaultConfig);
        this.cooldownManager = new CooldownManager(this);
    }
}
