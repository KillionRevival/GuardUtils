package com.flyerzrule.mc.guardutils.common.gui.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import com.flyerzrule.mc.guardutils.database.SettingsDao;

import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class InvisTagsItem extends AbstractItem {
  private boolean isEnabled;
  private SettingsDao settingsDao;

  public InvisTagsItem() {
    this.settingsDao = SettingsDao.getInstance();
    this.isEnabled = settingsDao.getInvisTagSetting();
  }

  @Override
  public ItemProvider getItemProvider() {
    String title = "Toggle Invis Tags";
    String state = "State: " + (isEnabled ? "Enabled" : "Disabled");
    String info = "Global (Admin Only)";

    if (isEnabled) {
      ItemStack potion = new ItemStack(Material.POTION);
      PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
      if (potionMeta != null) {
        potionMeta.setBasePotionType(PotionType.INVISIBILITY);
        potion.setItemMeta(potionMeta);
      }
      return new ItemBuilder(potion).setDisplayName(title).addLoreLines(state);
    }
    return new ItemBuilder(Material.GLASS_BOTTLE).setDisplayName(title).addLoreLines(state, info);
  }

  @Override
  public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
    this.isEnabled = settingsDao.toggleInvisTagSetting();
    notifyWindows();
  }
}
