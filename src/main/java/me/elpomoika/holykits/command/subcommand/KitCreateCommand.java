package me.elpomoika.holykits.command.subcommand;

import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.command.subcommand.model.SubCommand;
import me.elpomoika.holykits.util.Config;
import me.elpomoika.holykits.util.InventoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;

public class KitCreateCommand implements SubCommand {

    private final InventoryUtil inventoryUtil;
    private final HolyKits plugin;
    private final Config config;

    public KitCreateCommand(HolyKits plugin) {
        this.plugin = plugin;
        this.inventoryUtil = plugin.getInventoryUtil();
        this.config = plugin.getDefaultConfig();
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

        if (!player.hasPermission("holykits.admin")) {
            return;
        }

        if (args.length == 1) {
            sender.sendMessage("§cИспользуйте: " + getUsage());
            return;
        }

        if (args[1].isEmpty()) {
            config.send(player, "kit-name-is-empty");
        } else if (args[1].equalsIgnoreCase(String.valueOf(plugin.getCustomConfig().getConfigurationSection("kits")))) {
            config.send(player, "kit-already-exists");
        }
        try {
            inventoryUtil.serializePlayerInventory(player.getInventory(), args[1]);
            if (player.getEquipment() != null) {
                inventoryUtil.serializeArmor(player, args[1]);
            }
            player.sendMessage(ChatColor.GREEN + "Kit succeed created");
            plugin.getCustomConfig().load(plugin.getCustomConfigFile());
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
