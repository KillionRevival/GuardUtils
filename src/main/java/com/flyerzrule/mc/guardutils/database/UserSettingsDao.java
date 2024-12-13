package com.flyerzrule.mc.guardutils.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.database.models.UserSetting;

import co.killionrevival.killioncommons.database.DataAccessObject;

public class UserSettingsDao extends DataAccessObject<UserSetting> {
  private static UserSettingsDao instance;

  private UserSettingsDao(final GuardsDB db) {
    super(db);
    createSchema();
    createUserSettingsTable();
  }

  public static UserSettingsDao getInstance() {
    if (instance == null) {
      instance = new UserSettingsDao(GuardsDB.getInstance());
    }
    return instance;
  }

  private void createSchema() {
    createSchemaIfNotExists("guard_utils");
  }

  private void createUserSettingsTable() {
    String query = "CREATE TABLE IF NOT EXISTS guard_utils.user_settings (uuid TEXT PRIMARY KEY, scoreboard_enabled boolean);";
    try {
      executeQuery(query);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public boolean getScoreboardEnabled(String uuid) {
    String query = "SELECT * FROM guard_utils.user_settings WHERE uuid = ?;";
    try {
      List<UserSetting> userSettings = fetchQuery(query, uuid);
      if (userSettings.size() == 0) {
        return true;
      }
      return userSettings.get(0).isScoreboardEnabled();
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
      return true;
    }
  }

  public void setScoreboardEnabled(String uuid, boolean newValue) {
    String query = "INSERT INTO guard_utils.user_settings (uuid, scoreboard_enabled) VALUES (?, ?) ON CONFLICT (uuid) DO UPDATE SET scoreboard_enabled = EXCLUDED.scoreboard_enabled;";
    try {
      executeUpdate(query, uuid, newValue);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public boolean toggleScoreboardEnabled(String uuid) {
    boolean currentSetting = this.getScoreboardEnabled(uuid);
    this.setScoreboardEnabled(uuid, !currentSetting);
    return !currentSetting;
  }

  @Override
  public List<UserSetting> parse(ResultSet resultSet) throws SQLException {
    final ArrayList<UserSetting> userSettings = new ArrayList<>();
    while (resultSet != null && resultSet.next()) {
      String uuid = resultSet.getString("uuid");
      Boolean scoreboardEnabled = resultSet.getBoolean("scoreboard_enabled");
      userSettings.add(new UserSetting(uuid, scoreboardEnabled));
    }
    return userSettings;
  }
}
