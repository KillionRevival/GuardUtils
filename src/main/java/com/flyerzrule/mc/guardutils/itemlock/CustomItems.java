package com.flyerzrule.mc.guardutils.itemlock;

import com.flyerzrule.mc.guardutils.GuardUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItems {
    private static CustomItems instance;

    private final List<String> configItems;

    @Getter
    private final List<ItemStack> customItems;

    private CustomItems() {
        this.configItems = GuardUtils.getPlugin().getConfig().getStringList("custom-items");

        this.customItems = parseConfigItems();
    }

    public static CustomItems getInstance() {
        if (instance == null) {
            instance = new CustomItems();
        }
        return instance;
    }

    private List<ItemStack> parseConfigItems() {
        List<ItemStack> items = new ArrayList<>();
        for (String configItem : configItems) {
            items.add(parseConfig(configItem));
        }
        return items;
    }

    private ItemStack parseConfig(String configString) {
        String[] parts = configString.split(" ");

        if (parts.length < 2) {
            GuardUtils.getMyLogger().sendError("Invalid custom item: " + configString);
            return null;
        }

        Material material = Material.getMaterial(parts[0]);
        int amount = Integer.parseInt(parts[1]);

        Map<Enchantment, Integer> enchantments = new HashMap<>();

        for (int i = 2; i < parts.length; i++) {
            String[] enchantmentParts = parts[i].split(":");

            if (enchantmentParts.length != 2) {
                GuardUtils.getMyLogger().sendError("Invalid enchantment: " + parts[i]);
                continue;
            }

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentParts[0]));
            int level = Integer.parseInt(enchantmentParts[1]);

            enchantments.put(enchantment, level);
        }
        CustomItem customItem = new CustomItem(material, amount, enchantments);

        return customItem.getItemStack();
    }

}
