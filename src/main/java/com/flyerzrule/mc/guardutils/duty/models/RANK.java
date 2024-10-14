package com.flyerzrule.mc.guardutils.duty.models;

public enum RANK {
  FREE("FREE"),
  CITIZEN("CITIZEN"),
  UNKNOWN("UNKNOWN");

  private final String name;

  RANK(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}