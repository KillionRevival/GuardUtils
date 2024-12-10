package com.flyerzrule.mc.guardutils.duty.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.duty.gui.GuardConfirmPanel;
import com.flyerzrule.mc.guardutils.duty.utils.GuardDuty;

public class ResignFromGuardCommand implements CommandExecutor {
  public ResignFromGuardCommand() {

  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!cmd.getName().equalsIgnoreCase("guardResign")) {
      return true;
    }

    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player to use this command.");
      return true;
    }

    Player player = (Player) sender;

    if (!player.hasPermission("guardutils.canBeGuard")) {
      sender.sendMessage(
          "You do not have permission to use this command. If you would like to become a guard, please contact an admin.");
      return true;
    }

    if (!GuardDuty.isOnDuty(player)) {
      sender.sendMessage("You are not on duty!");
      return true;
    }

    // Open confirmation GUI
    new GuardConfirmPanel(player, true);

    return true;
  }

}
