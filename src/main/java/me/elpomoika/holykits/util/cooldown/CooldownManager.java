package me.elpomoika.holykits.util.cooldown;

import me.elpomoika.holykits.HolyKits;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class CooldownManager {
    private final HolyKits plugin;
    private YamlConfiguration cooldownsConfig;
    private File cooldownsFile;

    public CooldownManager(HolyKits plugin) {
        this.plugin = plugin;
        setupCooldownsFile();
    }

    private void setupCooldownsFile() {
        cooldownsFile = new File(plugin.getDataFolder(), "cooldowns.yml");
        if (!cooldownsFile.exists()) {
            plugin.saveResource("cooldowns.yml", false);
        }
        cooldownsConfig = YamlConfiguration.loadConfiguration(cooldownsFile);
    }

    public void reloadCooldownConfig() throws IOException, InvalidConfigurationException {
        cooldownsConfig.load(cooldownsFile);
    }

    public void setCooldown(UUID playerId, String abilityName, long durationSeconds) {
        long endTime = System.currentTimeMillis() + (durationSeconds * 1000);
        cooldownsConfig.set(playerId + "." + abilityName, endTime);
        saveConfig();
    }

    public boolean hasCooldown(UUID playerId, String abilityName) {
        long endTime = cooldownsConfig.getLong(playerId + "." + abilityName, 0);
        return System.currentTimeMillis() < endTime;
    }

    public long getRemainingTime(UUID playerId, String abilityName) {
        long endTime = cooldownsConfig.getLong(playerId + "." + abilityName, 0);
        return Math.max(0, (endTime - System.currentTimeMillis()) / 1000);
    }

    public void cleanExpiredCooldowns() {
        for (String playerId : cooldownsConfig.getKeys(false)) {
            for (String ability : cooldownsConfig.getConfigurationSection(playerId).getKeys(false)) {
                long endTime = cooldownsConfig.getLong(playerId + "." + ability);
                if (System.currentTimeMillis() > endTime) {
                    cooldownsConfig.set(playerId + "." + ability, null);
                }
            }
        }
        saveConfig();
    }

    private void saveConfig() {
        try {
            cooldownsConfig.save(cooldownsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить cooldowns.yml: " + e.getMessage());
        }
    }
}
