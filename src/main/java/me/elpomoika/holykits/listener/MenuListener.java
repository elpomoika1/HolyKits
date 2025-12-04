package me.elpomoika.holykits.listener;

import me.elpomoika.holykits.config.CustomConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;

public class MenuListener implements Listener {

    private final CustomConfig customConfig;

    public MenuListener(CustomConfig customConfig) {
        this.customConfig = customConfig;
    }

    @EventHandler
    public void onClickGui(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        final String title = event.getView().getTitle();

        for (String kit : getAllKitsName()) {
            if (!title.equalsIgnoreCase(kit)) {
                continue;
            }
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null) {
                return;
            }
        }
    }

    private Set<String> getAllKitsName() {
        var kitsSection = customConfig.getConfigurationSection("kits");

        if (kitsSection == null) {
            return Collections.emptySet();
        }

        return kitsSection.getKeys(false);
    }
}
