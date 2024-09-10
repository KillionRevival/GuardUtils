package com.flyerzrule.mc.guardutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.invis.InvisPlayers;
import com.flyerzrule.mc.guardutils.utils.Message;

import net.kyori.adventure.text.format.NamedTextColor;

public class GuardInvisTagCommand implements CommandExecutor {
    public GuardInvisTagCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("guardsinvistag")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                InvisPlayers invisPlayers = InvisPlayers.getInstance();
                if (player.hasPermission("guardutils.admin")) {
                    if (args.length == 1) {
                        boolean isEnabled = invisPlayers.getshowGuardTag();
                        if (args[0].equalsIgnoreCase("on")) {
                            if (!isEnabled) {
                                invisPlayers.setshowGuardTag(true);
                                player.sendMessage(Message.formatMessage(NamedTextColor.GOLD,
                                        "You have enabled the guard invis tags!"));
                                return true;
                            } else {
                                player.sendMessage(Message.formatMessage(NamedTextColor.DARK_RED,
                                        "You already have the guard invis tags enabled!"));
                                return true;
                            }
                        } else if (args[0].equalsIgnoreCase("off")) {
                            if (isEnabled) {
                                invisPlayers.setshowGuardTag(false);
                                player.sendMessage(Message.formatMessage(NamedTextColor.GOLD,
                                        "You have disabled the guard invis tags!"));
                                return true;
                            } else {
                                player.sendMessage(Message.formatMessage(NamedTextColor.DARK_RED,
                                        "You do not have the guard invis tags enabled!"));
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
}
