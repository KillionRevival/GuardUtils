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

import java.util.List;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        List<Material> blacklist = List.of(
                Material.ENDER_CHEST,
                Material.ANVIL,
                Material.CHIPPED_ANVIL,
                Material.DAMAGED_ANVIL,
                Material.BREWING_STAND,
                Material.DISPENSER,
                Material.DROPPER,
                Material.SMOKER,
                Material.BLAST_FURNACE,
                Material.FURNACE,
                Material.HOPPER
        );

        Block block = event.getClickedBlock();
        if (block == null || !blacklist.contains(block.getType())) return;

        Player player = event.getPlayer();

        if (GuardDuty.isOnDuty(player)) {
            player.sendMessage(Component.text("You cannot use this item while on duty!", NamedTextColor.RED));
            event.setCancelled(true);
        }
    }

}
