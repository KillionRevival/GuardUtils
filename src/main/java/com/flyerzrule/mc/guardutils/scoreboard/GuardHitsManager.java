package com.flyerzrule.mc.guardutils.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.flyerzrule.mc.guardutils.GuardUtils;

public class GuardHitsManager {
  private static GuardHitsManager instance;

  private Map<UUID, Map<UUID, List<Long>>> attackData;
  private final long hitTimeout;

  private GuardHitsManager() {
    this.attackData = new HashMap<>();
    this.hitTimeout = GuardUtils.getPlugin().getConfig().getInt("hit-timeout", 120) * 1000;

    new BukkitRunnable() {
      @Override
      public void run() {
        for (Player guard : Bukkit.getOnlinePlayers()) {
          if (attackData.containsKey(guard.getUniqueId())) {
            removeOldAttacks(guard);
          }
        }
      }
    }.runTaskTimer(GuardUtils.getPlugin(), 0L, 20L); // Update every second
  }

  public static GuardHitsManager getInstance() {
    if (instance == null) {
      instance = new GuardHitsManager();
    }
    return instance;
  }

  public void addAttack(Player guard, Player attacker) {
    this.attackData.putIfAbsent(guard.getUniqueId(), new HashMap<UUID, List<Long>>());

    Map<UUID, List<Long>> attackers = attackData.get(guard.getUniqueId());
    attackers.putIfAbsent(attacker.getUniqueId(), new ArrayList<Long>());
    attackers.get(attacker.getUniqueId()).add(System.currentTimeMillis());
  }

  public void removeOldAttacks(Player guard) {
    Map<UUID, List<Long>> attacks = attackData.get(guard.getUniqueId());
    long currentTime = System.currentTimeMillis();

    for (UUID attackerUUID : attacks.keySet()) {
      List<Long> attackTimes = attacks.get(attackerUUID);
      attackTimes.removeIf(time -> currentTime - time > hitTimeout);

      if (attackTimes.isEmpty()) {
        attacks.remove(attackerUUID);
      }
    }
  }

  public void clearAttacksForGuard(Player guard) {
    attackData.remove(guard.getUniqueId());
  }

  public void clearAllAttacksFromPlayer(Player player) {
    UUID playerUUID = player.getUniqueId();
    for (Map.Entry<UUID, Map<UUID, List<Long>>> guardEntry : attackData.entrySet()) {
      Map<UUID, List<Long>> playerMap = guardEntry.getValue();

      // Remove the player's UUID from the inner map if it exists
      if (playerMap != null) {
        playerMap.remove(playerUUID);
      }
    }
  }

  public void clearAttacksFromPlayerToGuard(Player player, Player guard) {
    if (attackData.containsKey(guard.getUniqueId())) {
      attackData.get(guard.getUniqueId()).remove(player.getUniqueId());
    }
  }

  public Map<UUID, List<Long>> getAttacksForGuard(Player guard) {
    return attackData.get(guard.getUniqueId());
  }

  public List<Long> getAttacksFromPlayerForGuard(Player guard, Player player) {
    if (attackData.containsKey(guard.getUniqueId())) {
      return attackData.get(guard.getUniqueId()).get(player.getUniqueId());
    }
    return new ArrayList<>();
  }

}
