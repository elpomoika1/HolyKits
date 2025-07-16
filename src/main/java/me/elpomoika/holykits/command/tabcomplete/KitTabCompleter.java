package me.elpomoika.holykits.command.tabcomplete;

import me.elpomoika.holykits.HolyKits;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class KitTabCompleter implements TabCompleter {
    private static final String[] COMMANDS = {"create", "remove", "get", "preview", "cooldown", "reload", "give"};
    private static final String[] NO_PERM_COMMAND = {"Используйте: /kit <название кита>"};
    private final HolyKits plugin;

    public KitTabCompleter(HolyKits plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        final List<String> completions = new ArrayList<>();

        if (player.hasPermission("holykits.admin")) {
            handleAdminCompletions(args, completions);
        } else {
            handlePlayerCompletions(player, args, completions);
        }

        return completions;
    }

    private void handleAdminCompletions(String[] args, List<String> completions) {
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), completions);
            return;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "create" -> {
                if (args.length == 2) completions.add("<название кита>");
            }
            case "get", "remove", "preview" -> {
                if (args.length == 2) completions.addAll(getAllKitsName());
            }
            case "cooldown" -> {
                if (args.length == 2) completions.addAll(getAllKitsName());
                else if (args.length == 3) completions.add("<длительность>");
            }
            case "give" -> {
                if (args.length == 2) completions.addAll(getOnlinePlayers());
                else if (args.length == 3) completions.addAll(getAllKitsName());
            }
        }
    }

    private void handlePlayerCompletions(Player player, String[] args, List<String> completions) {
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList(NO_PERM_COMMAND), completions);
        }

        completions.addAll(getAvailableKits(player));
    }

    private Set<String> getAvailableKits(Player player) {
        ConfigurationSection kitsSection = plugin.getCustomConfig().getConfigurationSection("kits");
        if (kitsSection == null) return Collections.emptySet();

        return kitsSection.getKeys(false).stream()
                .filter(kit -> player.hasPermission("holykits.use." + kit))
                .collect(Collectors.toSet());
    }

    private Set<String> getAllKitsName() {
        ConfigurationSection kitsSection = plugin.getCustomConfig().getConfigurationSection("kits");
        return kitsSection != null ? kitsSection.getKeys(false) : Collections.emptySet();
    }

    private Set<String> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toSet());
    }
}
