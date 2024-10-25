package com.flyerzrule.mc.guardutils.duty.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.flyerzrule.mc.guardutils.duty.utils.GuardDuty;

public class GuardKillDeathListener implements Listener {
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player deadPlayer = event.getEntity();
    Player killer = event.getEntity().getKiller();

    // Killer was not a player
    if (killer == null) {
      return;
    }

    if (GuardDuty.isOnDuty(deadPlayer)) {
      GuardDuty.addGuardDeath(deadPlayer);
    } else if (GuardDuty.isOnDuty(killer)) {
      GuardDuty.addGuardKill(killer);
    }
  }
}
