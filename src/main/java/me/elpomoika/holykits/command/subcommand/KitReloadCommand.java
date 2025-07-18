package me.elpomoika.holykits.command.subcommand;

import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.command.subcommand.model.SubCommand;
import me.elpomoika.holykits.config.CustomConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitReloadCommand implements SubCommand {

    private final HolyKits plugin;
    private final CustomConfig customConfig;

    public KitReloadCommand(HolyKits plugin) {
        this.plugin = plugin;
        this.customConfig = plugin.getCustomConfig();
    }

    @Override
    public String getUsage() {
        return "/kit reload";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (!player.hasPermission("holykits.admin")) {
            player.sendMessage("§cУ вас нет прав на эту команду!");
            return;
        }

        try {
            plugin.reloadConfig();
            customConfig.reload();
            plugin.getCooldownManager().reloadCooldownConfig();

            player.sendMessage("§aВсе конфиги и кулдауны успешно перезагружены!");
        } catch (Exception e) {
            throw new RuntimeException("Error while reload config " + e.getMessage());
        }
    }
}
