package com.flyerzrule.mc.guardutils.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatUtils {
    public static List<String> getOnlinePlayers(Player currentPlayer, String input) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
                .filter(name -> !name.toLowerCase().equals(currentPlayer.getName().toLowerCase()))
                .collect(Collectors.toList());
    }

    public static List<String> filterListByPrefix(List<String> list, String prefix) {
        return list.stream().filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}
