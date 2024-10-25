package com.flyerzrule.mc.guardutils.duty.database;

import java.sql.ResultSet;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.duty.models.CLAN_RANK;
import com.flyerzrule.mc.guardutils.duty.models.GuardStats;
import com.flyerzrule.mc.guardutils.duty.models.PlayerInfo;
import com.flyerzrule.mc.guardutils.duty.models.RANK;

import co.killionrevival.killioncommons.database.DatabaseConnection;
import co.killionrevival.killioncommons.database.models.ReturnCode;

public class DutyDatabase extends DatabaseConnection {
  private static DutyDatabase instance;

  private DutyDatabase() {
    super(GuardUtils.getMyLogger());

    boolean success = true;
    success &= (this.createSchemaIfNotExists("guard_utils") == ReturnCode.SUCCESS);
    success &= (this.createEnumIfNotExists("guard_utils", "RANK",
        new String[] { "FREE", "CITIZEN" }) == ReturnCode.SUCCESS);
    success &= (this.createEnumIfNotExists("guard_utils", "CLAN_RANK",
        new String[] { "LEADER", "TRUSTED", "UNTRUSTED", "UNKNOWN" }) == ReturnCode.SUCCESS);
    success &= this.createPlayerInfoTable();
    success &= this.createGuardStatsTable();

    if (!success) {
      GuardUtils.getMyLogger().sendError("Failed to setup Guard Duty database!");
    } else {
      GuardUtils.getMyLogger().sendSuccess("Successfully setup Guard Duty database!");
    }
  }

  public static DutyDatabase getInstance() {
    if (instance == null) {
      instance = new DutyDatabase();
    }
    return instance;
  }

  private boolean createPlayerInfoTable() {
    String query = "CREATE TABLE IF NOT EXISTS guard_utils.player_info (PRIMARY KEY user_id TEXT, rank RANK, clan_tag TEXT, clan_rank CLAN_RANK, clan_join_date BIGINT,time_of_last_on_duty TIMESTAMP);";

    try {
      this.executeQuery(query);
      GuardUtils.getMyLogger().sendDebug("Created player info table!");
      return true;
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
    return false;
  }

  private boolean createGuardStatsTable() {
    String query = "CREATE TABLE IF NOT EXISTS guard_utils.guard_stats (PRIMARY KEY user_id TEXT, kills INT, deaths INT, guard_time INT);";

    try {
      this.executeQuery(query);
      GuardUtils.getMyLogger().sendDebug("Created guard stats table!");
      return true;
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
    return false;
  }

  public boolean addPlayerInfo(String userId, RANK rank, String clanTag, long clanJoinDate, CLAN_RANK clanRank) {
    String query = "INSERT INTO guard_utils.player_info (user_id, rank, clan_tag, clan_rank, clan_join_date, time_of_last_on_duty) VALUES (?, ?, ?, ?, ?, NOW());";
    try {
      this.executeUpdate(query, userId, rank.getName(), clanTag);
      GuardUtils.getMyLogger().sendDebug("Added player info for " + userId);
      return true;
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
    return false;
  }

  public boolean addPlayerInfo(Player player, RANK rank, String clanTag, long clanJoinDate, CLAN_RANK clanRank) {
    String userId = player.getUniqueId().toString();
    return this.addPlayerInfo(userId, rank, clanTag, clanJoinDate, clanRank);
  }

  public boolean removePlayerInfo(String userId) {
    String query = "DELETE FROM guard_utils.player_info WHERE user_id = ?;";
    try {
      this.executeUpdate(query, userId);
      GuardUtils.getMyLogger().sendDebug("Removed player info for " + userId);
      return true;
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
    return false;
  }

  public boolean removePlayerInfo(Player player) {
    String userId = player.getUniqueId().toString();
    return this.removePlayerInfo(userId);
  }

  public PlayerInfo getPlayerInfo(String userId) {
    String query = "SELECT * FROM guard_utils.player_info WHERE user_id = ?;";
    try {
      ResultSet rs = this.fetchQuery(query, userId);
      GuardUtils.getMyLogger().sendDebug("Found player info for " + userId);
      return new PlayerInfo(rs);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
    return null;
  }

  public PlayerInfo getPlayerInfo(Player player) {
    String userId = player.getUniqueId().toString();
    return this.getPlayerInfo(userId);
  }

  public boolean hasPlayerInfo(String userId) {
    String query = "SELECT 1 FROM guard_utils.player_info WHERE user_id = ?;";
    try {
      ResultSet rs = this.fetchQuery(query, userId);
      GuardUtils.getMyLogger().sendDebug("Found player info for " + userId);
      return rs.next();
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
    return false;
  }

  public boolean hasPlayerInfo(Player player) {
    String userId = player.getUniqueId().toString();
    return this.hasPlayerInfo(userId);
  }

  public boolean addNewGuardStats(String userId) {
    String query = "INSERT INTO guard_utils.guard_stats (user_id, kills, deaths, guard_time) VALUES (?, 0, 0, 0);";

    try {
      this.executeUpdate(query, userId);
      GuardUtils.getMyLogger().sendDebug("Added new guard stat for " + userId);
      return true;
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
    return false;
  }

  public boolean addNewGuardStats(Player player) {
    String userId = player.getUniqueId().toString();
    return this.addNewGuardStats(userId);
  }

  public GuardStats getGuardStats(String userId) {
    String query = "SELECT * FROM guard_utils.guard_stats WHERE user_id = ?;";
    try {
      ResultSet rs = this.fetchQuery(query, userId);
      GuardUtils.getMyLogger().sendDebug("Found guard stats for " + userId);
      if (rs != null && rs.next()) {
        return new GuardStats(rs);
      }
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
    return null;
  }

  public GuardStats getGuardStats(Player player) {
    String userId = player.getUniqueId().toString();
    return this.getGuardStats(userId);
  }

  public boolean updateGuardStats(GuardStats stats) {
    String query = "UPDATE guard_utils.guard_stats SET kills = ?, deaths = ?, guard_time = ? WHERE user_id = ?;";

    try {
      this.executeUpdate(query, stats.getKills(), stats.getDeaths(), stats.getGuardTime(), stats.getUserId());
      GuardUtils.getMyLogger().sendDebug("Updated guard stats for " + stats.getUserId());
      return true;
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
    return false;

  }

  public boolean hasGuardStats(String userId) {
    String query = "SELECT 1 FROM guard_utils.guard_stats WHERE user_id = ?;";
    try {
      ResultSet rs = this.fetchQuery(query, userId);
      GuardUtils.getMyLogger().sendDebug("Found player stats for " + userId);
      return rs.next();
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
    return false;
  }

  public boolean hasGuardStats(Player player) {
    String userId = player.getUniqueId().toString();
    return this.hasGuardStats(userId);
  }
}
