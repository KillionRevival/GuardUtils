package com.flyerzrule.mc.guardutils.duty.models;

public enum RANK {
  FREE("FREE", "free"),
  CITIZEN("CITIZEN", "citizen"),
  UNKNOWN("UNKNOWN", "");

  private final String name;
  private final String groupName;

  RANK(String name, String groupName) {
    this.name = name;
    this.groupName = groupName;
  }

  public String getName() {
    return this.name;
  }

  public String getGroupName() {
    return this.groupName;
  }
}