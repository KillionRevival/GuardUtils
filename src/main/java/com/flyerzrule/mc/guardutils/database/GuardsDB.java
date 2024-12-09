package com.flyerzrule.mc.guardutils.database;

import com.flyerzrule.mc.guardutils.GuardUtils;

import co.killionrevival.killioncommons.database.DatabaseConnection;

public class GuardsDB extends DatabaseConnection {
  private static GuardsDB instance;

  private GuardsDB() {
    super(GuardUtils.getMyLogger(), GuardUtils.getPlugin());
  }

  public static GuardsDB getInstance() {
    if (instance == null) {
      instance = new GuardsDB();
    }
    return instance;
  }
}
