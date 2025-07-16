package me.elpomoika.holykits.util;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class Config {

    private final JavaPlugin plugin;
    @Getter
    private final FileConfiguration config;

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void send(Player player, String path) {
        send(player, "messages." + path, Map.of());
    }

    public void send(Player player, String path, Map<String, String> placeholders) {
        String rawMessage = config.getString(path, "");
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            rawMessage = rawMessage.replace(entry.getKey(), entry.getValue());
        }
        String coloredMessage = ChatColor.translateAlternateColorCodes('&', rawMessage);
        player.sendMessage(coloredMessage);
    }

    public String formatTime(long totalSeconds) {
        if (totalSeconds <= 0) return "0 сек.";

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder builder = new StringBuilder();

        if (hours > 0) builder.append(hours).append(" ч. ");
        if (minutes > 0 || hours > 0) builder.append(minutes).append(" мин. ");
        builder.append(seconds).append(" сек.");

        return builder.toString().trim();
    }
}
