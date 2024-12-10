package com.flyerzrule.mc.guardutils.common.gui.items;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.kos.KOSTimerPlayer;
import com.flyerzrule.mc.guardutils.utils.time.TimeUtils;
import com.flyerzrule.mc.guardutils.utils.time.models.MinSec;

import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class KOSPlayerItem extends MyAbstractItem {
  private KOSTimerPlayer player;

  public KOSPlayerItem(KOSTimerPlayer player) {
    this.player = player;
  }

  @Override
  public ItemProvider getItemProvider() {
    ItemStack head = getPlayerSkull();
    String playerName = this.player.getPlayer().getName();
    String guardName = this.player.getGuard().getName();

    String guardNameLore = String.format("Guard: %s", guardName);
    String timeLeft = this.getTimeLeft();

    return new ItemBuilder(head).setDisplayName(playerName).addLoreLines(guardNameLore, timeLeft);
  }

  private ItemStack getPlayerSkull() {
    if (this.player.getSkinUrl() == null) {
      return new ItemStack(Material.PLAYER_HEAD);
    }

    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

    GameProfile profile = new GameProfile(UUID.randomUUID(), null);

    profile.getProperties().put("textures", new Property("textures", this.player.getSkinUrl()));
    try {
      Field profileField = skullMeta.getClass().getDeclaredField("profile");
      profileField.setAccessible(true);
      profileField.set(skullMeta, profile);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      GuardUtils.getMyLogger().sendThrowable(e);
    }

    skull.setItemMeta(skullMeta);
    return skull;
  }

  private String getTimeLeft() {
    long timeLeft = this.player.getEndTime() - System.currentTimeMillis();
    MinSec minSec = TimeUtils.getMinutesAndSecondsFromMilli(timeLeft);
    return String.format("%d:%d left", minSec.getMinutes(), minSec.getSeconds());
  }
}
