package me.elpomoika.holykits.config;

import lombok.Getter;
import me.elpomoika.holykits.util.InventoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

@Getter
public class CustomConfig {
    private final JavaPlugin plugin;

    private File file;
    private FileConfiguration config;

    public CustomConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        initConfigFile();
        reload();

        if (config == null) {
            throw new IllegalStateException("Error kits.yml parse");
        }
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }

    public long getLong(String path, long def) {
        return config.getLong(path, def);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isKitExists(String kitName) {
        var kitSection = config.getConfigurationSection("kits");

        if (kitName.equalsIgnoreCase(String.valueOf(kitSection))) {
            return true;
        }
        return false;
    }

    public boolean isKitNameEmpty(String kitName) {
        return kitName.isEmpty();
    }

    public void reload() {
        if (file == null) return;

        config = YamlConfiguration.loadConfiguration(file);

        InputStream stream = plugin.getResource("kits.yml");
        if (stream == null) return;

        Reader reader = new InputStreamReader(stream);
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);

        config.setDefaults(defaultConfig);
    }

    private void initConfigFile() {
        if (file == null) {
            this.file = new File(plugin.getDataFolder()+"/kits.yml");
        }
        if (!file.exists()) {
            plugin.saveResource("kits.yml", false);
        }
    }
}
