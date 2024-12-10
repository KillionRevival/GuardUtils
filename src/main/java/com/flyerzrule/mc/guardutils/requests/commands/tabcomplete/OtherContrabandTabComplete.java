package com.flyerzrule.mc.guardutils.requests.commands.tabcomplete;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.requests.models.ContrabandType;
import com.flyerzrule.mc.guardutils.requests.models.Item;
import com.flyerzrule.mc.guardutils.requests.utils.Utils;
import com.flyerzrule.mc.guardutils.utils.ChatUtils;

public class OtherContrabandTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;

        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions = ChatUtils.getOnlinePlayers(player, args[0]);
        } else if (args.length == 2) {
            List<Item> otherPossibleContraband = Utils.getContrandTypeItems(ContrabandType.OTHER);
            suggestions = otherPossibleContraband.stream()
                    .map(Item::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return suggestions;
    }

}
