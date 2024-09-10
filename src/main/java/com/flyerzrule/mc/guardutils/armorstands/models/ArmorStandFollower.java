package com.flyerzrule.mc.guardutils.armorstands.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.flyerzrule.mc.guardutils.GuardUtils;

import net.kyori.adventure.text.Component;

public class ArmorStandFollower {
    private ArmorStand armorStand;
    private Player player;
    private BukkitRunnable task;
    private Component text;

    private final int showAndHideDelay = 30 * 20;
    private int showAndHideTimer = 0;

    public ArmorStandFollower(Player player, Component text) {
        this.player = player;
        this.text = text;
        spawnArmorStand();
        startFollowing();

        showAndHideArmorStand();
    }

    private void spawnArmorStand() {
        this.armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, .3, 0),
                EntityType.ARMOR_STAND);
        this.armorStand.setVisible(false);

        this.armorStand.customName(this.text);
        this.armorStand.setCustomNameVisible(true);
        this.armorStand.setGravity(false);
        this.armorStand.setSmall(true);
        this.armorStand.setInvulnerable(true);
        this.armorStand.setMarker(true);
    }

    private void startFollowing() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && armorStand != null) {
                    Location playerLocation = player.getLocation();
                    armorStand.teleport(playerLocation.add(0, 2, 0));

                    if (showAndHideTimer > showAndHideDelay) {
                        showAndHideArmorStand();
                        showAndHideTimer = 0;
                    } else {
                        showAndHideTimer++;
                    }
                } else {
                    stopFollowing();
                }
            }
        };

        // Run task every 1 tick (20 ticks = 1 second)
        task.runTaskTimer(GuardUtils.getPlugin(), 0L, 1L);
    }

    public void stopFollowing() {
        if (task != null) {
            task.cancel();
        }

        if (armorStand != null) {
            armorStand.remove();
        }
    }

    public void updateCustomName(Component text) {
        this.text = text;
        this.armorStand.customName(text);
    }

    private void showAndHideArmorStand() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.player.equals(player)) {
                continue;
            }

            if (player.hasPermission("guardutils.guard")) {
                GuardUtils.getMyLogger().sendDebug(
                        String.format("Showing %s's armor stand for %s.", this.player.getName(), player.getName()));
                player.showEntity(GuardUtils.getPlugin(), this.armorStand);
            } else {
                GuardUtils.getMyLogger().sendDebug(
                        String.format("Hiding %s's armor stand for %s.", this.player.getName(), player.getName()));
                player.hideEntity(GuardUtils.getPlugin(), this.armorStand);
            }
        }
    }

}
