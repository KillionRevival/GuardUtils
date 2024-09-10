package com.flyerzrule.mc.guardutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.utils.Message;

import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;

public class ScoreboardToggleCommand implements CommandExecutor {
    public ScoreboardToggleCommand() {
    }

    private final String permission = "guardutils.guard.scoreboard";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("guardsb")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("guardutils.guard")) {
                    if (args.length == 1) {
                        boolean hasPermission = player.hasPermission(permission);
                        if (args[0].equalsIgnoreCase("on")) {
                            if (!hasPermission) {
                                enablePermission(player);
                                player.sendMessage(Message.formatMessage(NamedTextColor.GOLD,
                                        "You have enabled the guard scoreboard!"));
                                return true;
                            } else {
                                player.sendMessage(Message.formatMessage(NamedTextColor.DARK_RED,
                                        "You already have the guard scoreboard enabled!"));
                                return true;
                            }
                        } else if (args[0].equalsIgnoreCase("off")) {
                            if (hasPermission) {
                                disablePermission(player);
                                player.sendMessage(Message.formatMessage(NamedTextColor.GOLD,
                                        "You have disabled the guard scoreboard!"));
                                return true;
                            } else {
                                player.sendMessage(Message.formatMessage(NamedTextColor.DARK_RED,
                                        "You do not have the guard scoreboard enabled!"));
                                return true;
                            }
                        }
                    } else {
                        return false;
                    }
                } else {
                    sender.sendMessage("You do not have permission to use this command.");
                    return true;
                }
            } else {
                sender.sendMessage("You must be a player to use this command.");
                return true;
            }
        }
        return true;
    }

    private void disablePermission(Player player) {
        LuckPerms luckPerms = GuardUtils.getLuckperms();
        luckPerms.getUserManager().loadUser(player.getUniqueId()).thenAccept(user -> {
            Node node = Node.builder(permission).build();
            if (user.data().contains(node, NodeEqualityPredicate.ONLY_KEY).asBoolean()) {
                user.data().remove(node);
                Node falseNode = Node.builder(permission).value(false).build();
                user.data().add(falseNode);
            } else {
                Node falseNode = Node.builder(permission).value(false).build();
                user.data().add(falseNode);
            }

            // Save changes to LuckPerms
            luckPerms.getUserManager().saveUser(user);
        });
    }

    private void enablePermission(Player player) {
        LuckPerms luckPerms = GuardUtils.getLuckperms();
        luckPerms.getUserManager().loadUser(player.getUniqueId()).thenAccept(user -> {
            Node node = Node.builder(permission).build();
            if (user.data().contains(node, NodeEqualityPredicate.ONLY_KEY).asBoolean()) {
                user.data().remove(node);
                Node trueNode = Node.builder(permission).value(true).build();
                user.data().add(trueNode);
            } else {
                Node trueNode = Node.builder(permission).value(true).build();
                user.data().add(trueNode);
            }

            // Save changes to LuckPerms
            luckPerms.getUserManager().saveUser(user);
        });
    }
}
