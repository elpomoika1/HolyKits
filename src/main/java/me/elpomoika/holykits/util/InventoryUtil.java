package me.elpomoika.holykits.util;

import me.elpomoika.holykits.HolyKits;
import me.elpomoika.holykits.config.CustomConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InventoryUtil {

    private final HolyKits plugin;
    private final CustomConfig customConfig;

    public InventoryUtil(HolyKits plugin) {
        this.plugin = plugin;
        this.customConfig = plugin.getCustomConfig();
    }

    public void serializePlayerInventory(Inventory inventory, String kitName) throws IOException {
        Map<Integer, Map<String, Object>> inventoryItems = new HashMap<>();
        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < 36; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() != Material.AIR) {
                inventoryItems.put(i, item.serialize());
            }
        }

        customConfig.set("kits." + kitName + ".items", inventoryItems);
        customConfig.save();
    }

    public void serializeArmor(Player player, String kitName) throws IOException {
        Map<String, Map<String, Object>> armorMap = new HashMap<>();

        ItemStack[] armorContents = player.getInventory().getArmorContents();

        armorMap.put("helmet", armorContents[3] != null ? armorContents[3].serialize() : null);
        armorMap.put("chestplate", armorContents[2] != null ? armorContents[2].serialize() : null);
        armorMap.put("leggings", armorContents[1] != null ? armorContents[1].serialize() : null);
        armorMap.put("boots", armorContents[0] != null ? armorContents[0].serialize() : null);
        armorMap.put("offhand", player.getInventory().getItemInOffHand().serialize());

        customConfig.set("kits." + kitName + ".armor", armorMap);
        customConfig.save();
    }


    public Map<Integer, Map<String, Object>> deserializeItems(String kitName) {
        var itemsSection = customConfig.getConfigurationSection("kits." + kitName + ".items");
        Map<Integer, Map<String, Object>> itemsMap = new HashMap<>();

        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                int slot = Integer.parseInt(key);
                Map<String, Object> itemData = itemsSection.getConfigurationSection(key).getValues(false);
                itemsMap.put(slot, itemData);
            }
        }

        return itemsMap;
    }

    public void giveDeserializedItems(Player player, String kitName) {
        Map<Integer, Map<String, Object>> itemsMap = deserializeItems(kitName);

        for (Map.Entry<Integer, Map<String, Object>> entry : itemsMap.entrySet()) {
            int slot = entry.getKey();
            Map<String, Object> itemData = entry.getValue();

            try {
                ItemStack item = ItemStack.deserialize(itemData);
                if (slot < 36) {
                    player.getInventory().addItem(item);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Can't give items from map " + e.getMessage());
            }
        }
    }

    public void giveItemsFromMap(Player player, Map<Integer, Map<String, Object>> itemsMap) {
        for (Map.Entry<Integer, Map<String, Object>> entry : itemsMap.entrySet()) {
            int slot = entry.getKey();
            Map<String, Object> itemData = entry.getValue();

            try {
                ItemStack item = ItemStack.deserialize(itemData);
                if (slot < 36) {
                    player.getInventory().addItem(item);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Can't give items from map " + e.getMessage());
            }
        }
    }

    public ItemStack[] deserializeArmor(String kitName) {
        ConfigurationSection armorSection = plugin.getCustomConfig()
                .getConfigurationSection("kits." + kitName + ".armor");

        if (armorSection == null) {
            return new ItemStack[]{};
        }

        ItemStack[] armorContents = new ItemStack[4];

        if (armorSection.get("helmet") != null && !armorSection.getString("helmet.type", "").equals("AIR")) {
            armorContents[3] = ItemStack.deserialize(armorSection.getConfigurationSection("helmet").getValues(false));
        }

        if (armorSection.get("chestplate") != null && !armorSection.getString("chestplate.type", "").equals("AIR")) {
            armorContents[2] = ItemStack.deserialize(armorSection.getConfigurationSection("chestplate").getValues(false));
        }

        if (armorSection.get("leggings") != null && !armorSection.getString("leggings.type", "").equals("AIR")) {
            armorContents[1] = ItemStack.deserialize(armorSection.getConfigurationSection("leggings").getValues(false));
        }

        if (armorSection.get("boots") != null && !armorSection.getString("boots.type", "").equals("AIR")) {
            armorContents[0] = ItemStack.deserialize(armorSection.getConfigurationSection("boots").getValues(false));
        }

        return armorContents;
    }

    public ItemStack deserializeOffhand(String kitName) {
        ConfigurationSection armorSection = plugin.getCustomConfig()
                .getConfigurationSection("kits." + kitName + ".armor");

        if (armorSection == null || armorSection.get("offhand") == null) {
            return new ItemStack(Material.AIR);
        }

        return ItemStack.deserialize(armorSection.getConfigurationSection("offhand").getValues(false));
    }

    public void giveArmor(Player player, ItemStack[] armorContents, ItemStack offhand) {
        if (armorContents != null) {
            ItemStack[] currentArmor = player.getInventory().getArmorContents();

            for (int i = 0; i < armorContents.length; i++) {
                if (armorContents[i] != null && armorContents[i].getType() != Material.AIR) {
                    if (currentArmor[i] != null && currentArmor[i].getType() != Material.AIR) {
                        HashMap<Integer, ItemStack> leftover = player.getInventory()
                                .addItem(armorContents[i]);

                        if (!leftover.isEmpty()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), leftover.get(0));
                        }
                    } else {
                        currentArmor[i] = armorContents[i];
                    }
                }
            }

            player.getInventory().setArmorContents(currentArmor);
        }

        if (offhand != null && offhand.getType() != Material.AIR) {
            if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
                HashMap<Integer, ItemStack> leftover = player.getInventory()
                        .addItem(offhand);

                if (!leftover.isEmpty()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover.get(0));
                }
            } else {
                player.getInventory().setItemInOffHand(offhand);
            }
        }

        player.updateInventory();
    }
}
