package com.flyerzrule.mc.guardutils.duty.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.common.gui.GuardPanel;

public class GuardCommand implements CommandExecutor {
  public GuardCommand() {
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!cmd.getName().equalsIgnoreCase("guard")) {
      return true;
    }

    if (!(sender instanceof Player)) {
      sender.sendMessage("You must be a player to use this command.");
      return true;
    }

    Player player = (Player) sender;

    if (!player.hasPermission("guardutils.guard")) {
      sender.sendMessage(
          "You do not have permission to use this command. If you would like to become a guard, please contact an admin.");
      return true;
    }

    GuardUtils.getMyLogger().sendDebug(String.format("Player %s is opening the guard menu", player.getName()));
    // Open confirmation GUI
    new GuardPanel(player);

    return true;
  }
}
