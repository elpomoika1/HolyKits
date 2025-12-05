package me.elpomoika.holykits.menu;

import lombok.RequiredArgsConstructor;
import me.elpomoika.holykits.config.Config;
import me.elpomoika.holykits.menu.item.GlassItem;
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

@RequiredArgsConstructor
public class PreviewMenu {
    private final Config config;

    public void openMenu(Player player, String kitName, Map<Integer, ItemStack> items) {
        Window window = Window.single()
                .setViewer(player)
                .setTitle(kitName)
                .setGui(buildPreviewGui(kitName, items))
                .build();

        window.open();
    }

    private Gui buildPreviewGui(String kitName, Map<Integer, ItemStack> items) {
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
                .setStructure(config.getStructure().toArray(new String[0]))
                .addIngredient('g', new GlassItem(config))
                .addIngredient('!', new CommandItem(giveItem, "/kit " + kitName))
                .build();

        items.forEach((slot, item) -> gui.addItems(new SimpleItem(item)));

        return gui;
    }
}
