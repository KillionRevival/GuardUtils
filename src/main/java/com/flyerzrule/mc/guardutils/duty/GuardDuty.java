package com.flyerzrule.mc.guardutils.duty;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.database.GuardStatsDao;
import com.flyerzrule.mc.guardutils.database.SavedPlayerInfoDao;
import com.flyerzrule.mc.guardutils.database.models.SavedPlayerInfo;
import com.flyerzrule.mc.guardutils.duty.models.CLAN_RANK;
import com.flyerzrule.mc.guardutils.duty.models.RANK;
import com.flyerzrule.mc.guardutils.utils.time.TimeUtils;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class GuardDuty {
  private static final GuardStatsDao guardStatsDao = GuardStatsDao.getInstance();
  private static final SavedPlayerInfoDao savedPlayerInfoDao = SavedPlayerInfoDao.getInstance();

  public static void becomeGuard(Player player) {
    // TODO: Make sure the switch does not effect DTR
    if (isOnDuty(player)) {
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
      GuardUtils.getMyLogger()
          .sendDebug(String.format("Player %s is currently in clan %s with the rank %s with join date %d", clanTag,
              clanRank.getName(), clanJoinDate));
    } else {
      GuardUtils.getMyLogger().sendDebug(String.format("Player %s is not in a clan", player.getName()));
    }

    // Put the player in the G clan
    Clan guardClan = sc.getClanManager().getClan("G");
    clanPlayer.setClan(guardClan);
    GuardUtils.getMyLogger().sendDebug(String.format("Player %s added to G clan", player.getName()));

    // Add the LP guard group to the player and remove their old group
    // TODO: Implement

    // Kill the player
    player.setHealth(0.0);
    GuardUtils.getMyLogger().sendDebug(String.format("Player %s was killed to become a guard", player.getName()));

    if (!guardStatsDao.hasGuardStats(player)) {
      guardStatsDao.addNewGuardStats(player);
      GuardUtils.getMyLogger()
          .sendDebug(String.format("Guard stats for %s didn't exist and were added to DB", player.getName()));
    }

    // Save player info
    SavedPlayerInfo savedPlayerInfo = new SavedPlayerInfo(player.getUniqueId().toString(), rank, clanTag, clanRank,
        clanJoinDate, null);
    savedPlayerInfoDao.addPlayerInfo(savedPlayerInfo);
    GuardUtils.getMyLogger().sendDebug(String.format("Player %s's info saved to DB", player.getName()));

    GuardUtils.getMyLogger().sendInfo(String.format("Player %s is now a guard", player.getName()));
  }

  public static void becomePlayer(Player player) {
    // TODO: Make sure the switch does not effect DTR
    if (!isOnDuty(player)) {
      GuardUtils.getMyLogger().sendError(String.format("Player %s is not on duty!", player.getName()));
      return;
    }

    SavedPlayerInfo playerInfo = savedPlayerInfoDao.getPlayerInfo(player);

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
    int timeToAdd = TimeUtils.getSecondsSince(playerInfo.getTimeStartOfDuty());
    guardStatsDao.addGuardTime(player.getUniqueId().toString(), timeToAdd);
    GuardUtils.getMyLogger().sendDebug(String.format("Guard stats for %s updated", player.getName()));

    // Set the player's rank to the rank they had before going on duty
    // TODO: Implement

    // Kill player
    player.setHealth(0.0);
    GuardUtils.getMyLogger().sendDebug(String.format("Player %s was killed to become a player", player.getName()));

    savedPlayerInfoDao.removePlayerInfo(player);
    GuardUtils.getMyLogger().sendDebug(String.format("Player %s's info removed from DB", player.getName()));

    GuardUtils.getMyLogger().sendInfo(String.format("Player %s is no longer a guard", player.getName()));
  }

  public static boolean isOnDuty(Player player) {
    return savedPlayerInfoDao.hasPlayerInfo(player);
  }

  public static RANK getRank(Player player) {
    // TODO: Check the player's primary group and return the corresponding rank
    return RANK.UNKNOWN;
  }

  public static void switchDuty(Player player) {
    if (isOnDuty(player)) {
      becomePlayer(player);
    } else {
      becomeGuard(player);
    }
  }
}
