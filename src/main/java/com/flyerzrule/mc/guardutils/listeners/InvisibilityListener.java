package com.flyerzrule.mc.guardutils.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.potion.PotionEffectType;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.invis.InvisPlayers;

public class InvisibilityListener implements Listener {
    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            InvisPlayers invisPlayers = InvisPlayers.getInstance();
            if (event.getModifiedType().equals(PotionEffectType.INVISIBILITY)) {
                if (event.getCause().equals(Cause.POTION_DRINK) || event.getCause().equals(Cause.POTION_SPLASH)
                        || event.getCause().equals(Cause.ARROW)) {
                    GuardUtils.getMyLogger().sendDebug(String.format("Adding %s to invis list. Reason: %s",
                            player.getName(), event.getCause().name()));
                    invisPlayers.addPlayer(player);
                } else if (event.getAction().equals(EntityPotionEffectEvent.Action.REMOVED)) {
                    GuardUtils.getMyLogger().sendDebug(String.format("Removing %s from invis list.", player.getName()));
                    invisPlayers.removePlayer(player);
                }
            }
        }
    }
}
