package com.flyerzrule.mc.guardutils.requests.commands;

import java.util.List;

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
import com.flyerzrule.mc.guardutils.requests.utils.Utils;
import com.flyerzrule.mc.guardutils.utils.ChatUtils;
import com.flyerzrule.mc.guardutils.utils.Message;
import com.flyerzrule.mc.guardutils.utils.Permissions;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class SwordCommand {

  public SwordCommand() {
  }

  @Permission(Permissions.GUARD)
  @Command(value = "sword <player>", requiredSender = PlayerSource.class)
  @CommandDescription("Request a player to drop their sword")
  public void handlerBowCommand(final CommandSender sender,
      @Argument(value = "player", suggestions = "player", description = "The player to request to their sword") final String playerName) {
    Player player = Bukkit.getPlayer(playerName);
    Player guard = (Player) sender;
    if (player == null) {
      sender.sendMessage("That player does not exist.");
      return;
    }

    if (Utils.hasContrabrandInInventory(player, ContrabandType.SWORD)) {
      player.sendMessage(Message.formatMessage(NamedTextColor.DARK_PURPLE,
          "Drop your sword or you will be sent to solitary for breaking the rules!"));

      Requests requests = Requests.getInstance();
      requests.addRequest(player, guard, ContrabandType.SWORD, null);
      return;
    } else {
      TextComponent message = Message.formatMessage(NamedTextColor.RED,
          String.format("%s does not have a sword in their inventory!", player.getName()));
      guard.sendMessage(message);
      return;
    }
  }

  @Suggestions("player")
  public List<String> getPlayerSuggestions(final CommandContext<PlayerSource> context) {
    return ChatUtils.getOnlinePlayers(context);
  }
}
