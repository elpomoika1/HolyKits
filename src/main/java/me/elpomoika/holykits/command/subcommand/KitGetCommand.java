package me.elpomoika.holykits.command.subcommand;

import com.github.benmanes.caffeine.cache.Cache;
import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.command.subcommand.model.SubCommand;
import me.elpomoika.holykits.config.CustomConfig;
import me.elpomoika.holykits.model.Kit;
import me.elpomoika.holykits.config.Config;
import me.elpomoika.holykits.util.FormatUtil;
import me.elpomoika.holykits.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class KitGetCommand implements SubCommand {

    private final Cache<@NotNull String, Kit> kitCache;
    private final HolyKits plugin;
    private final InventoryUtil inventoryUtil;
    private final Config config;
    private final CustomConfig customConfig;

    public KitGetCommand(HolyKits plugin) {
        this.plugin = plugin;
        this.inventoryUtil = plugin.getInventoryUtil();
        this.config = plugin.getDefaultConfig();
        this.customConfig = plugin.getCustomConfig();
        this.kitCache = plugin.getKitCache();
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
        ConfigurationSection kitsSection = customConfig.getConfigurationSection("kits");

        if (kitsSection == null || kitsSection.getKeys(false).isEmpty()) {
            player.sendMessage("§cНет доступных китов!");
            return;
        }

        for (String kitName : kitsSection.getKeys(false)) {
            if (inputKitName.equalsIgnoreCase(kitName)) {

                if (!player.hasPermission("holykits.use." + kitName)) {
                    player.sendMessage(config.getKitNoPermission());
                    return;
                }

                long cooldownSeconds = customConfig.getLong("kits." + kitName + ".cooldown", 0);

                if (cooldownSeconds > 0) {
                    String cooldownKey = "kit_" + kitName;

                    if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), cooldownKey)) {
                        long remaining = plugin.getCooldownManager().getRemainingTime(player.getUniqueId(), cooldownKey);
                        String formatTime = FormatUtil.formatTime(remaining);
                        player.sendMessage(FormatUtil.parseAndFormatMessage(config.getKitRemainingTime(),
                                Map.of("%remaining%", Component.text(formatTime))
                        ));
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
                    player.sendMessage(FormatUtil.parseAndFormatMessage(config.getKitSuccessGot(),
                            Map.of("%kit%", Component.text(kitName))
                    ));
                    return;
                } else {
                    throw new RuntimeException("Kit items is null");
                }
            }
        }
    }
}
