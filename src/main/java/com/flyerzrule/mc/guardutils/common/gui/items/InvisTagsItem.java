package com.flyerzrule.mc.guardutils.common.gui.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.database.SettingsDao;

import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class InvisTagsItem extends AbstractItem {
  private SettingsDao settingsDao;
  private InvisTagsType type;

  private boolean isEnabled;

  public InvisTagsItem(InvisTagsType type) {
    this.type = type;
    this.settingsDao = SettingsDao.getInstance();

    if (type == InvisTagsType.GUARD) {
      this.isEnabled = settingsDao.getGuardInvisTagSetting();
    } else {
      this.isEnabled = settingsDao.getKOSInvisTagSetting();
    }

    GuardUtils.getMyLogger().sendDebug(String.format("Invis tags for %s are %s", type.toString(), isEnabled));
  }

  @Override
  public ItemProvider getItemProvider() {
    String typeStr = (type == InvisTagsType.GUARD) ? "Guard" : "KOS";

    String title = "Toggle " + typeStr + " Tags";
    String state = "State: " + (isEnabled ? "Enabled" : "Disabled");
    String info = "Global (Admin Only)";

    if (isEnabled) {
      ItemStack potion = new ItemStack(Material.POTION);
      PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
      if (potionMeta != null) {
        potionMeta.setBasePotionType(PotionType.INVISIBILITY);
        potionMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        potion.setItemMeta(potionMeta);
      }
      return new ItemBuilder(potion).setDisplayName(title).addLoreLines(state, info);
    }
    return new ItemBuilder(Material.GLASS_BOTTLE).setDisplayName(title).addLoreLines(state, info);
  }

  @Override
  public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
    if (type == InvisTagsType.GUARD) {
      this.isEnabled = settingsDao.toggleGuardInvisTagSetting();
    } else {
      this.isEnabled = settingsDao.toggleKOSInvisTagSetting();
    }
    notifyWindows();
  }
}
