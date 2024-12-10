package com.flyerzrule.mc.guardutils.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.database.models.Setting;

import co.killionrevival.killioncommons.database.DataAccessObject;

public class SettingsDao extends DataAccessObject<Setting> {
  private static SettingsDao instance;

  private final String INVIS_TAG_KEY = "invis_tag";

  private SettingsDao(final GuardsDB db) {
    super(db);
    createSchema();
    createSettingsTable();
  }

  public static SettingsDao getInstance() {
    if (instance == null) {
      instance = new SettingsDao(GuardsDB.getInstance());
    }
    return instance;
  }

  private void createSchema() {
    createSchemaIfNotExists("guard_utils");
  }

  private void createSettingsTable() {
    String query = "CREATE TABLE IF NOT EXISTS guard_utils.settings (PRIMARY KEY key TEXT, value boolean);";
    try {
      executeQuery(query);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public boolean getInvisTagSetting() {
    String query = "SELECT * FROM guard_utils.settings WHERE key = ?;";
    try {
      List<Setting> settings = fetchQuery(query, INVIS_TAG_KEY);
      if (settings.size() == 0) {
        return true;
      }
      return settings.get(0).getValue();
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
      return true;
    }
  }

  public void setInvisTagSetting(boolean newValue) {
    String query = "UPDATE guard_utils.settings SET value = ? WHERE key = ?;";
    try {
      executeUpdate(query, newValue, INVIS_TAG_KEY);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public boolean toggleInvisTagSetting() {
    boolean currentSetting = this.getInvisTagSetting();
    this.setInvisTagSetting(!currentSetting);
    return !currentSetting;
  }

  @Override
  public List<Setting> parse(ResultSet resultSet) throws SQLException {
    final ArrayList<Setting> settings = new ArrayList<>();
    while (resultSet != null && resultSet.next()) {
      String key = resultSet.getString("key");
      Boolean value = resultSet.getBoolean("value");
      settings.add(new Setting(key, value));
    }
    return settings;
  }
}
