package com.flyerzrule.mc.guardutils.armorstands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.armorstands.models.ArmorStandFollower;
import com.flyerzrule.mc.guardutils.invis.InvisPlayers;
import com.flyerzrule.mc.guardutils.kos.KOSTimer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ArmorStandManager {
    private static ArmorStandManager instance;

    private Map<UUID, ArmorStandFollower> armorStandFollowers = new HashMap<>();

    private ArmorStandManager() {
    }

    public static ArmorStandManager getInstance() {
        if (instance == null) {
            instance = new ArmorStandManager();
        }
        return instance;
    }

    public void addArmorStand(Player player, Component initialText) {
        if (!hasArmorStand(player)) {
            ArmorStandFollower armorStandFollower = new ArmorStandFollower(player, initialText);
            armorStandFollowers.put(player.getUniqueId(), armorStandFollower);
        }
    }

    public void removeArmorStand(Player player) {
        if (hasArmorStand(player)) {
            ArmorStandFollower armorStandFollower = this.getArmorStand(player);
            armorStandFollower.stopFollowing();
            armorStandFollowers.remove(player.getUniqueId());
        }
    }

    public void removeAllArmorStands() {
        for (ArmorStandFollower armorStandFollower : armorStandFollowers.values()) {
            armorStandFollower.stopFollowing();
        }
        armorStandFollowers.clear();
    }

    public ArmorStandFollower getArmorStand(Player player) {
        return armorStandFollowers.get(player.getUniqueId());
    }

    public void updateArmorStandName(Player player, Component text) {
        ArmorStandFollower armorStandFollower = this.getArmorStand(player);
        armorStandFollower.updateCustomName(text);

    }

    public boolean hasArmorStand(Player player) {
        return armorStandFollowers.containsKey(player.getUniqueId());
    }

    public void createArmorStandTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                KOSTimer kosTimer = KOSTimer.getInstance();
                InvisPlayers invisPlayers = InvisPlayers.getInstance();
                ArmorStandManager armorStandManager = ArmorStandManager.getInstance();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.hasPermission("guardutils.guard")) {
                        if (kosTimer.isKOSTimerActive(player)) {
                            List<Component> tagList = new ArrayList<>();
                            tagList.add(Component.text().color(NamedTextColor.RED).content("KOS").build());
                            // if (invisPlayers.isPlayerInvisible(player)) {
                            //     tagList.add(Component.text().color(NamedTextColor.AQUA).content("INVIS").build());
                            // }
                            Component tag = Component.text().append(tagList).build();
                            armorStandManager.addArmorStand(player, tag);
                        } else {
                            armorStandManager.removeArmorStand(player);
                        }

                    }

                }
            }
        }.runTaskTimer(GuardUtils.getPlugin(), 0L, 20L); // Update every second
    }
}
