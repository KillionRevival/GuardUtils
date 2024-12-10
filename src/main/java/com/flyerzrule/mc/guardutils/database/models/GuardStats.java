package com.flyerzrule.mc.guardutils.database.models;

import lombok.Value;

@Value
public class GuardStats {
  private String uuid;
  private int kills;
  private int deaths;
  private int guardTime;
}
