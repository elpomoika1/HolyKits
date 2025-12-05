package me.elpomoika.holykits.menu.item;

import lombok.RequiredArgsConstructor;
import me.elpomoika.holykits.config.Config;
import me.elpomoika.holykits.util.FormatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GiveItem extends AbstractItem {
    private final Config config;
    private final String kitName;

    @Override
    public ItemProvider getItemProvider(Player viewer) {
        List<ComponentWrapper> loreWrapped = config.getGiveKitItemLore().stream()
                .map(line -> {
                    Component formatted = FormatUtil.parseAndFormatMessage(line, Map.of());

                    return new AdventureComponentWrapper(formatted);
                })
                .collect(Collectors.toList());

        return new ItemBuilder(Material.valueOf(config.getGiveKitItemMaterial().toUpperCase()))
                .setDisplayName(new AdventureComponentWrapper(FormatUtil.parseAndFormatMessage(config.getGiveKitItemName(), Map.of())))
                .setLore(loreWrapped);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        String command = "kit " + kitName;

        player.performCommand(command);
    }
}
