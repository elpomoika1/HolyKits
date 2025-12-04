package me.elpomoika.holykits.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Data
public class Kit {
    ItemStack[] armor;
    ItemStack offhand;
    Map<Integer, Map<String, Object>> items;

    public Map<Integer, Map<String, Object>> getItems() {
        if (items != null) {
            return items;
        }
        return new HashMap<>();
    }

    public void setItems(Map<Integer, Map<String, Object>> items) {
        if (items != null) {
            this.items = items;
        }
    }
}
