package com.flyerzrule.mc.guardutils.duty.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.duty.gui.GuardConfirmPanel;
import com.flyerzrule.mc.guardutils.duty.utils.GuardDuty;

public class RegisterAsGuardCommand implements CommandExecutor {

  public RegisterAsGuardCommand() {
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!cmd.getName().equalsIgnoreCase("guardRegister")) {
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

    if (GuardDuty.isOnDuty(player)) {
      sender.sendMessage("You are already on duty!");
      return true;
    }

    GuardUtils.getMyLogger().sendInfo(String.format("Player %s is now a guard", player.getName()));
    // Open confirmation GUI
    new GuardConfirmPanel(player, false);

    return true;
  }

}
