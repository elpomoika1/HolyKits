package me.elpomoika.holykits.command.subcommand;

import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.command.subcommand.model.SubCommand;
import me.elpomoika.holykits.config.CustomConfig;
import me.elpomoika.holykits.config.Config;
import me.elpomoika.holykits.util.FormatUtil;
import me.elpomoika.holykits.util.InventoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;

public class KitCreateCommand implements SubCommand {

    private final InventoryUtil inventoryUtil;
    private final Config config;
    private final CustomConfig customConfig;

    public KitCreateCommand(HolyKits plugin) {
        this.inventoryUtil = plugin.getInventoryUtil();
        this.config = plugin.getDefaultConfig();
        this.customConfig = plugin.getCustomConfig();
    }


    @Override
    public String getUsage() {
        return "/kit create <название>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько игроки могут создавать киты!");
            return;
        }

        if (!player.hasPermission("holykits.admin")) return;

        if (args.length == 1) {
            sender.sendMessage("§cИспользуйте: " + getUsage());
            return;
        }

        if (customConfig.isKitNameEmpty(args[1])) {
            player.sendMessage(FormatUtil.parseAndFormatMessage(config.getKitNameIsEmpty(), Map.of()));
            return;
        } else if (customConfig.isKitExists(args[1])) {
            player.sendMessage(FormatUtil.parseAndFormatMessage(config.getKitAlreadyExists(), Map.of()));
            return;
        }

        try {
            inventoryUtil.serializePlayerInventory(player.getInventory(), args[1]);
            if (player.getEquipment() != null) {
                inventoryUtil.serializeArmor(player, args[1]);
            }
            player.sendMessage(ChatColor.GREEN + "Kit succeed created");
            customConfig.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
