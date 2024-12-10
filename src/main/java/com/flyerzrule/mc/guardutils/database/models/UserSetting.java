package com.flyerzrule.mc.guardutils.database.models;

import lombok.Value;

@Value
public class UserSetting {
  private String uuid;
  private boolean scoreboardEnabled;
}