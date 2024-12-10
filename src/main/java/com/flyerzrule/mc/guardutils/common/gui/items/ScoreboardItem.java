package com.flyerzrule.mc.guardutils.common.gui.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.flyerzrule.mc.guardutils.database.UserSettingsDao;

import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class ScoreboardItem extends AbstractItem {
  private boolean isEnabled;
  private final String uuid;

  private UserSettingsDao userSettingsDao;

  public ScoreboardItem(Player player) {
    this.uuid = player.getUniqueId().toString();

    this.userSettingsDao = UserSettingsDao.getInstance();
    this.isEnabled = userSettingsDao.getScoreboardEnabled(this.uuid);
  }

  @Override
  public ItemProvider getItemProvider() {
    String title = "Toggle Guard Scoreboard";
    String state = "State: " + (isEnabled ? "Enabled" : "Disabled");

    if (isEnabled) {
      return new ItemBuilder(Material.PAPER).setDisplayName(title).addLoreLines(state);
    }
    return new ItemBuilder(Material.BARRIER).setDisplayName(title).addLoreLines(state);
  }

  @Override
  public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
    this.isEnabled = userSettingsDao.toggleScoreboardEnabled(this.uuid);
    notifyWindows();
  }

}
