package com.flyerzrule.mc.guardutils.utils.requests;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.flyerzrule.mc.guardutils.utils.requests.models.Request;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import com.flyerzrule.mc.guardutils.utils.Utils;
import com.flyerzrule.mc.guardutils.utils.requests.models.ContrabandType;
import com.flyerzrule.mc.guardutils.utils.requests.models.Item;

public class Requests {
    private static Map<UUID, Request> requests = new HashMap<>();

    public static void addRequest(Player player, Player guard, ContrabandType type, Item contrabandItem) {
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

        BukkitTask[] tasks = Utils.startCountdown(player, type, contrabandItem, () -> {
            if (Requests.isRequested(player)) {
                Request request = Requests.getRequested(player);

                TextComponent playerMessage = Component.text().color(NamedTextColor.RED)
                        .content(String.format("You have failed to drop your %s!", contraband)).build();
                player.sendMessage(playerMessage);

                TextComponent guardMessage = Component.text().color(NamedTextColor.RED)
                        .content(String.format("%s has failed to drop their %s!", player.getName(), contraband))
                        .build();
                request.getGuard().sendMessage(guardMessage);

                Requests.removeRequest(player);
            }
        });

        Request request = new Request(guard, type, contrabandItem, tasks[0], tasks[1]);
        requests.put(player.getUniqueId(), request);
    }

    public static void removeRequest(Player player) {
        if (isRequested(player)) {
            requests.remove(player.getUniqueId());
        }
    }

    public static boolean isRequested(Player player) {
        return requests.containsKey(player.getUniqueId());
    }

    public static Request getRequested(Player player) {
        return requests.get(player.getUniqueId());
    }

}
