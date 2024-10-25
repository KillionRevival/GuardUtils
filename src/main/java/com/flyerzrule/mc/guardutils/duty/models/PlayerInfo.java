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
  private CLAN_RANK clanRank;
  private long clanJoinDate;
  private Timestamp timeOfLastOnDuty;

  public PlayerInfo(ResultSet rs) throws SQLException {
    this.userInfo = rs.getString("user_id");
    this.rank = RANK.valueOf(rs.getString("rank"));
    this.clanTag = rs.getString("clan_tag");
    this.clanRank = CLAN_RANK.valueOf(rs.getString("clan_rank"));
    this.clanJoinDate = rs.getLong("clan_join_date");
    this.timeOfLastOnDuty = rs.getTimestamp("time_of_last_on_duty");
  }
}
