package com.flyerzrule.mc.guardutils.database;

import co.killionrevival.killioncommons.database.DataAccessObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.database.models.SavedPlayerInfo;
import com.flyerzrule.mc.guardutils.duty.models.CLAN_RANK;
import com.flyerzrule.mc.guardutils.duty.models.RANK;

public class SavedPlayerInfoDao extends DataAccessObject<SavedPlayerInfo> {
  private static SavedPlayerInfoDao instance;

  private SavedPlayerInfoDao(final GuardsDB db) {
    super(db);
    createSchema();
    createRankEnum();
    createClanRankEnum();
    createSavedPlayerInfoTable();
    GuardUtils.getMyLogger().sendDebug("SavedPlayerInfoDao created");
  }

  public static SavedPlayerInfoDao getInstance() {
    if (instance == null) {
      instance = new SavedPlayerInfoDao(GuardsDB.getInstance());
    }
    return instance;
  }

  private void createSchema() {
    createSchemaIfNotExists("guard_utils");
  }

  private void createRankEnum() {
    String[] values = Arrays.stream(RANK.values())
        .map(Enum::name)
        .toArray(String[]::new);

    createEnumIfNotExists("guard_utils", "RANK",
        values);
  }

  private void createClanRankEnum() {
    String[] values = Arrays.stream(CLAN_RANK.values())
        .map(Enum::name)
        .toArray(String[]::new);

    createEnumIfNotExists("guard_utils", "CLAN_RANK",
        values);
  }

  private void createSavedPlayerInfoTable() {
    String query = "CREATE TABLE IF NOT EXISTS guard_utils.saved_player_info (uuid TEXT PRIMARY KEY, rank guard_utils.RANK, clan_tag TEXT, clan_rank guard_utils.CLAN_RANK, clan_join_date BIGINT,time_start_duty TIMESTAMP);";
    try {
      executeQuery(query);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public void addPlayerInfo(SavedPlayerInfo savedPlayerInfo) {
    String query = "INSERT INTO guard_utils.saved_player_info (uuid, rank, clan_tag, clan_rank, clan_join_date, time_start_duty) VALUES (?, CAST(? AS guard_utils.RANK), ?, CAST(? AS guard_utils.CLAN_RANK), ?, NOW());";
    try {
      executeUpdate(query, savedPlayerInfo.getUuid(), savedPlayerInfo.getRank().getName(), savedPlayerInfo.getClanTag(),
          savedPlayerInfo.getClanRank().getName(), savedPlayerInfo.getClanJoinDate());
      GuardUtils.getMyLogger().sendDebug(String.format("Added player info for %s", savedPlayerInfo.getUuid()));
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public void removePlayerInfo(String uuid) {
    String query = "DELETE FROM guard_utils.saved_player_info WHERE uuid = ?;";
    try {
      executeUpdate(query, uuid);
      GuardUtils.getMyLogger().sendDebug(String.format("Removed player info for %s", uuid));
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public void removePlayerInfo(Player player) {
    String uuid = player.getUniqueId().toString();
    this.removePlayerInfo(uuid);
  }

  public SavedPlayerInfo getPlayerInfo(String uuid) {
    String query = "SELECT * FROM guard_utils.saved_player_info WHERE uuid = ?;";
    try {
      List<SavedPlayerInfo> savedPlayerInfos = fetchQuery(query, uuid);
      if (savedPlayerInfos.size() == 0) {
        return null;
      }
      return savedPlayerInfos.get(0);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
      return null;
    }
  }

  public SavedPlayerInfo getPlayerInfo(Player player) {
    String uuid = player.getUniqueId().toString();
    return this.getPlayerInfo(uuid);
  }

  public boolean hasPlayerInfo(String uuid) {
    SavedPlayerInfo savedPlayerInfo = this.getPlayerInfo(uuid);
    return savedPlayerInfo != null;
  }

  public boolean hasPlayerInfo(Player player) {
    String uuid = player.getUniqueId().toString();
    return this.hasPlayerInfo(uuid);
  }

  @Override
  public List<SavedPlayerInfo> parse(ResultSet resultSet) throws SQLException {
    final ArrayList<SavedPlayerInfo> savedPlayerInfos = new ArrayList<>();
    while (resultSet != null && resultSet.next()) {
      String uuid = resultSet.getString("uuid");
      RANK rank = RANK.valueOf(resultSet.getString("rank"));
      String clanTag = resultSet.getString("clan_tag");
      CLAN_RANK clanRank = CLAN_RANK.valueOf(resultSet.getString("clan_rank"));
      long clanJoinDate = resultSet.getLong("clan_join_date");
      Timestamp timeStartOfDuty = resultSet.getTimestamp("time_start_duty");
      savedPlayerInfos.add(new SavedPlayerInfo(uuid, rank, clanTag, clanRank, clanJoinDate, timeStartOfDuty));
    }
    return savedPlayerInfos;
  }
}
