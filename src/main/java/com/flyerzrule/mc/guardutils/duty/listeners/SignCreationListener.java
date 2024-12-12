package com.flyerzrule.mc.guardutils.duty.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.flyerzrule.mc.guardutils.duty.models.SignCommands;
import com.flyerzrule.mc.guardutils.utils.Permissions;

import net.md_5.bungee.api.ChatColor;

public class SignCreationListener implements Listener {

  @EventHandler
  public void onSignChange(SignChangeEvent event) {
    Player player = event.getPlayer();
    Block block = event.getBlock();

    if (block.getType().name().toLowerCase().contains("sign")) {
      if (!event.getSide().equals(Side.FRONT) || event.line(0).toString().isEmpty()) {
        return;
      }

      String line1Contents = ChatColor.stripColor(event.line(0).toString());

      if (SignCommands.COMMANDS.contains(line1Contents)) {

        if (!player.hasPermission(Permissions.ADMIN)) {
          event.setCancelled(true);
          player.sendMessage(ChatColor.RED + "You do not have permission to create this type of sign.");
          return;
        }

        player.sendMessage(ChatColor.GREEN + "Command sign created successfully!");
      }
    }
  }
}