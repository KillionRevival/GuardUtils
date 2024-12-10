package com.flyerzrule.mc.guardutils.requests.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.requests.Requests;
import com.flyerzrule.mc.guardutils.requests.models.ContrabandType;
import com.flyerzrule.mc.guardutils.requests.utils.Utils;
import com.flyerzrule.mc.guardutils.utils.Message;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class SwordCommand implements CommandExecutor {
    private final String permissionErrorMessage = "You do not have permission to use this command.";

    public SwordCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("sword")) {
            if (sender instanceof Player) {
                Player guard = (Player) sender;
                if (guard.hasPermission("guardutils.guard")) {
                    if (args.length == 1) {
                        Player player = Bukkit.getPlayer(args[0]);
                        if (player == null) {
                            sender.sendMessage("That player does not exist.");
                            return false;
                        }

                        if (Utils.hasContrabrandInInventory(player, ContrabandType.SWORD)) {
                            player.sendMessage(Message.formatMessage(NamedTextColor.DARK_PURPLE,
                                    "Drop your sword or you will be sent to solitary for breaking the rules!"));

                            Requests requests = Requests.getInstance();
                            requests.addRequest(player, guard, ContrabandType.SWORD, null);
                            return true;
                        } else {
                            TextComponent message = Message.formatMessage(NamedTextColor.RED,
                                    String.format("%s does not have a sword in their inventory!", player.getName()));
                            guard.sendMessage(message);
                            return true;
                        }

                    } else {
                        return false;
                    }
                } else {
                    sender.sendMessage(permissionErrorMessage);
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
