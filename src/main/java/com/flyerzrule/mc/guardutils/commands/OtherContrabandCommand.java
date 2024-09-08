package com.flyerzrule.mc.guardutils.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.requests.Requests;
import com.flyerzrule.mc.guardutils.requests.models.ContrabandType;
import com.flyerzrule.mc.guardutils.requests.models.Item;
import com.flyerzrule.mc.guardutils.utils.Message;
import com.flyerzrule.mc.guardutils.utils.Utils;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class OtherContrabandCommand implements CommandExecutor {
    private final String permissionErrorMessage = "You do not have permission to use this command.";

    public OtherContrabandCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("contraband")) {
            if (sender instanceof Player) {
                Player guard = (Player) sender;
                if (guard.hasPermission("guardutils.guard")) {
                    if (args.length == 2) {
                        Player player = Bukkit.getPlayer(args[0]);
                        if (player == null) {
                            sender.sendMessage("That player does not exist.");
                            return false;
                        }

                        List<Item> otherPossibleContraband = Utils.getContrandTypeItems(ContrabandType.OTHER);

                        Item contrabandItem = otherPossibleContraband.stream()
                                .filter((ele) -> ele.getName().equals(args[1])).findFirst().orElse(null);

                        if (contrabandItem == null) {
                            TextComponent message = Message.formatMessage(NamedTextColor.RED,
                                    "That is not a valid contraband item.");
                            sender.sendMessage(message);
                            return false;
                        }

                        if (Utils.hasOtherContrabandItemInInventory(player, contrabandItem)) {
                            TextComponent message = Message.formatMessage(NamedTextColor.GOLD,
                                    String.format(
                                            "Drop your %s or you will be sent to solitary for breaking the rules!",
                                            contrabandItem.getName()));
                            player.sendMessage(message);

                            Requests requests = Requests.getInstance();
                            requests.addRequest(player, guard, ContrabandType.OTHER, contrabandItem);
                            return true;
                        } else {
                            TextComponent message = Message.formatMessage(NamedTextColor.RED,
                                    String.format("%s does not have a %s in their inventory!", player.getName(),
                                            contrabandItem.getName()));
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
