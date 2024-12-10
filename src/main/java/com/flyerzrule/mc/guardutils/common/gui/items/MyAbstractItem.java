package com.flyerzrule.mc.guardutils.common.gui.items;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import xyz.xenondevs.invui.item.impl.AbstractItem;

public abstract class MyAbstractItem extends AbstractItem {
  @Override
  public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
  }
}
