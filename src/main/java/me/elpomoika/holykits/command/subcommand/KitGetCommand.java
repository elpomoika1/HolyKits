package me.elpomoika.holykits.command.subcommand;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.command.subcommand.model.SubCommand;
import me.elpomoika.holykits.model.Kit;
import me.elpomoika.holykits.util.Config;
import me.elpomoika.holykits.util.InventoryUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class KitGetCommand implements SubCommand {

    public static Cache<String, Kit> kitCache;
    private final HolyKits plugin;
    private final InventoryUtil inventoryUtil;
    private final Config config;

    public KitGetCommand(HolyKits plugin) {
        this.plugin = plugin;
        this.inventoryUtil = plugin.getInventoryUtil();
        this.config = plugin.getDefaultConfig();

        kitCache = Caffeine.newBuilder()
                .maximumSize(50)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }

    @Override
    public String getUsage() {
        return "/kit get <название>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько игроки могут использовать киты!");
            return;
        }

        String inputKitName = args[0];
        ConfigurationSection kitsSection = plugin.getCustomConfig().getConfigurationSection("kits");

        if (kitsSection == null || kitsSection.getKeys(false).isEmpty()) {
            player.sendMessage("§cНет доступных китов!");
            return;
        }

        for (String kitName : kitsSection.getKeys(false)) {
            if (inputKitName.equalsIgnoreCase(kitName)) {

                if (!player.hasPermission("holykits.use." + kitName)) {
                    config.send(player, "kit-no-permission");
                    return;
                }

                long cooldownSeconds = plugin.getCustomConfig().getLong("kits." + kitName + ".cooldown", 0);

                if (cooldownSeconds > 0) {
                    String cooldownKey = "kit_" + kitName;

                    if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), cooldownKey)) {
                        long remaining = plugin.getCooldownManager().getRemainingTime(player.getUniqueId(), cooldownKey);
                        String formatTime = config.formatTime(remaining);
                        config.send(player, "kit-remaining-time", Map.of("%remaining%", formatTime));
                        return;
                    }

                    plugin.getCooldownManager().setCooldown(player.getUniqueId(), cooldownKey, cooldownSeconds);
                }

                Kit kit = kitCache.get(kitName, k -> {
                    Kit data = new Kit();
                    data.setArmor(inventoryUtil.deserializeArmor(kitName));
                    data.setOffhand(inventoryUtil.deserializeOffhand(kitName));
                    if (inventoryUtil.deserializeItems(kitName) != null) {
                        data.setItems(inventoryUtil.deserializeItems(kitName));
                    }
                    return data;
                });

                if (kit != null && kit.getItems() != null) {
                    inventoryUtil.giveItemsFromMap(player, kit.getItems());
                    inventoryUtil.giveArmor(player, kit.getArmor(), kit.getOffhand());
                } else {
                    throw new RuntimeException("Kit items is null");
                }

                config.send(player, "kit-success-got", Map.of("%kit%", kitName));
            }
        }
    }
}
