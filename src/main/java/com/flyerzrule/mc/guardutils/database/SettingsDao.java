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

  private final String GUARD_INVIS_TAG_KEY = "guard_invis_tag";
  private final String KOS_INVIS_TAG_KEY = "kos_invis_tag";

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
    String query = "CREATE TABLE IF NOT EXISTS guard_utils.settings (setting TEXT PRIMARY KEY, value boolean);";
    try {
      executeQuery(query);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public boolean getGuardInvisTagSetting() {
    String query = "SELECT * FROM guard_utils.settings WHERE setting = ?;";
    try {
      List<Setting> settings = fetchQuery(query, GUARD_INVIS_TAG_KEY);
      if (settings.size() == 0) {
        return true;
      }
      return settings.get(0).getValue();
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
      return true;
    }
  }

  public void setGuardInvisTagSetting(boolean newValue) {
    String query = "INSERT INTO guard_utils.settings (setting, value) VALUES (?, ?) ON CONFLICT (setting) DO UPDATE SET value = EXCLUDED.value;";
    try {
      executeUpdate(query, GUARD_INVIS_TAG_KEY, newValue);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public boolean toggleGuardInvisTagSetting() {
    boolean currentSetting = this.getGuardInvisTagSetting();
    this.setGuardInvisTagSetting(!currentSetting);
    return !currentSetting;
  }

  public boolean getKOSInvisTagSetting() {
    String query = "SELECT * FROM guard_utils.settings WHERE setting = ?;";
    try {
      List<Setting> settings = fetchQuery(query, KOS_INVIS_TAG_KEY);
      if (settings.size() == 0) {
        return true;
      }
      return settings.get(0).getValue();
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
      return true;
    }
  }

  public void setKOSInvisTagSetting(boolean newValue) {
    String query = "INSERT INTO guard_utils.settings (setting, value) VALUES (?, ?) ON CONFLICT (setting) DO UPDATE SET value = EXCLUDED.value;";
    try {
      executeUpdate(query, KOS_INVIS_TAG_KEY, newValue);
    } catch (Exception e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }
  }

  public boolean toggleKOSInvisTagSetting() {
    boolean currentSetting = this.getKOSInvisTagSetting();
    this.setKOSInvisTagSetting(!currentSetting);
    return !currentSetting;
  }

  @Override
  public List<Setting> parse(ResultSet resultSet) throws SQLException {
    final ArrayList<Setting> settings = new ArrayList<>();
    while (resultSet != null && resultSet.next()) {
      String setting = resultSet.getString("setting");
      Boolean value = resultSet.getBoolean("value");
      settings.add(new Setting(setting, value));
    }
    return settings;
  }
}
