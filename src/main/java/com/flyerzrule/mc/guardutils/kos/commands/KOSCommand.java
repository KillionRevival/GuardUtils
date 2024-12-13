package com.flyerzrule.mc.guardutils.kos.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Default;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.kos.KOSTimer;
import com.flyerzrule.mc.guardutils.utils.ChatUtils;
import com.flyerzrule.mc.guardutils.utils.Permissions;
import com.flyerzrule.mc.guardutils.utils.time.TimeUtils;
import com.flyerzrule.mc.guardutils.utils.time.models.MinSec;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class KOSCommand {
  private final KOSTimer kosTimer;

  public KOSCommand() {
    this.kosTimer = KOSTimer.getInstance();
  }

  @Permission(Permissions.GUARD)
  @Command(value = "kos set <time> <player>", requiredSender = PlayerSource.class)
  @CommandDescription("Places a player on KOS for the specified amount of time.")
  public void handleRootKOSCommand(final CommandSender sender,
      @Argument(value = "time", suggestions = "time", description = "The amount of time to give the player.") final int time,
      @Argument(value = "player", suggestions = "player", description = "The player to give KOS to.") final String playerName) {
    Player guard = (Player) sender;
    Player player = Bukkit.getPlayer(playerName);
    if (player == null) {
      sender.sendMessage("That player does not exist.");
      return;
    }

    if (time != 5 && time != 10) {
      sender.sendMessage("Time must be 5 or 10.");
      return;
    }

    long ticks = time * 60 * 20; // convert minutes to ticks (20 ticks = 1 second)

    if (kosTimer.isKOSTimerActive(player)) {
      TextComponent response = Component.text().color(NamedTextColor.RED)
          .content(String.format("%s is already on KOS!", player.getName()))
          .build();
      sender.sendMessage(response);
      return;
    }

    kosTimer.setKOSTimer(player, guard, time);
    Component serverMessage = Component.text().color(NamedTextColor.RED)
        .content(String.format("%s is KOS for %d minutes!", player.getName(), time))
        .build();
    Bukkit.broadcast(serverMessage);

    Component userMessage = Component.text().color(NamedTextColor.GOLD).content(String.format(
        "You are on KOS for %d minutes! Use '/kos' to check your remaining time.",
        time)).build();
    player.sendMessage(userMessage);

    // Start KOS timer
    GuardUtils.getPlugin().getServer().getScheduler().runTaskLater(GuardUtils.getPlugin(),
        () -> {
          if (kosTimer.isKOSTimerActive(player)) {
            GuardUtils.getMyLogger().sendDebug(
                String.format("KOS timer for %s ended.", player.getName()));
            kosTimer.cancelKOSTimer(player);
          } else {
            GuardUtils.getMyLogger().sendDebug(String.format(
                "KOS timer for %s ended but the KOS timer was not active. Most likely the timer was cancelled.",
                player.getName()));
          }
        }, ticks);
  }

  @Permission(Permissions.GUARD)
  @Command(value = "kos cancel <kosPlayer>", requiredSender = PlayerSource.class)
  @CommandDescription("Cancels a player's KOS timer.")
  public void handleCancelKOSCommand(final CommandSender sender,
      @Argument(value = "kosPlayer", suggestions = "kosPlayer", description = "The player to check the KOS time of") final String playerName) {
    Player player = Bukkit.getPlayer(playerName);
    if (player == null) {
      sender.sendMessage("That player does not exist.");
      return;
    }

    if (!kosTimer.isKOSTimerActive(player)) {
      sender.sendMessage(String.format("%s is not on KOS!", player.getName()));
      return;
    }

    kosTimer.cancelKOSTimer(player);
  }

  @Permission(value = { Permissions.PLAYER, Permissions.GUARD }, mode = Permission.Mode.ANY_OF)
  @Command(value = "kos time [kosPlayer]", requiredSender = PlayerSource.class)
  @CommandDescription("Checks the time left on a player's KOS timer.")
  public void handleTimeCommand(final CommandSender sender,
      @Argument(value = "kosPlayer", suggestions = "kosPlayer", description = "The player to check the KOS time of") @Default("-") final String playerName) {
    Player senderPlayer = (Player) sender;
    Player player;
    if (playerName.equals("-")) {
      player = senderPlayer;
    } else {
      player = Bukkit.getPlayer(playerName);
    }

    if (player == null) {
      sender.sendMessage("That player does not exist.");
      return;
    }

    if (!kosTimer.isKOSTimerActive(player)) {
      sender.sendMessage(String.format("%s is not KOS!", player.getName()));
      return;
    }

    long endTime = kosTimer.getKOSTimer(player);
    long timeLeft = endTime - System.currentTimeMillis();
    if (timeLeft > 0) {
      MinSec minSec = TimeUtils.getMinutesAndSecondsFromMilli(timeLeft);
      TextComponent response = Component.text().color(NamedTextColor.GOLD)
          .content(
              String.format("%s has %d minutes and %d seconds left on KOS.",
                  player.getName(),
                  minSec.getMinutes(), minSec.getSeconds()))
          .build();
      sender.sendMessage(response);
    } else {
      TextComponent response = Component.text().color(NamedTextColor.GREEN)
          .content(String.format("%s has a KOS timer ending soom!", player.getName()))
          .build();
      sender.sendMessage(response);
    }
  }

  @Suggestions("time")
  public List<String> getTimeSuggestions(final CommandContext<PlayerSource> context) {
    GuardUtils.getMyLogger().sendDebug("checking time suggestions");
    return List.of("5", "10");
  }

  @Suggestions("player")
  public List<String> getPlayerSuggestions(final CommandContext<PlayerSource> context) {
    return ChatUtils.getOnlinePlayers(context);
  }

  @Suggestions("kosPlayer")
  public List<String> getKosPlayerSuggestions(final CommandContext<PlayerSource> context) {
    return ChatUtils.filterListByPrefix(kosTimer.getPlayerUsernamesOnKOS(), ChatUtils.getArgumentValue(context));
  }
}