package com.flyerzrule.mc.guardutils.duty.listeners;

import com.flyerzrule.mc.guardutils.duty.GuardDuty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.ENDER_CHEST) return;

        Player player = event.getPlayer();

        if (GuardDuty.isOnDuty(player)) {
            player.sendMessage(Component.text("You cannot open Enderchests while on duty!", NamedTextColor.RED));
            event.setCancelled(true);
        }
    }

}
