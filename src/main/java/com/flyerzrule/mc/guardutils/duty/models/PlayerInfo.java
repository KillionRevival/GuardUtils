package com.flyerzrule.mc.guardutils.duty.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class PlayerInfo {
  private final String userInfo;
  private RANK rank;
  private String clanTag;
  private Timestamp timeOfLastOnDuty;

  public PlayerInfo(ResultSet rs) throws SQLException {
    this.userInfo = rs.getString("user_id");
    this.rank = RANK.valueOf(rs.getString("rank"));
    this.clanTag = rs.getString("clan_tag");
    this.timeOfLastOnDuty = rs.getTimestamp("time_of_last_on_duty");
  }
}
