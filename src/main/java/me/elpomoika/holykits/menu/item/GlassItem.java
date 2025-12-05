package me.elpomoika.holykits.menu.item;

import lombok.RequiredArgsConstructor;
import me.elpomoika.holykits.config.Config;
import me.elpomoika.holykits.util.FormatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.Map;

@RequiredArgsConstructor
public class GlassItem extends AbstractItem {
    private final Config config;

    @Override
    public ItemProvider getItemProvider(Player viewer) {
        return new ItemBuilder(Material.valueOf(config.getGlassMaterial().toUpperCase()))
                .setDisplayName(new AdventureComponentWrapper(FormatUtil.parseAndFormatMessage(config.getGlassName(), Map.of())));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
