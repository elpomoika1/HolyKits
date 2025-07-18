package me.elpomoika.holykits.command.subcommand;

import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.command.subcommand.model.SubCommand;
import me.elpomoika.holykits.config.CustomConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class KitSetCooldownCommand implements SubCommand {

    private final HolyKits plugin;
    private final CustomConfig customConfig;

    public KitSetCooldownCommand(HolyKits plugin) {
        this.plugin = plugin;
        this.customConfig = plugin.getCustomConfig();
    }

    @Override
    public String getUsage() {
        return "/kit cooldown <название> <длительность в сек>";
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

        String kitName = args[1];
        long duration = Long.parseLong(args[2]);

        customConfig.set("kits." + kitName + ".cooldown", duration);

        customConfig.save();
        player.sendMessage("Successfully set cooldown to fucking kit " + duration);
    }
}
