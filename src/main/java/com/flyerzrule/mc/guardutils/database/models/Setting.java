package com.flyerzrule.mc.guardutils.database.models;

import lombok.Value;

@Value
public class Setting {
  private String key;
  private Boolean value;
}