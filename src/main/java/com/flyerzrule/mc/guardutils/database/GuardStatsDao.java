package com.flyerzrule.mc.guardutils.database;

import java.security.Guard;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.database.models.GuardStats;

import co.killionrevival.killioncommons.database.DataAccessObject;

public class GuardStatsDao extends DataAccessObject<GuardStats> {
  private static GuardStatsDao instance;

  private GuardStatsDao(final GuardsDB db) {
    super(db);
    createSchema();
    createGuardStatsTable();
  }

  public static GuardStatsDao getInstance() {
    if (instance == null) {
      instance = new GuardStatsDao(GuardsDB.getInstance());
    }
    return instance;
  }

  private void createSchema() {
    createSchemaIfNotExists("guard_utils");
  }

  private void createGuardStatsTable() {
    String query = "CREATE TABLE IF NOT EXISTS guard_utils.guard_stats (uuid TEXT PRIMARY KEY, kills int, deaths int, guard_time int);";
    try {
      executeQuery(query);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public void addNewGuardStats(String uuid) {
    String query = "INSERT INTO guard_utils.guard_stats (uuid, kills, deaths, guard_time) VALUES (?, 0, 0, 0);";
    try {
      executeUpdate(query, uuid);
      GuardUtils.getMyLogger().sendDebug(String.format("Added new guard stats for %s", uuid));
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public void addNewGuardStats(Player player) {
    GuardUtils.getMyLogger().sendDebug(String.format("Adding new guard stats for %s", player.getName()));
    String uuid = player.getUniqueId().toString();
    this.addNewGuardStats(uuid);
  }

  public GuardStats getGuardStats(String uuid) {
    String query = "SELECT * FROM guard_utils.guard_stats WHERE uuid = ?;";
    try {
      List<GuardStats> guardStats = fetchQuery(query, uuid);
      if (guardStats.size() == 0) {
        return null;
      }
      return guardStats.get(0);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
      return null;
    }
  }

  public GuardStats getGuardStats(Player player) {
    String uuid = player.getUniqueId().toString();
    return this.getGuardStats(uuid);
  }

  public void updateGuardStats(GuardStats guardStats) {
    String query = "UPDATE guard_utils.guard_stats SET kills = ?, deaths = ?, guard_time = ? WHERE uuid = ?;";
    try {
      executeUpdate(query, guardStats.getKills(), guardStats.getDeaths(), guardStats.getGuardTime(),
          guardStats.getUuid());
      GuardUtils.getMyLogger().sendDebug(String.format("Updated guard stats for %s", guardStats.getUuid()));
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public boolean hasGuardStats(String uuid) {
    GuardStats guardStats = this.getGuardStats(uuid);
    return guardStats != null;
  }

  public boolean hasGuardStats(Player player) {
    String uuid = player.getUniqueId().toString();
    return this.hasGuardStats(uuid);
  }

  public void incrementKills(String uuid) {
    String query = "UPDATE guard_utils.guard_stats SET kills = kills + 1 WHERE uuid = ?;";
    try {
      executeUpdate(query, uuid);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public void incrementDeaths(String uuid) {
    String query = "UPDATE guard_utils.guard_stats SET deaths = deaths + 1 WHERE uuid = ?;";
    try {
      executeUpdate(query, uuid);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public void addGuardTime(String uuid, int time) {
    String query = "UPDATE guard_utils.guard_stats SET guard_time = guard_time + ? WHERE uuid = ?;";
    try {
      executeUpdate(query, time, uuid);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  @Override
  public List<GuardStats> parse(ResultSet resultSet) throws SQLException {
    final ArrayList<GuardStats> guardStats = new ArrayList<>();
    while (resultSet != null && resultSet.next()) {
      String uuid = resultSet.getString("uuid");
      int kills = resultSet.getInt("kills");
      int deaths = resultSet.getInt("deaths");
      int guardTime = resultSet.getInt("guard_time");
      guardStats.add(new GuardStats(uuid, kills, deaths, guardTime));
    }
    return guardStats;
  }
}
