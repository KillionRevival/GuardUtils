package com.flyerzrule.mc.guardutils.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.util.sender.PlayerSource;

public class ChatUtils {
  public static List<String> getOnlinePlayers(CommandContext<PlayerSource> context) {
    Player currentPlayer = context.sender().source();

    String input = getArgumentValue(context);

    if (input.equals("")) {
      return Bukkit.getOnlinePlayers().stream()
          .map(Player::getName)
          .filter(name -> !name.toLowerCase().equals(currentPlayer.getName().toLowerCase()))
          .collect(Collectors.toList());
    }

    return Bukkit.getOnlinePlayers().stream()
        .map(Player::getName)
        .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
        .filter(name -> !name.toLowerCase().equals(currentPlayer.getName().toLowerCase()))
        .collect(Collectors.toList());
  }

  public static List<String> filterListByPrefix(List<String> list, String prefix) {
    if (prefix.equals("")) {
      return list;
    }
    return list.stream().filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase()))
        .collect(Collectors.toList());
  }

  public static String getArgumentValue(CommandContext<PlayerSource> context) {
    String rawInput = context.rawInput().input();
    if (rawInput.endsWith(" ")) {
      return "";
    }

    String[] inputSplit = rawInput.split(" ");
    String input;
    if (inputSplit.length == 1) {
      input = "";
    } else {
      input = inputSplit[inputSplit.length - 1];
    }

    return input;
  }
}
