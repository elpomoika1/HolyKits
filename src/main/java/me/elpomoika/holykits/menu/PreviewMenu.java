package me.elpomoika.holykits.menu;

import me.elpomoika.holykits.config.Config;
import me.elpomoika.holykits.util.FormatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.CommandItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        ItemBuilder glassItem = new ItemBuilder(Material.valueOf(config.getGlassMaterial().toUpperCase()))
                .setDisplayName(new AdventureComponentWrapper(FormatUtil.parseAndFormatMessage(config.getGlassName(), Map.of())));

        List<ComponentWrapper> loreWrapped = config.getGiveKitItemLore().stream()
                .map(line -> {
                    Component formatted = FormatUtil.parseAndFormatMessage(line, Map.of());

                    return new AdventureComponentWrapper(formatted);
                })
                .collect(Collectors.toList());

        ItemBuilder giveItem = new ItemBuilder(Material.valueOf(config.getGiveKitItemMaterial().toUpperCase()))
                .setDisplayName(new AdventureComponentWrapper(FormatUtil.parseAndFormatMessage(config.getGiveKitItemName(), Map.of())))
                .setLore(loreWrapped);

        Gui gui = Gui.normal()
                .setStructure(
                        ". . . . . . . . .",
                        ". . . . . . . . .",
                        ". . . . . . . . .",
                        ". . . . . . . . .",
                        "# # # # ! # # # #")
                .addIngredient('#', new SimpleItem(glassItem))
                .addIngredient('!', new CommandItem(giveItem, "/kit " + kitName))
                .build();

        items.forEach((slot, item) -> gui.addItems(new SimpleItem(item)));

        return gui;
    }
}
