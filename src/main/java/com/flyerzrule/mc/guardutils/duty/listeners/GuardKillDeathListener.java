package com.flyerzrule.mc.guardutils.duty.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.flyerzrule.mc.guardutils.database.GuardStatsDao;
import com.flyerzrule.mc.guardutils.duty.utils.GuardDuty;

public class GuardKillDeathListener implements Listener {
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player deadPlayer = event.getEntity();
    Player killer = event.getEntity().getKiller();

    GuardStatsDao guardStatsDao = GuardStatsDao.getInstance();

    // Killer was not a player
    if (killer == null) {
      return;
    }

    if (GuardDuty.isOnDuty(deadPlayer)) {
      guardStatsDao.incrementDeaths(deadPlayer.getUniqueId().toString());
    } else if (GuardDuty.isOnDuty(killer)) {
      guardStatsDao.incrementKills(killer.getUniqueId().toString());
    }
  }
}
