package com.flyerzrule.mc.guardutils.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.database.UserSettingsDao;
import com.flyerzrule.mc.guardutils.duty.GuardDuty;

import co.killionrevival.killioncommons.scoreboard.ScoreboardAddition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;

public class GuardHitsScoreboard extends ScoreboardAddition {
  private final String COMPONENT_NAME = "guard_hits";

  private final UserSettingsDao userSettingsDao;
  private final GuardHitsManager guardHitsManager;

  private Player guard;

  public GuardHitsScoreboard() {
    this.userSettingsDao = UserSettingsDao.getInstance();
    this.guardHitsManager = GuardHitsManager.getInstance();
  }

  @Override
  public String componentName() {
    return COMPONENT_NAME;
  }

  @Override
  public ScoreboardAddition clone() {
    return new GuardHitsScoreboard();
  }

  @Override
  public void initWithPlayer(Player player) {
    if (player == null) {
      return;
    }

    this.guard = player;

    this.setEnabled(true);

  }

  @Override
  public List<Component> getLinesToAdd() {
    final List<Component> lines = new ArrayList<>();

    if (this.guard != null && GuardDuty.isOnDuty(this.guard)
        && userSettingsDao.getScoreboardEnabled(this.guard.getUniqueId().toString())) {

      Map<UUID, List<Long>> attackers = guardHitsManager.getAttacksForGuard(guard);
      List<Map.Entry<UUID, List<Long>>> sortedAttackers = new ArrayList<>(attackers.entrySet());
      sortedAttackers.sort((a, b) -> {
        long aLastHit = a.getValue().get(a.getValue().size() - 1);
        long bLastHit = b.getValue().get(b.getValue().size() - 1);
        return Long.compare(bLastHit, aLastHit);
      });

      if (sortedAttackers.size() > 0) {
        lines.add(Component.text("Recent Attacks: ").color(TextColor.color(0xFF0000)));
      }

      for (Map.Entry<UUID, List<Long>> attacker : sortedAttackers) {
        UUID attackerUUID = attacker.getKey();
        Player attackerPlayer = Bukkit.getPlayer(attackerUUID);
        List<Long> attackTimes = attacker.getValue();

        String attackStr = String.format("%s%s: %s%d hits%s", ChatColor.GRAY, attackerPlayer.getName(),
            ChatColor.DARK_AQUA, attackTimes.size(), ChatColor.RESET);
        lines.add(Component.text(attackStr).color(TextColor.color(0xFFFF00)));
      }

    }

    return lines;
  }

}
