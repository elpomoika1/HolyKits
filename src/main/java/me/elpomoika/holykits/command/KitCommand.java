package me.elpomoika.holykits.command;

import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.command.subcommand.*;
import me.elpomoika.holykits.command.subcommand.model.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class KitCommand implements CommandExecutor {

    private final HolyKits plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public void registerSubCommands() {
        subCommands.put("create", new KitCreateCommand(plugin));
        subCommands.put("remove", new KitRemoveCommand(plugin));
        subCommands.put("get", new KitGetCommand(plugin));
        subCommands.put("preview", new KitPreviewCommand(plugin));
        subCommands.put("cooldown", new KitSetCooldownCommand(plugin));
        subCommands.put("reload", new KitReloadCommand(plugin));
        subCommands.put("give", new KitGiveCommand(plugin));
    }

    public KitCommand(HolyKits plugin) {
        this.plugin = plugin;
        registerSubCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length == 0) {
            if (sender.hasPermission("holykits.admin")) {
                sender.sendMessage("§cДоступные команды: create, remove, get, preview, reload");
            } else {
                sender.sendMessage("§cДоступные команды: preview, get");
            }
            return true;
        }

        if ((args.length == 1 && !subCommands.containsKey(args[0].toLowerCase()))) {
            SubCommand getCommand = subCommands.get("get");
            if (getCommand != null) {
                getCommand.execute(sender, args);
                return true;
            }
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage("§cНеизвестная подкоманда!");
            return true;
        }

        subCommand.execute(sender, args);
        return true;
    }
}