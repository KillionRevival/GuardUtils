package com.flyerzrule.mc.guardutils.database.models;

import java.sql.Timestamp;

import com.flyerzrule.mc.guardutils.duty.models.CLAN_RANK;
import com.flyerzrule.mc.guardutils.duty.models.RANK;

import lombok.Value;

@Value
public class SavedPlayerInfo {
  private String uuid;
  private RANK rank;
  private String clanTag;
  private CLAN_RANK clanRank;
  private long clanJoinDate;
  private Timestamp timeStartOfDuty;
}
