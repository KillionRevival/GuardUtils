package com.flyerzrule.mc.guardutils.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KOSTimer {
    private static Map<UUID, Long> kosTimers = new HashMap<>();

    public static void setKOSTimer(Player player, int time) {
        long timeInMillis = time * 60 * 1000;
        long endTime = System.currentTimeMillis() + timeInMillis;

        kosTimers.put(player.getUniqueId(), endTime);
    }

    public static void cancelKOSTimer(Player player) {
        if (isKOSTimerActive(player)) {
            kosTimers.remove(player.getUniqueId());
        }
    }

    public static boolean isKOSTimerActive(Player player) {
        return kosTimers.containsKey(player.getUniqueId());
    }

    public static long getKOSTimer(Player player) {
        return kosTimers.get(player.getUniqueId());
    }

    public static List<Player> getPlayersOnKOS() {
        return kosTimers.keySet().stream().map(uuid -> Bukkit.getPlayer(uuid)).collect(Collectors.toList());
    }

    public static List<String> getPlayerUsernamesOnKOS() {
        return kosTimers.keySet().stream().map(uuid -> Bukkit.getPlayer(uuid).getName()).collect(Collectors.toList());
    }
}
