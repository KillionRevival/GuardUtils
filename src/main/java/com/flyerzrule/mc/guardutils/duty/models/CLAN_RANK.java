package com.flyerzrule.mc.guardutils.duty.models;

public enum CLAN_RANK {
  LEADER("LEADER"),
  TRUSTED("TRUSTED"),
  UNTRUSTED("UNTRUSTED");

  private final String name;

  CLAN_RANK(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
