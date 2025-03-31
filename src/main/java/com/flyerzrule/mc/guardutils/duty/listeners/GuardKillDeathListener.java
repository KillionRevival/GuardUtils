package com.flyerzrule.mc.guardutils.duty.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.database.GuardStatsDao;
import com.flyerzrule.mc.guardutils.duty.GuardDuty;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.crate.impl.Crate;

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

    GuardUtils.getMyLogger().sendDebug(String.format("Player %s died. Killer was %s", deadPlayer.getName(),
        killer.getName()));

    if (GuardDuty.isOnDuty(deadPlayer)) {
      guardStatsDao.incrementDeaths(deadPlayer.getUniqueId().toString());

      // Drop guard crate
       CratesPlugin cratesPlugin = GuardUtils.getCratesPlugin();
       String guardCrateId = GuardUtils.getPlugin().getConfig().getString("guard-crate-id");

       if (guardCrateId == null || guardCrateId.isEmpty()) {
           GuardUtils.getMyLogger().sendWarning("guard-crate-id is not configured!");
       } else {
           Crate guardCrate = cratesPlugin.getCrateManager().getCrateById(guardCrateId);
           if (guardCrate != null) {
               cratesPlugin.getCrateManager().dropCrateItem(guardCrate, deadPlayer.getLocation());
               GuardUtils.getMyLogger().sendDebug(String.format("Crate %s dropped at x: %f y: %f z: %f", guardCrateId, deadPlayer.getLocation().getX(), deadPlayer.getLocation().getY(), deadPlayer.getLocation().getZ()));
           } else {
               GuardUtils.getMyLogger().sendError(String.format("Crate %s not found", guardCrateId));
           }
       }
    } else if (GuardDuty.isOnDuty(killer)) {
      guardStatsDao.incrementKills(killer.getUniqueId().toString());
    }
  }
}
