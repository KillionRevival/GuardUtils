package com.flyerzrule.mc.guardutils.duty.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.duty.GuardDuty;
import com.flyerzrule.mc.guardutils.duty.gui.GuardConfirmPanel;
import com.flyerzrule.mc.guardutils.duty.models.RANK;
import com.flyerzrule.mc.guardutils.duty.models.SignCommands;
import com.flyerzrule.mc.guardutils.utils.Permissions;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class SignCommandListener implements Listener {
  @EventHandler
  public void onSignClick(PlayerInteractEvent event) {
    // Check if the player clicked on a block
    if (event.getClickedBlock() == null) {
      return;
    }

    Block block = event.getClickedBlock();

    if (block.getType().name().toLowerCase().contains("sign")) {

      Sign sign = (Sign) event.getClickedBlock().getState();

      String line1Contents = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(0));
      line1Contents = ChatColor.stripColor(line1Contents);

      Player player = event.getPlayer();

      if (!line1Contents.equals(SignCommands.REGISTER_COMMAND) && !line1Contents.equals(SignCommands.RESIGN_COMMAND)) {
        return;
      }

      if (SignCommands.COMMANDS.contains(line1Contents) && !player.hasPermission(Permissions.CAN_BE_GUARD)) {
        player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
        return;
      }

      // Check if the player is the either Free or Citizen rank
      if (!Permissions.getGroups(player).contains(RANK.FREE.getGroupName())
          && !Permissions.getGroups(player).contains(RANK.CITIZEN.getGroupName())
          && !Permissions.getGroups(player).contains("guard")) {
        player.sendMessage(ChatColor.RED + "You must be Free or Citizen to use this command.");
        return;
      }

      // Check if the sign contains the trigger text
      if (line1Contents.equals(SignCommands.REGISTER_COMMAND)) {
        if (GuardDuty.isOnDuty(player)) {
          player.sendMessage("You are already on duty!");
          return;
        }

        GuardUtils.getMyLogger().sendDebug(String.format("Player %s clicked the register sign", player.getName()));

        // Open confirmation GUI
        new GuardConfirmPanel(player, false).open();
      } else if (line1Contents.equals(SignCommands.RESIGN_COMMAND)) {
        if (!GuardDuty.isOnDuty(player)) {
          player.sendMessage("You are not on duty!");
          return;
        }

        GuardUtils.getMyLogger().sendDebug(String.format("Player %s clicked the resign sign", player.getName()));

        event.setCancelled(true);
        // Open confirmation GUI
        new GuardConfirmPanel(player, true).open();
      }
    }
  }

}
