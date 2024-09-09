package com.flyerzrule.mc.guardutils.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.flyerzrule.mc.guardutils.GuardUtils;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;

public class GuardScoreboard {
    private static GuardScoreboard instance;

    private Map<UUID, Map<UUID, List<Long>>> guardAttackData = new HashMap<>();
    private final long hitTimeout;

    private GuardScoreboard() {
        hitTimeout = GuardUtils.getPlugin().getConfig().getInt("hit-timeout", 120) * 1000;

        createScoreboardTask();
    }

    public static GuardScoreboard getInstance() {
        if (instance == null) {
            instance = new GuardScoreboard();
        }
        return instance;
    }

    private void createScoreboardTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player guard : Bukkit.getOnlinePlayers()) {
                    if (guardAttackData.containsKey(guard.getUniqueId())) {
                        if (guard.hasPermission("guardutils.guard.scoreboard")) {
                            removeOldAttacks(guard);
                            updateGuardScoreboard(guard);
                        } else {
                            hideGuardScoreboard(guard);
                        }
                    }
                }
            }
        }.runTaskTimer(GuardUtils.getPlugin(), 0L, 20L); // Update every second
    }

    private void updateGuardScoreboard(Player guard) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        TextComponent title = Component.text().color(NamedTextColor.GOLD).content("Recent Attacks").build();
        Objective objective = scoreboard.getObjective("attacks");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("attacks", Criteria.DUMMY, title);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        scoreboard.getEntries().forEach(scoreboard::resetScores);

        Map<UUID, List<Long>> attackers = guardAttackData.get(guard.getUniqueId());
        List<Map.Entry<UUID, List<Long>>> sortedAttackers = new ArrayList<>(attackers.entrySet());
        sortedAttackers.sort((a, b) -> {
            long aLastHit = a.getValue().get(a.getValue().size() - 1);
            long bLastHit = b.getValue().get(b.getValue().size() - 1);
            return Long.compare(bLastHit, aLastHit);
        });

        int index = sortedAttackers.size();
        for (Map.Entry<UUID, List<Long>> attacker : sortedAttackers) {
            UUID attackerUUID = attacker.getKey();
            Player attackerPlayer = Bukkit.getPlayer(attackerUUID);
            List<Long> attackTimes = attacker.getValue();

            Score score = objective
                    .getScore(String.format("%s%s: %s%d hits%s", ChatColor.GRAY, attackerPlayer.getName(),
                            ChatColor.DARK_AQUA, attackTimes.size(), ChatColor.RESET));
            objective.numberFormat(NumberFormat.blank());
            score.setScore(index--);
        }

        guard.setScoreboard(scoreboard);
    }

    public void addAttack(Player guard, Player attacker) {
        this.guardAttackData.putIfAbsent(guard.getUniqueId(), new HashMap<UUID, List<Long>>());

        Map<UUID, List<Long>> attackers = guardAttackData.get(guard.getUniqueId());
        attackers.putIfAbsent(attacker.getUniqueId(), new ArrayList<Long>());
        attackers.get(attacker.getUniqueId()).add(System.currentTimeMillis());
    }

    private void removeOldAttacks(Player guard) {
        Map<UUID, List<Long>> attacks = guardAttackData.get(guard.getUniqueId());
        long currentTime = System.currentTimeMillis();

        for (UUID attackerUUID : attacks.keySet()) {
            List<Long> attackTimes = attacks.get(attackerUUID);
            attackTimes.removeIf(time -> currentTime - time > hitTimeout);

            if (attackTimes.isEmpty()) {
                attacks.remove(attackerUUID);
            }
        }
    }

    private void hideGuardScoreboard(Player guard) {
        if (guard.getScoreboard() != null) {
            guard.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }
}
