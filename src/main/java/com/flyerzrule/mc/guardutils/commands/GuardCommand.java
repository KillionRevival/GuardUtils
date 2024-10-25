package com.flyerzrule.mc.guardutils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.duty.gui.GuardPanel;
import com.flyerzrule.mc.guardutils.duty.utils.GuardDuty;

public class GuardCommand implements CommandExecutor {
  public GuardCommand() {
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("guard")) {
      if (sender instanceof Player) {
        Player player = (Player) sender;
        if (player.hasPermission("guardutils.canBeGuard")) {
          if (args.length == 0) {
            GuardPanel guardPanel = new GuardPanel(player);
            guardPanel.openMain();
          } else {
            if (args[0].equalsIgnoreCase("switch")) {
              GuardDuty.switchDuty(player);
            }
          }
        } else {
          sender.sendMessage(
              "You do not have permission to use this command. If you would like to become a guard, please contact an administrator.");
          return true;
        }
      } else {
        sender.sendMessage("You must be a player to use this command.");
        return true;
      }
    }
    return true;
  }
}
