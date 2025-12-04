package me.elpomoika.holykits.command.subcommand.model;

import org.bukkit.command.CommandSender;

public interface SubCommand {
    String getUsage();
    void execute(CommandSender sender, String[] args);
}
