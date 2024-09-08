package com.flyerzrule.mc.guardutils.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.flyerzrule.mc.guardutils.kos.KOSTimer;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getEntity();
        Player killer = event.getEntity().getKiller();

        // Killer was not a player
        if (killer == null) {
            return;
        }

        KOSTimer kosTimer = KOSTimer.getInstance();
        if (kosTimer.isKOSTimerActive(deadPlayer)) {
            kosTimer.cancelKOSTimer(deadPlayer);
        }

    }
}
