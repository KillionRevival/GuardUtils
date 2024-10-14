package com.flyerzrule.mc.guardutils.duty.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Data;

@Data
public class GuardStats {
  private final String userId;
  private int kills;
  private int deaths;
  private int guardTime;

  public GuardStats(ResultSet rs) throws SQLException {
    this.userId = rs.getString("user_id");
    this.kills = rs.getInt("kills");
    this.deaths = rs.getInt("deaths");
    this.guardTime = rs.getInt("guard_time");
  }
}
