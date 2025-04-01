package com.flyerzrule.mc.guardutils.itemlock;

import lombok.Value;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Value
public class CustomItem {
    Material material;
    int amount;
    Map<Enchantment, Integer> enchantments;

    ItemStack itemStack;

    public CustomItem(Material material, int amount, Map<Enchantment, Integer> enchantments) {
        this.material = material;
        this.amount = amount;
        this.enchantments = enchantments;

        this.itemStack = createItemStack();
    }

    private ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material, amount);
        itemStack.addUnsafeEnchantments(enchantments);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Guard Item"));
        itemStack.lore(lore);

        ItemMeta itemMeta = itemStack.getItemMeta();
        NamespacedKey key = new NamespacedKey("GuardUtils", "guard_item");
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "GuardUtils.guard");

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
