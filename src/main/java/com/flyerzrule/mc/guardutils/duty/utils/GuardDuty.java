package com.flyerzrule.mc.guardutils.duty.utils;

import com.flyerzrule.mc.guardutils.duty.database.DutyDatabase;
import com.flyerzrule.mc.guardutils.duty.models.RANK;

public class GuardDuty {
  public static void becomeGuard(String playerId) {
    // TODO: Implement
    // Need to save off the player's clan if they are in one, as well as their rank in the clan
    // Then remove them from the clan and add them to the G clan
    // Should kill the player (make sure that this does not effect DTR)
  }

  public static void becomePlayer(String playerId) {
    // TODO: Implement
    // Need to pull the player's clan tag and rank from the database
    // Then remove them from the G clan and add them back to their clan and make sure that they maintain their rank in the clan (leader, trusted, untrusted)
    // Should kill the player (make sure that this does not effect DTR or the guard stats)
  }

  public static boolean isOnDuty(String playerId) {
    DutyDatabase db = DutyDatabase.getInstance();
    return db.hasPlayerInfo(playerId);
  }

  public static RANK getRank(String playerId) {
    // TODO: Check the player's primary group and return the corresponding rank
    return RANK.UNKNOWN;
  }

}
