package me.elpomoika.holykits.command.subcommand;

import com.github.benmanes.caffeine.cache.Cache;
import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.menu.PreviewMenu;
import me.elpomoika.holykits.command.subcommand.model.SubCommand;
import me.elpomoika.holykits.config.Config;
import me.elpomoika.holykits.util.FormatUtil;
import me.elpomoika.holykits.util.InventoryUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class KitPreviewCommand implements SubCommand {

    private final InventoryUtil inventoryUtil;
    private final HolyKits plugin;
    private final Config config;
    private final PreviewMenu previewMenu;
    private final Cache<@NotNull String, Map<Integer, ItemStack>> itemCache;

    public KitPreviewCommand(HolyKits plugin) {
        this.plugin = plugin;
        this.inventoryUtil = plugin.getInventoryUtil();
        this.config = plugin.getDefaultConfig();
        this.previewMenu = plugin.getPreviewMenu();

        this.itemCache = plugin.getItemCache();
    }

    @Override
    public String getUsage() {
        return "/kit preview <название>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько игроки могут просматривать киты!");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§cИспользуйте: " + getUsage());
            return;
        }

        ConfigurationSection kitsSection = plugin.getCustomConfig().getConfigurationSection("kits");

        if (kitsSection == null) {
            return;
        }
        
        String kitName = args[1];
        if (!kitsSection.contains(kitName)) {
            player.sendMessage(FormatUtil.parseAndFormatMessage(config.getKitNotFound(), Map.of()));
            return;
        }

        Map<Integer, ItemStack> cachedItems = itemCache.getIfPresent(kitName);

        if (cachedItems == null) {
            Map<Integer, Map<String, Object>> serializedItems = inventoryUtil.deserializeItems(kitName);
            cachedItems = new HashMap<>();

            for (Map.Entry<Integer, Map<String, Object>> entry : serializedItems.entrySet()) {
                try {
                    ItemStack item = ItemStack.deserialize(entry.getValue());
                    cachedItems.put(entry.getKey(), item);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e);
                }
            }

            itemCache.put(kitName, cachedItems);
        }

        previewMenu.openMenu(player, kitName, cachedItems);
    }
}
