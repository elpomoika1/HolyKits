package me.elpomoika.holykits.config;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class Config {
    @Getter(AccessLevel.NONE)
    private final FileConfiguration config;

    public Config(JavaPlugin plugin) {
        this.config = plugin.getConfig();
        parseMessages();
        parseMenu();
    }

    private void parseMenu() {
        final var menuSection = config.getConfigurationSection("menu");
        if (menuSection == null) throw new IllegalStateException("menu section in config.yml is null");

        this.glassMaterial = menuSection.getString("glass-material");
        this.glassName = menuSection.getString("glass-name");

        final var giveKitItemSection = menuSection.getConfigurationSection("give-kit-item");
        if (giveKitItemSection == null) throw new IllegalStateException("menu.give-kit-item section in config.yml is null");

        this.giveKitItemMaterial = giveKitItemSection.getString("material");
        this.giveKitItemName = giveKitItemSection.getString("name");
        this.giveKitItemLore = giveKitItemSection.getStringList("lore");
    }

    private String glassMaterial;
    private String glassName;

    private String giveKitItemMaterial;
    private String giveKitItemName;
    private List<String> giveKitItemLore;

    private void parseMessages() {
        final var messageSection = config.getConfigurationSection("messages");
        if (messageSection == null) throw new IllegalStateException("messages section in config.yml is null");

        this.kitNameIsEmpty = messageSection.getString("kit-name-is-empty");
        this.kitRemoved = messageSection.getString("kit-removed");
        this.kitNotFound = messageSection.getString("kit-not-found");
        this.kitAlreadyExists = messageSection.getString("kit-already-exists");
        this.kitSuccessGot = messageSection.getString("kit-success-got");
        this.kitRemainingTime = messageSection.getString("kit-remaining-time");
        this.kitNoPermission = messageSection.getString("kit-no-permission");
        this.kitGiveSucceed = messageSection.getString("kit-give-succeed");
        this.playerIsOffline = messageSection.getString("player-is-offline");
    }

    private String kitNameIsEmpty;
    private String kitRemoved;
    private String kitNotFound;
    private String kitAlreadyExists;
    private String kitSuccessGot;
    private String kitRemainingTime;
    private String kitNoPermission;
    private String kitGiveSucceed;
    private String playerIsOffline;
}
