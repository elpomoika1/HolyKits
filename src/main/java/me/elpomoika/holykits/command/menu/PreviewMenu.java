package me.elpomoika.holykits.command.menu;

import me.elpomoika.holykits.util.Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.CommandItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.Map;

public class PreviewMenu {

    private final Config config;

    public PreviewMenu(Config config) {
        this.config = config;
    }

    public void openMenu(Player player, String kitName, Map<Integer, ItemStack> items) {
        Window window = Window.single()
                .setViewer(player)
                .setTitle(kitName)
                .setGui(buildPreviewGui(kitName, items))
                .build();

        window.open();
    }

    private Gui buildPreviewGui(String kitName, Map<Integer, ItemStack> items) {
        ItemBuilder glassItem = new ItemBuilder(getMaterial("menu.glasses"))
                .setDisplayName(color(config.getConfig().getString("menu.glass-name")));

        ItemBuilder giveItem = new ItemBuilder(getMaterial("menu.give-kit-item.material"))
                .setDisplayName(color(config.getConfig().getString("menu.give-kit-item.name")))
                .addLoreLines(getLore());

        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . . . . . . . #",
                        "# . . . . . . . #",
                        "# . . . . . . . #",
                        "# # # # ! # # # #")
                .addIngredient('#', new SimpleItem(glassItem))
                .addIngredient('!', new CommandItem(giveItem, "/kit " + kitName))
                .build();

        items.forEach((slot, item) -> gui.addItems(new SimpleItem(item)));

        return gui;
    }

    private Material getMaterial(String path) {
        return Material.getMaterial(config.getConfig().getString(path, "STONE"));
    }

    private String[] getLore() {
        return config.getConfig().getStringList("menu.give-kit-item.lore").stream()
                .map(this::color)
                .toArray(String[]::new);
    }

    private String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
