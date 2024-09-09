package com.flyerzrule.mc.guardutils.invis;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class InvisPlayers {
    private static InvisPlayers instance;

    private Set<Player> invisiblePlayers = new HashSet<>();

    private InvisPlayers() {
    }

    public static InvisPlayers getInstance() {
        if (instance == null) {
            instance = new InvisPlayers();
        }
        return instance;
    }

    public void addPlayer(Player player) {
        invisiblePlayers.add(player);
    }

    public void removePlayer(Player player) {
        invisiblePlayers.remove(player);
    }

    public boolean isPlayerInvisible(Player player) {
        return invisiblePlayers.contains(player);
    }

    public Set<Player> getAllInvisiblePlayers() {
        return invisiblePlayers;
    }

}
