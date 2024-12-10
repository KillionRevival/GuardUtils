package com.flyerzrule.mc.guardutils.requests.commands.tabcomplete;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.utils.ChatUtils;

public class PlayerTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;

        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions = ChatUtils.getOnlinePlayers(player, args[0]);
        }
        return suggestions;
    }
}
