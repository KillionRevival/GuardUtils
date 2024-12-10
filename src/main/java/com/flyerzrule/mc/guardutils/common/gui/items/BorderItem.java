package com.flyerzrule.mc.guardutils.common.gui.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class BorderItem extends AbstractItem {
  private final Material material;
  private final Runnable clickFunction;

  public BorderItem(Material material, Runnable clickFunction) {
    this.material = material;
    this.clickFunction = clickFunction;
  }

  @Override
  public ItemProvider getItemProvider() {
    return new ItemBuilder(this.material).setDisplayName("§r");
  }

  @Override
  public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
    this.clickFunction.run();
  }
}
