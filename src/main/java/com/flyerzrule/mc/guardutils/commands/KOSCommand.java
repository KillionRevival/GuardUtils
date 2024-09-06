package com.flyerzrule.mc.guardutils.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.utils.KOSTimer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class KOSCommand implements CommandExecutor {

    private final String permissionErrorMessage = "You do not have permission to use this command.";

    public KOSCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kos")) {
            // Args length 1 and is time or no args
            if ((args.length == 1 && args[0].equalsIgnoreCase("time")) || args.length == 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("guardutils.player")) {
                        if (KOSTimer.isKOSTimerActive(player)) {
                            long timeLeft = KOSTimer.getKOSTimer(player);
                            if (timeLeft > 0) {
                                TextComponent response = Component.text().color(NamedTextColor.GOLD)
                                        .content(String.format("You have %0.2f minutes left on KOS.",
                                                ((float) timeLeft / 1000 / 60)))
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
                        sender.sendMessage(permissionErrorMessage);
                        return true;
                    }
                } else {
                    sender.sendMessage("You must be a player to use this command.");
                    return true;
                }
            } else if (args.length == 2) {
                Player player = Bukkit.getPlayer(args[1]);

                if (player != null) {
                    if (player.hasPermission("guardutils.guard")) {
                        if (args[0].equalsIgnoreCase("5") || args[0].equalsIgnoreCase("10")) {
                            int time = Integer.parseInt(args[0]);
                            long ticks = time * 60 * 20; // convert minutes to ticks (20 ticks = 1 second)
                            KOSTimer.setKOSTimer(player, time);
                            Component serverMessage = Component.text().color(NamedTextColor.RED)
                                    .content(String.format("%s is KOS for %d minutes!", player.getName(), time))
                                    .build();
                            Bukkit.broadcast(serverMessage);

                            Component userMessage = Component.text().color(NamedTextColor.GOLD).content(String.format(
                                    "You are on KOS for %d minutes! Use '/kos time' to check your remaining time.",
                                    time)).build();
                            sender.sendMessage(userMessage);

                            // Start KOS timer
                            GuardUtils.getPlugin().getServer().getScheduler().runTaskLater(GuardUtils.getPlugin(),
                                    () -> {
                                        if (KOSTimer.isKOSTimerActive(player)) {
                                            GuardUtils.getMyLogger().sendDebug(
                                                    String.format("KOS timer for %s ended.", player.getName()));
                                            KOSTimer.cancelKOSTimer(player);

                                            Component message = Component.text().color(NamedTextColor.GREEN)
                                                    .content(String.format("KOS has ended for %s!", player.getName()))
                                                    .build();
                                            Bukkit.broadcast(message);
                                        } else {
                                            GuardUtils.getMyLogger().sendDebug(String.format(
                                                    "KOS timer for %s ended but the KOS timer was not active. Most likely the timer was cancelled.",
                                                    player.getName()));
                                        }
                                    }, ticks);
                            return true;
                        } else if (args[0].equalsIgnoreCase("cancel")) {
                            KOSTimer.cancelKOSTimer(player);
                            Component serverMessage = Component.text().color(NamedTextColor.GREEN)
                                    .content(String.format("KOS has ended for %s!", player.getName())).build();
                            Bukkit.broadcast(serverMessage);

                            Component userMessage = Component.text().color(NamedTextColor.GOLD)
                                    .content("Your KOS timer was cancelled.").build();
                            player.sendMessage(userMessage);
                            return true;
                        } else if (args[0].equalsIgnoreCase("time")) {
                            if (KOSTimer.isKOSTimerActive(player)) {
                                long timeLeft = KOSTimer.getKOSTimer(player);
                                if (timeLeft > 0) {
                                    TextComponent response = Component.text().color(NamedTextColor.GOLD)
                                            .content(
                                                    String.format("%s has %0.2f minutes left on KOS.", player.getName(),
                                                            ((float) timeLeft / 1000 / 60)))
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
                        }
                    } else {
                        sender.sendMessage(permissionErrorMessage);
                        return true;
                    }
                } else {
                    sender.sendMessage("Player not found.");
                    return false;
                }
            }

        }
        return false;
    }
}
