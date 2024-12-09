package com.flyerzrule.mc.guardutils.kos.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.kos.KOSTimer;
import com.flyerzrule.mc.guardutils.utils.time.TimeUtils;
import com.flyerzrule.mc.guardutils.utils.time.models.MinSec;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class KOSCommand implements CommandExecutor {

    public KOSCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kos")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must be a player to use this command.");
                return true;
            }

            Player guard = (Player) sender;

            KOSTimer kosTimer = KOSTimer.getInstance();

            if (!sender.hasPermission("guardutils.guard")) {
                if (kosTimer.isKOSTimerActive(guard)) {
                    long endTime = kosTimer.getKOSTimer(guard);
                    long timeLeft = endTime - System.currentTimeMillis();
                    if (timeLeft > 0) {
                        MinSec minSec = TimeUtils.getMinutesAndSecondsFromMilli(timeLeft);
                        TextComponent response = Component.text().color(NamedTextColor.GOLD)
                                .content(String.format("You have %d minutes and %d seconds left on KOS.",
                                        minSec.getMinutes(), minSec.getSeconds()))
                                .build();
                        sender.sendMessage(response);
                    } else {
                        TextComponent response = Component.text().color(NamedTextColor.GREEN)
                                .content("Your KOS status is ending soon!").build();
                        sender.sendMessage(response);
                    }
                } else {
                    TextComponent response = Component.text().color(NamedTextColor.RED)
                            .content("You do not have a KOS timer active.").build();
                    sender.sendMessage(response);
                }
                return true;
            } else {
                if (args.length == 2) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage("That player does not exist.");
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("5") || args[0].equalsIgnoreCase("10")) {
                        int time = Integer.parseInt(args[0]);

                        long ticks = time * 60 * 20; // convert minutes to ticks (20 ticks = 1 second)
                        if (kosTimer.isKOSTimerActive(player)) {
                            TextComponent response = Component.text().color(NamedTextColor.RED)
                                    .content(String.format("%s is already on KOS!", player.getName()))
                                    .build();
                            sender.sendMessage(response);
                            return true;
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
                        return true;
                    } else if (args[0].equalsIgnoreCase("cancel")) {
                        kosTimer.cancelKOSTimer(player);
                        return true;
                    } else if (args[0].equalsIgnoreCase("time")) {
                        if (kosTimer.isKOSTimerActive(player)) {
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
                        } else {
                            TextComponent response = Component.text().color(NamedTextColor.RED)
                                    .content(String.format("%s does not have a KOS timer active!", player.name()))
                                    .build();
                            sender.sendMessage(response);
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
