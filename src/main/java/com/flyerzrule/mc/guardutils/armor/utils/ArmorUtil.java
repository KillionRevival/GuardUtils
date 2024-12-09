package com.flyerzrule.mc.guardutils.armor.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.flyerzrule.mc.guardutils.GuardUtils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;

public class ArmorUtil {
    public static boolean isArmor(Material material) {
        String[] armorParts = { "helmet", "chestplate", "leggings", "boots" };
        for (String armorPart : armorParts) {
            if (material.name().toLowerCase().contains(armorPart)) {
                return true;
            }
        }
        return false;
    }

    public static Material getChainmailType(Material material) {
        String materialName = material.name().toLowerCase();
        if (materialName.contains("helmet")) {
            return Material.CHAINMAIL_HELMET;
        } else if (materialName.contains("chestplate")) {
            return Material.CHAINMAIL_CHESTPLATE;
        } else if (materialName.contains("leggings")) {
            return Material.CHAINMAIL_LEGGINGS;
        } else if (materialName.contains("boots")) {
            return Material.CHAINMAIL_BOOTS;
        }
        return material;
    }

    public static boolean isPlayerGuard(Player player) {
        LuckPerms luckPerms = GuardUtils.getLuckperms();

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            return user.getPrimaryGroup().equals("guard");
        }
        return false;
    }

    public static ItemStack copyEnchants(ItemStack source, ItemStack target) {
        if (source.hasItemMeta() && source.getItemMeta().hasEnchants()) {
            ItemMeta sourceMeta = source.getItemMeta();
            ItemMeta targetMeta = target.getItemMeta();

            for (Enchantment enchantment : sourceMeta.getEnchants().keySet()) {
                int enchantmentLevel = sourceMeta.getEnchantLevel(enchantment);
                targetMeta.addEnchant(enchantment, enchantmentLevel, true);
            }

            target.setItemMeta(targetMeta);
        }
        return target;
    }
}
