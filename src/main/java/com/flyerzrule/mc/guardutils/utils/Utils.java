package com.flyerzrule.mc.guardutils.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.bukkit.scheduler.BukkitTask;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.utils.requests.Requests;
import com.flyerzrule.mc.guardutils.utils.requests.models.ContrabandType;
import com.flyerzrule.mc.guardutils.utils.requests.models.Item;
import com.flyerzrule.mc.guardutils.utils.requests.models.Request;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class Utils {
    public static BukkitTask[] startCountdown(Player player, ContrabandType type, Item contrabandItem,
            Runnable runnable) {
        String contraband;
        switch (type) {
            case SWORD:
                contraband = "sword";
                break;
            case BOW:
                contraband = "bow";
                break;
            case OTHER:
            default:
                if (contrabandItem != null) {
                    contraband = contrabandItem.getName();
                } else {
                    contraband = "contraband";
                }
                break;
        }

        int countdown = GuardUtils.getPlugin().getConfig().getInt("countdown-time");
        AtomicInteger timer = new AtomicInteger(countdown + 1);

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(GuardUtils.getPlugin(), () -> {
            timer.set(timer.get() - 1);

            Request request = Requests.getRequested(player);

            TextComponent playerMessage = Component.text().color(NamedTextColor.GOLD)
                    .content(String.format("You have %d seconds to drop your %s!", timer.get(), contraband)).build();
            player.sendMessage(playerMessage);

            TextComponent guardMessage = Component.text().color(NamedTextColor.GOLD).content(
                    String.format("%s has %d seconds to drop their %s!", player.getName(), timer.get(), contraband))
                    .build();

            request.getGuard().sendMessage(guardMessage);
        }, 0, 20);

        BukkitTask cancelTask = Bukkit.getScheduler().runTaskLaterAsynchronously(GuardUtils.getPlugin(), () -> {
            task.cancel();
            runnable.run();
        }, 20L * countdown);
        return new BukkitTask[] { task, cancelTask };
    }

    public static boolean hasContrabrandInInventory(Player player, ContrabandType type) {
        if (type == ContrabandType.OTHER) {
            return false;
        }

        for (ItemStack playerItem : player.getInventory().getContents()) {
            for (Item contrabandItem : Utils.getContrandTypeItems(type)) {
                if (playerItem.getType() == contrabandItem.getMaterial()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasOtherContrabandItemInInventory(Player player, Item contrabandItem) {
        for (ItemStack playerItem : player.getInventory().getContents()) {
            if (playerItem.getType() == contrabandItem.getMaterial()) {
                if (contrabandItem.isPotion()) {
                    PotionMeta meta = (PotionMeta) playerItem.getItemMeta();
                    for (PotionEffect effect : meta.getCustomEffects()) {
                        if (effect.getType().getCategory() == contrabandItem.getPotionEffectTypeCategory()) {
                            return true;
                        }
                    }
                } else {
                    if (playerItem.getType() == contrabandItem.getMaterial()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<Item> getContrandTypeItems(ContrabandType type) {
        List<Item> contraband = new ArrayList<>();
        switch (type) {
            case SWORD:
                contraband.add(new Item(Material.NETHERITE_SWORD));
                contraband.add(new Item(Material.DIAMOND_SWORD));
                contraband.add(new Item(Material.GOLDEN_SWORD));
                contraband.add(new Item(Material.IRON_SWORD));
                contraband.add(new Item(Material.STONE_SWORD));
                contraband.add(new Item(Material.WOODEN_SWORD));
                break;
            case BOW:
                contraband.add(new Item(Material.BOW));
                contraband.add(new Item(Material.CROSSBOW));
                break;
            case OTHER:
            default:
                contraband.add(new Item(Material.FLINT_AND_STEEL));
                contraband.add(new Item(Material.ARROW));
                contraband.add(new Item(Material.TIPPED_ARROW));
                contraband.add(new Item(Material.SPECTRAL_ARROW));
                contraband.add(new Item(Material.LAVA_BUCKET));
                contraband.add(new Item(Material.SHIELD));
                contraband.add(new Item(Material.TRIDENT));
                contraband.add(new Item(PotionEffectTypeCategory.HARMFUL));
                break;
        }
        return contraband;
    }
}
