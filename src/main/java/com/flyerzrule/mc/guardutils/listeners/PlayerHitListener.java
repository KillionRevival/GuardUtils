package com.flyerzrule.mc.guardutils.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.flyerzrule.mc.guardutils.utils.Message;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerHitListener {
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        // Make sure both entities are players
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player damagedPlayer = (Player) event.getEntity();
        Player attackingPlayer = (Player) event.getDamager();

        if (damagedPlayer.hasPermission("guardutils.guard")) {

            TextComponent message = Message.formatMessage(NamedTextColor.DARK_PURPLE,
                    String.format("%s has hit you!", attackingPlayer.getName()));
            damagedPlayer.sendMessage(message);
        }
    }
}
