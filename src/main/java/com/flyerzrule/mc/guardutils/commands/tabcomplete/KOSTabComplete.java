package com.flyerzrule.mc.guardutils.commands.tabcomplete;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.utils.ChatUtils;
import com.flyerzrule.mc.guardutils.utils.KOSTimer;

public class KOSTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (!(sender instanceof Player)) {
            GuardUtils.getMyLogger().sendDebug("Cannot Tab complete KOS command. Player is not a player.");
            return suggestions;
        }

        Player player = (Player) sender;

        if (sender.hasPermission("guardutils.guard")) {
            if (args.length == 1) {
                suggestions.add("5");
                suggestions.add("10");
                suggestions.add("cancel");
                suggestions.add("time");

                suggestions = ChatUtils.filterListByPrefix(suggestions, args[0]);
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("cancel")) {
                    List<String> KOSUsernames = KOSTimer.getPlayerUsernamesOnKOS();
                    suggestions = ChatUtils.filterListByPrefix(KOSUsernames, args[1]);
                } else if (args[0].equalsIgnoreCase("5") || args[0].equalsIgnoreCase("10")) {
                    List<String> onlinePlayers = ChatUtils.getOnlinePlayers(player, args[1]);
                    List<String> KOSUsernames = KOSTimer.getPlayerUsernamesOnKOS();
                    suggestions = onlinePlayers.stream().filter(name -> !KOSUsernames.contains(name)).toList();
                }
            }
        }
        return suggestions;
    }
}
