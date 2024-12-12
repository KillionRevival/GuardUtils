package com.flyerzrule.mc.guardutils.armorstands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.armorstands.models.ArmorStandFollower;
import com.flyerzrule.mc.guardutils.database.SettingsDao;
import com.flyerzrule.mc.guardutils.invis.InvisPlayers;
import com.flyerzrule.mc.guardutils.kos.KOSTimer;
import com.flyerzrule.mc.guardutils.utils.Message;
import com.flyerzrule.mc.guardutils.utils.Permissions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ArmorStandManager {
  private static ArmorStandManager instance;

  private Map<UUID, ArmorStandFollower> armorStandFollowers = new HashMap<>();

  private final KOSTimer kosTimer;
  private final InvisPlayers invisPlayers;
  private final SettingsDao settingsDao;

  private ArmorStandManager() {
    this.kosTimer = KOSTimer.getInstance();
    this.invisPlayers = InvisPlayers.getInstance();
    this.settingsDao = SettingsDao.getInstance();
  }

  public static ArmorStandManager getInstance() {
    if (instance == null) {
      instance = new ArmorStandManager();
    }
    return instance;
  }

  public void addArmorStand(Player player, Component initialText) {
    if (!hasArmorStand(player)) {
      ArmorStandFollower armorStandFollower = new ArmorStandFollower(player, initialText);
      armorStandFollowers.put(player.getUniqueId(), armorStandFollower);
    }
  }

  public void removeArmorStand(Player player) {
    if (hasArmorStand(player)) {
      ArmorStandFollower armorStandFollower = this.getArmorStand(player);
      armorStandFollower.stopFollowing();
      armorStandFollowers.remove(player.getUniqueId());
    }
  }

  public void removeAllArmorStands() {
    for (ArmorStandFollower armorStandFollower : armorStandFollowers.values()) {
      armorStandFollower.stopFollowing();
    }
    armorStandFollowers.clear();
  }

  public ArmorStandFollower getArmorStand(Player player) {
    return armorStandFollowers.get(player.getUniqueId());
  }

  public void updateArmorStandName(Player player, Component text) {
    ArmorStandFollower armorStandFollower = this.getArmorStand(player);
    armorStandFollower.updateCustomName(text);
  }

  public boolean hasArmorStand(Player player) {
    return armorStandFollowers.containsKey(player.getUniqueId());
  }

  public void createArmorStandTask() {
    new BukkitRunnable() {
      @Override
      public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
          if (!player.hasPermission(Permissions.GUARD)) {
            if (kosTimer.isKOSTimerActive(player) && settingsDao.getKOSInvisTagSetting()) {
              Component tag = Message.formatMessage(NamedTextColor.RED, "KOS");
              addArmorStand(player, tag);
            } else {
              removeArmorStand(player);
            }

          } else {
            if (invisPlayers.isPlayerInvisible(player) && settingsDao.getGuardInvisTagSetting()) {
              Component tag = Message.formatMessage(NamedTextColor.GOLD, "GUARD");
              addArmorStand(player, tag);
            } else {
              removeArmorStand(player);
            }
          }

        }
      }
    }.runTaskTimer(GuardUtils.getPlugin(), 0L, 20L); // Update every second
  }
}
