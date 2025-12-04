package me.elpomoika.holykits.command.subcommand;

import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.command.subcommand.model.SubCommand;
import me.elpomoika.holykits.config.CustomConfig;
import me.elpomoika.holykits.config.Config;
import me.elpomoika.holykits.util.FormatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class KitRemoveCommand implements SubCommand {

    private final Config config;
    private final CustomConfig customConfig;

    public KitRemoveCommand(HolyKits plugin) {
        this.config = plugin.getDefaultConfig();
        this.customConfig = plugin.getCustomConfig();
    }

    @Override
    public String getUsage() {
        return "/kit remove <название>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько игроки могут создавать киты!");
            return;
        }

        if (!player.hasPermission("holykits.admin")) return;

        if (args.length < 2) {
            sender.sendMessage("§cИспользуйте: " + getUsage());
            return;
        }

        String kitName = args[1];

        try {
            customConfig.set("kits." + kitName, null);
            customConfig.save();

            player.sendMessage(FormatUtil.parseAndFormatMessage(config.getKitRemoved(),
                    Map.of("%kit%", Component.text(kitName))
            ));
        } catch (Exception e) {
            throw new RuntimeException("Can't remove kit " + e.getMessage());
        }
    }
}
