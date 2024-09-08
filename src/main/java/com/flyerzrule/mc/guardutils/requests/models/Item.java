package com.flyerzrule.mc.guardutils.requests.models;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectTypeCategory;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Item {
    private Material material;
    private boolean isPotion;
    private PotionEffectTypeCategory potionEffectTypeCategory;
    private String name;

    public Item(Material material) {
        this.material = material;
        this.isPotion = false;
        this.name = material.name();
    }

    public Item(PotionEffectTypeCategory potionEffectTypeCategory) {
        this.isPotion = true;
        this.material = Material.POTION;
        this.potionEffectTypeCategory = potionEffectTypeCategory;
        this.name = "POTION." + potionEffectTypeCategory.name();
    }
}
