package com.flyerzrule.mc.guardutils.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.scoreboard.GuardScoreboard;

public class PlayerHitListener implements Listener {
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        // Make sure both entities are players
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player damagedPlayer = (Player) event.getEntity();
        Player attackingPlayer = (Player) event.getDamager();

        GuardUtils.getMyLogger().sendDebug(String.format("%s has hit %s!", attackingPlayer.getName(),
                damagedPlayer.getName()));

        GuardScoreboard guardScoreboard = GuardScoreboard.getInstance();
        guardScoreboard.addAttack(damagedPlayer, attackingPlayer);
    }
}
