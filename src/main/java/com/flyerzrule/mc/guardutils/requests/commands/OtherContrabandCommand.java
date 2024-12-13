package com.flyerzrule.mc.guardutils.requests.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.util.sender.PlayerSource;

import com.flyerzrule.mc.guardutils.requests.Requests;
import com.flyerzrule.mc.guardutils.requests.models.ContrabandType;
import com.flyerzrule.mc.guardutils.requests.models.Item;
import com.flyerzrule.mc.guardutils.requests.utils.Utils;
import com.flyerzrule.mc.guardutils.utils.ChatUtils;
import com.flyerzrule.mc.guardutils.utils.Message;
import com.flyerzrule.mc.guardutils.utils.Permissions;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class OtherContrabandCommand {
  public OtherContrabandCommand() {
  }

  @Permission(Permissions.GUARD)
  @Command(value = "cb <player> <item>", requiredSender = PlayerSource.class)
  @CommandDescription("Request a player to drop their other contraband")
  public void handlerBowCommand(final CommandSender sender,
      @Argument(value = "player", suggestions = "player", description = "The player to request to their other contraband") final String playerName,
      @Argument(value = "item", suggestions = "item", description = "The type of other contraband to ask the player to drop") final String item) {
    Player player = Bukkit.getPlayer(playerName);
    Player guard = (Player) sender;
    if (player == null) {
      sender.sendMessage("That player does not exist.");
      return;
    }

    List<Item> otherPossibleContraband = Utils.getContrandTypeItems(ContrabandType.OTHER);

    List<Item> contrabandItems = otherPossibleContraband.stream()
        .filter((ele) -> ele.getName().equals(item)).collect(Collectors.toList());

    if (contrabandItems == null || contrabandItems.isEmpty()) {
      TextComponent message = Message.formatMessage(NamedTextColor.RED,
          "That is not a valid contraband item.");
      sender.sendMessage(message);
      return;
    }

    for (Item contrabandItem : contrabandItems) {
      if (Utils.hasOtherContrabandItemInInventory(player, contrabandItem)) {
        TextComponent message = Message.formatMessage(NamedTextColor.DARK_PURPLE,
            String.format(
                "Drop your %s or you will be sent to solitary for breaking the rules!",
                contrabandItem.getName()));
        player.sendMessage(message);

        Requests requests = Requests.getInstance();
        requests.addRequest(player, guard, ContrabandType.OTHER, contrabandItem);

        return;
      }
    }

    TextComponent message = Message.formatMessage(NamedTextColor.RED,
        String.format("%s does not have a %s in their inventory!", player.getName(),
            item));
    guard.sendMessage(message);
  }

  @Suggestions("player")
  public List<String> getPlayerSuggestions(final CommandContext<PlayerSource> context) {
    return ChatUtils.getOnlinePlayers(context);
  }

  @Suggestions("item")
  public List<String> getOtherContrabandSuggestions(final CommandContext<PlayerSource> context) {
    List<Item> otherPossibleContraband = Utils.getContrandTypeItems(ContrabandType.OTHER);

    String input = ChatUtils.getArgumentValue(context);

    List<String> possibleItems = otherPossibleContraband.stream()
        .map(Item::getName)
        .collect(Collectors.toList());

    return ChatUtils.filterListByPrefix(possibleItems, input);
  }
}
