package com.flyerzrule.mc.guardutils.duty.commands;

import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.PlayerSource;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.common.gui.panels.MainPanel;

public class GuardCommand {
  public GuardCommand() {
  }

  @Permission("guardutils.guard")
  @Command(value = "guard", requiredSender = PlayerSource.class)
  @CommandDescription("Opens the guard menu")
  public void onCommand(Player player) {
    GuardUtils.getMyLogger().sendDebug(String.format("Player %s is opening the guard menu", player.getName()));

    new MainPanel(player).open();
  }
}
