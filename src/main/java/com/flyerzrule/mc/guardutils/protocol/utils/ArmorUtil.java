package com.flyerzrule.mc.guardutils.protocol.utils;

import org.bukkit.Material;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

public class ArmorUtil {
    public static boolean isDiamondArmor(Material material) {
        return material == Material.DIAMOND_HELMET ||
                material == Material.DIAMOND_CHESTPLATE ||
                material == Material.DIAMOND_LEGGINGS ||
                material == Material.DIAMOND_BOOTS;
    }

    public static Material getChainmailType(Material diamondType) {
        switch (diamondType) {
            case DIAMOND_HELMET:
                return Material.CHAINMAIL_HELMET;
            case DIAMOND_CHESTPLATE:
                return Material.CHAINMAIL_CHESTPLATE;
            case DIAMOND_LEGGINGS:
                return Material.CHAINMAIL_LEGGINGS;
            case DIAMOND_BOOTS:
                return Material.CHAINMAIL_BOOTS;
            default:
                return Material.CHAINMAIL_HELMET;
        }
    }

    public static boolean isArmorSlot(ItemSlot slot) {
        return slot == ItemSlot.HEAD || slot == ItemSlot.CHEST ||
                slot == ItemSlot.LEGS || slot == ItemSlot.FEET;
    }

    public static boolean isArmorSlot(int slot) {
        return slot >= 5 && slot <= 8;
    }
}
