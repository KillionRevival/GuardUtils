package com.flyerzrule.mc.guardutils.kos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import com.flyerzrule.mc.guardutils.utils.PlayerSkinManager;

public class KOSTimer {
    private static KOSTimer instance;

    private final PlayerSkinManager playerSkinManager;

    private Map<UUID, KOSTimerPlayer> kosTimers = new HashMap<>();

    private KOSTimer() {
        this.playerSkinManager = PlayerSkinManager.getInstance();
    }

    public static KOSTimer getInstance() {
        if (instance == null) {
            instance = new KOSTimer();
        }
        return instance;
    }

    public void setKOSTimer(Player player, Player guard, int time) {
        long timeInMillis = time * 60 * 1000;
        long endTime = System.currentTimeMillis() + timeInMillis;

        KOSTimerPlayer kosTimerPlayer = new KOSTimerPlayer(player, endTime, guard);

        CompletableFuture.runAsync(() -> {
            String skinUrl = this.playerSkinManager.getPlayerSkin(player);
            kosTimerPlayer.setSkinUrl(skinUrl);
        });

        kosTimers.put(player.getUniqueId(), kosTimerPlayer);
    }

    public void cancelKOSTimer(Player player) {
        if (isKOSTimerActive(player)) {
            Component serverMessage = Component.text().color(NamedTextColor.GREEN)
                    .content(String.format("KOS has ended for %s!", player.getName())).build();
            Bukkit.broadcast(serverMessage);

            Component userMessage = Component.text().color(NamedTextColor.GOLD)
                    .content("Your KOS timer has ended!").build();
            player.sendMessage(userMessage);

            kosTimers.remove(player.getUniqueId());
        }
    }

    public boolean isKOSTimerActive(Player player) {
        return kosTimers.containsKey(player.getUniqueId());
    }

    public long getKOSTimer(Player player) {
        return kosTimers.get(player.getUniqueId()).getEndTime();
    }

    public List<Player> getPlayersOnKOS() {
        return kosTimers.values().stream().map(ele -> ele.getPlayer()).collect(Collectors.toList());
    }

    public List<String> getPlayerUsernamesOnKOS() {
        return kosTimers.values().stream().map(ele -> ele.getPlayer().getName()).collect(Collectors.toList());
    }

    public List<KOSTimerPlayer> getKOSTimerPlayers() {
        return kosTimers.values().stream().collect(Collectors.toList());
    }
}
