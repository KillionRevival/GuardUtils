package com.flyerzrule.mc.guardutils.duty.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.util.sender.PlayerSource;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.database.SavedPlayerInfoDao;
import com.flyerzrule.mc.guardutils.database.models.SavedPlayerInfo;
import com.flyerzrule.mc.guardutils.duty.models.CLAN_RANK;
import com.flyerzrule.mc.guardutils.duty.models.RANK;
import com.flyerzrule.mc.guardutils.utils.ChatUtils;
import com.flyerzrule.mc.guardutils.utils.Permissions;

public class AddAdminGuardCommand {
  private final SavedPlayerInfoDao savedPlayerInfoDao;

  public AddAdminGuardCommand() {
    this.savedPlayerInfoDao = SavedPlayerInfoDao.getInstance();
  }

  @Permission(Permissions.ADMIN)
  @Command(value = "addadminguard [player]", requiredSender = PlayerSource.class)
  @CommandDescription("Adds a player to the guard table so they dont need to use the guard signs")
  public void handlerAddAdminGuardCommand(final CommandSender sender,
      @Argument(value = "player", suggestions = "player", description = "The player to add to the guard table") @Default("-") final String playerName) {

    Player senderPlayer = (Player) sender;
    Player player;
    if (playerName.equals("-")) {
      player = senderPlayer;
    } else {
      player = Bukkit.getPlayer(playerName);
    }

    if (player == null) {
      sender.sendMessage("That player does not exist.");
      return;
    }

    SavedPlayerInfo savedPlayerInfo = new SavedPlayerInfo(player.getUniqueId().toString(), RANK.UNKNOWN, "adminAccount",
        CLAN_RANK.UNKNOWN, 0, null);

    savedPlayerInfoDao.addPlayerInfo(savedPlayerInfo);

    if (playerName.equals("-")) {
      player.sendMessage(String.format(
          "You have been added to the guard table. You no longer need to use the Guard signs. You will always be able to use the guard commands."));
    } else {
      player.sendMessage(String.format(
          "%s has been added to the guard table. They no longer need to use the Guard signs. They will always be able to use the guard commands.",
          playerName));
    }

    GuardUtils.getMyLogger().sendInfo(String.format(
        "Added %s to the guard table. They no longer need to use the Guard signs. They will always be able to use the guard commands.",
        player.getName()));
  }

  @Suggestions("player")
  public List<String> getPlayerSuggestions(final CommandContext<PlayerSource> context) {
    return ChatUtils.getOnlinePlayers(context, true);
  }
}
