package com.flyerzrule.mc.guardutils.kos;

import org.bukkit.entity.Player;

import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@ToString
public class KOSTimerPlayer {
  private final Player player;
  private final long endTime;
  private final Player guard;

  @Setter
  private String skinUrl;

  public KOSTimerPlayer(Player player, long endTime, Player guard) {
    this.player = player;
    this.endTime = endTime;
    this.guard = guard;
    this.skinUrl = null;
  }
}
