package me.elpomoika.holykits.command.subcommand;

import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.command.subcommand.model.SubCommand;
import me.elpomoika.holykits.config.Config;
import me.elpomoika.holykits.util.FormatUtil;
import me.elpomoika.holykits.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class KitGiveCommand implements SubCommand {

    private final HolyKits plugin;
    private final InventoryUtil inventoryUtil;
    private final Config config;

    public KitGiveCommand(HolyKits plugin) {
        this.plugin = plugin;
        this.inventoryUtil = plugin.getInventoryUtil();
        this.config = plugin.getDefaultConfig();
    }

    @Override
    public String getUsage() {
        return "/kit give <ник> <кит>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько игроки могут использовать киты!");
            return;
        }

        String inputKitName = args[2];
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(FormatUtil.parseAndFormatMessage(config.getPlayerIsOffline(), Map.of()));
            return;
        }

        var kitsSection = plugin.getCustomConfig().getConfigurationSection("kits");

        if (kitsSection == null || kitsSection.getKeys(false).isEmpty()) {
            player.sendMessage("§cНет доступных китов!");
            return;
        }

        for (String kitName : kitsSection.getKeys(false)) {
            if (!inputKitName.equalsIgnoreCase(kitName)) continue;

            ItemStack[] armor = inventoryUtil.deserializeArmor(kitName);

            inventoryUtil.giveDeserializedItems(target, kitName);
            inventoryUtil.giveArmor(target, armor, inventoryUtil.deserializeOffhand(kitName));

            player.sendMessage(FormatUtil.parseAndFormatMessage(config.getKitGiveSucceed(),
                    Map.of("%kit%", Component.text(kitName),
                            "%player%", Component.text(target.getName()))
            ));
        }
    }
}
