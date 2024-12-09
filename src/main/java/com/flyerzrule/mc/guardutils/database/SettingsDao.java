package com.flyerzrule.mc.guardutils.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.flyerzrule.mc.guardutils.database.models.Setting;

import co.killionrevival.killioncommons.database.DataAccessObject;

public class SettingsDao extends DataAccessObject<Setting> {
  private static SettingsDao instance;

  private final String SCHEMA_NAME = "guard_utils";
  private final String TABLE_NAME = "settings";
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
    createSchemaIfNotExists(this.SCHEMA_NAME);
  }

  private void createSettingsTable() {
    String query = String.format("CREATE TABLE IF NOT EXISTS %s.%s (PRIMARY KEY key TEXT, value boolean);",
        this.SCHEMA_NAME, this.TABLE_NAME);
    try {
      executeQuery(query);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean getInvisTagSetting() {
    String query = String.format("SELECT value FROM %s.%s WHERE key = '%s';", this.SCHEMA_NAME, this.TABLE_NAME,
        this.INVIS_TAG_KEY);
    try {
      List<Setting> settings = fetchQuery(query);
      if (settings.size() == 0) {
        return true;
      }
      return settings.get(0).getValue();
    } catch (Exception e) {
      e.printStackTrace();
      return true;
    }
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
