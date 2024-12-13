package com.flyerzrule.mc.guardutils.duty.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.duty.models.SignCommands;
import com.flyerzrule.mc.guardutils.utils.Permissions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class SignCreationListener implements Listener {

  @EventHandler
  public void onSignChange(SignChangeEvent event) {
    Player player = event.getPlayer();
    Block block = event.getBlock();

    GuardUtils.getMyLogger().sendDebug(String.format("Block type: %s", block.getType().name()));
    if (block.getType().name().toLowerCase().contains("sign")) {
      String line1Contents = PlainTextComponentSerializer.plainText().serialize(event.line(0));
      line1Contents = ChatColor.stripColor(line1Contents);

      if (!event.getSide().equals(Side.FRONT) || line1Contents.isEmpty()) {
        return;
      }

      GuardUtils.getMyLogger().sendDebug(String.format("Line 1 contents: %s", line1Contents));

      if (SignCommands.COMMANDS.contains(line1Contents)) {

        if (!player.hasPermission(Permissions.ADMIN)) {
          event.setCancelled(true);
          player.sendMessage(ChatColor.RED + "You do not have permission to create this type of sign.");
          return;
        }

        if (line1Contents.equals(SignCommands.REGISTER_COMMAND)) {
          event.line(0, Component.text(SignCommands.REGISTER_COMMAND, NamedTextColor.GREEN));
        } else if (line1Contents.equals(SignCommands.RESIGN_COMMAND)) {
          event.line(0, Component.text(SignCommands.RESIGN_COMMAND, NamedTextColor.RED));
        }

        Sign sign = (Sign) block.getState();
        sign.setWaxed(true);
        sign.update();

        player.sendMessage(ChatColor.GREEN + "Command sign created successfully!");
      }
    }
  }
}