package com.flyerzrule.mc.guardutils.duty.utils;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.duty.database.DutyDatabase;
import com.flyerzrule.mc.guardutils.duty.models.CLAN_RANK;
import com.flyerzrule.mc.guardutils.duty.models.GuardStats;
import com.flyerzrule.mc.guardutils.duty.models.PlayerInfo;
import com.flyerzrule.mc.guardutils.duty.models.RANK;
import com.flyerzrule.mc.guardutils.utils.time.TimeUtils;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class GuardDuty {
  public static void becomeGuard(Player player) {
    // TODO: Make sure the switch does not effect DTR
    DutyDatabase db = DutyDatabase.getInstance();

    if (db.hasPlayerInfo(player)) {
      GuardUtils.getMyLogger().sendError(String.format("Player %s is already on duty!", player.getName()));
      return;
    }

    SimpleClans sc = GuardUtils.getSimpleClans();
    ClanPlayer clanPlayer = sc.getClanManager().getClanPlayer(player);

    if (clanPlayer == null) {
      GuardUtils.getMyLogger().sendError(String.format("Cannot get clan player for %s", player.getName()));
      return;
    }

    String clanTag = "";
    CLAN_RANK clanRank = CLAN_RANK.UNKNOWN;
    RANK rank = GuardDuty.getRank(player);
    Clan clan = clanPlayer.getClan();
    long clanJoinDate = -1;

    // If the player is currently in a clan
    if (clan != null) {
      clanTag = clan.getTag();
      clanJoinDate = clanPlayer.getJoinDate();
      GuardUtils.getMyLogger().sendDebug("Clan tag: " + clanTag);
      if (clanPlayer.isLeader()) {
        clanRank = CLAN_RANK.LEADER;
      } else if (clanPlayer.isTrusted()) {
        clanRank = CLAN_RANK.TRUSTED;
      } else {
        clanRank = CLAN_RANK.UNTRUSTED;
      }
    }

    // Put the player in the G clan
    Clan guardClan = sc.getClanManager().getClan("G");
    clanPlayer.setClan(guardClan);

    if (!db.hasGuardStats(player)) {
      db.addNewGuardStats(player);
    }

    // Add the LP guard group to the player and remove their old group
    // TODO: Implement

    // Kill the player
    player.setHealth(0.0);

    // Save player info
    db.addPlayerInfo(player, rank, clanTag, clanJoinDate, clanRank);
  }

  public static void becomePlayer(Player player) {
    // TODO: Make sure the switch does not effect DTR
    DutyDatabase db = DutyDatabase.getInstance();

    if (!db.hasPlayerInfo(player)) {
      GuardUtils.getMyLogger().sendError(String.format("Player %s is not on duty!", player.getName()));
      return;
    }

    PlayerInfo playerInfo = db.getPlayerInfo(player);

    SimpleClans sc = GuardUtils.getSimpleClans();
    ClanPlayer clanPlayer = sc.getClanManager().getClanPlayer(player);

    if (clanPlayer == null) {
      GuardUtils.getMyLogger().sendError(String.format("Cannot get clan player for %s", player.getName()));
      return;
    }

    // Remove the player from the G clan and add them back to their clan
    Clan pClan = sc.getClanManager().getClan(playerInfo.getClanTag());
    clanPlayer.setClan(pClan);
    clanPlayer.setJoinDate(playerInfo.getClanJoinDate());
    if (playerInfo.getClanRank().equals(CLAN_RANK.LEADER)) {
      clanPlayer.setLeader(true);
      clanPlayer.setTrusted(true);
    } else if (playerInfo.getClanRank().equals(CLAN_RANK.TRUSTED)) {
      clanPlayer.setLeader(false);
      clanPlayer.setTrusted(true);
    } else {
      clanPlayer.setLeader(false);
      clanPlayer.setTrusted(false);
    }

    // Update guard stats
    GuardStats stats = db.getGuardStats(player);
    int timeToAdd = TimeUtils.getSecondsSince(playerInfo.getTimeOfLastOnDuty());
    stats.addGuardTime(timeToAdd);
    db.updateGuardStats(stats);

    // Set the player's rank to the rank they had before going on duty
    // TODO: Implement

    // Kill player
    player.setHealth(0.0);

    db.removePlayerInfo(player);
  }

  public static boolean isOnDuty(Player player) {
    DutyDatabase db = DutyDatabase.getInstance();
    return db.hasPlayerInfo(player);
  }

  public static RANK getRank(Player player) {
    // TODO: Check the player's primary group and return the corresponding rank
    return RANK.UNKNOWN;
  }

  public static void addGuardKill(Player player) {
    DutyDatabase db = DutyDatabase.getInstance();
    GuardStats stats = db.getGuardStats(player);

    stats.incrementKills();
    db.updateGuardStats(stats);
  }

  public static void addGuardDeath(Player player) {
    DutyDatabase db = DutyDatabase.getInstance();
    GuardStats stats = db.getGuardStats(player);

    stats.incrementDeaths();
    db.updateGuardStats(stats);
  }

  public static void switchDuty(Player player) {
    if (isOnDuty(player)) {
      becomePlayer(player);
    } else {
      becomeGuard(player);
    }
  }
}
