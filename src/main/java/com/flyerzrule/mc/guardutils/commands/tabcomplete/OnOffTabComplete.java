package com.flyerzrule.mc.guardutils.commands.tabcomplete;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;

public class OnOffTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (!(sender instanceof Player)) {
            GuardUtils.getMyLogger().sendDebug("Cannot Tab complete KOS command. Player is not a player.");
            return suggestions;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("guardsb")) {
            if (sender.hasPermission("guardutils.guard")) {
                if (args.length == 1) {
                    if (player.hasPermission("guardutils.guard.scoreboard")) {
                        suggestions.add("off");
                    } else {
                        suggestions.add("on");
                    }
                }
            }
        }
        return suggestions;
    }
}
