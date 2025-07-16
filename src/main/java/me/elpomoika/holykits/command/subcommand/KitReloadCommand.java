package me.elpomoika.holykits.command.subcommand;

import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.command.subcommand.model.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitReloadCommand implements SubCommand {

    private final HolyKits plugin;

    public KitReloadCommand(HolyKits plugin) {
        this.plugin = plugin;
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
            plugin.getCustomConfig().load(plugin.getCustomConfigFile());
            plugin.getCooldownManager().reloadCooldownConfig();

            player.sendMessage("§aВсе конфиги и кулдауны успешно перезагружены!");
        } catch (Exception e) {
            player.sendMessage("§cОшибка при перезагрузке: " + e.getMessage());
            plugin.getLogger().severe("Ошибка перезагрузки: ");
            e.printStackTrace();
        }
    }
}
