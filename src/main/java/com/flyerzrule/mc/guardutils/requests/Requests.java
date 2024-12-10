package com.flyerzrule.mc.guardutils.requests;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.requests.models.ContrabandType;
import com.flyerzrule.mc.guardutils.requests.models.Item;
import com.flyerzrule.mc.guardutils.requests.models.Request;
import com.flyerzrule.mc.guardutils.requests.utils.Utils;

public class Requests {
    private static Requests instance;

    private Requests() {
    }

    public static Requests getInstance() {
        if (instance == null) {
            instance = new Requests();
        }
        return instance;
    }

    private Map<UUID, Request> requests = new HashMap<>();

    public void addRequest(Player player, Player guard, ContrabandType type, Item contrabandItem) {
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
            if (this.isRequested(player)) {
                Request request = this.getRequested(player);

                TextComponent playerMessage = Component.text().color(NamedTextColor.RED)
                        .content(String.format("You have failed to drop your %s!", contraband)).build();
                player.sendMessage(playerMessage);

                TextComponent guardMessage = Component.text().color(NamedTextColor.RED)
                        .content(String.format("%s has failed to drop their %s!", player.getName(), contraband))
                        .build();
                request.getGuard().sendMessage(guardMessage);

                this.removeRequest(player);
            }
        });

        Request request = new Request(guard, type, contrabandItem, tasks[0], tasks[1]);
        requests.put(player.getUniqueId(), request);
    }

    public void removeRequest(Player player) {
        if (isRequested(player)) {
            requests.remove(player.getUniqueId());
        }
    }

    public boolean isRequested(Player player) {
        return requests.containsKey(player.getUniqueId());
    }

    public Request getRequested(Player player) {
        return requests.get(player.getUniqueId());
    }

    public void cancelRequest(Player player) {
        if (isRequested(player)) {
            Request request = this.getRequested(player);

            BukkitTask countTask = request.getCountTask();
            countTask.cancel();
            BukkitTask cancelTask = request.getCancelTask();
            cancelTask.cancel();

            GuardUtils.getMyLogger().sendDebug(String.format("Cancelling request for %s.", player.getName()));
            this.removeRequest(player);
        }
    }

}
