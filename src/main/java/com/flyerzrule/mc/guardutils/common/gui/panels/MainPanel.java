package com.flyerzrule.mc.guardutils.common.gui.panels;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.common.gui.items.InvisTagsItem;
import com.flyerzrule.mc.guardutils.common.gui.items.InvisTagsType;
import com.flyerzrule.mc.guardutils.common.gui.items.ScoreboardItem;
import com.flyerzrule.mc.guardutils.utils.Permissions;

import co.killionrevival.killioncommons.ui.items.MySimpleItem;
import co.killionrevival.killioncommons.ui.panels.Panel;
import xyz.xenondevs.invui.gui.Gui;

public class MainPanel extends Panel {
  public MainPanel(Player player) {
    super(player, "Guard Menu");
  }

  @Override
  protected Gui createGui() {
    String settingsGuiLine = "^ . . . s . . . ^";
    if (Permissions.hasPermission(player, "guardutils.admin")) {
      settingsGuiLine = "^ . s . G . k . ^";
    }

    return Gui.normal().setStructure(
        "@ ^ @ ^ @ ^ @ ^ @",
        "^ . . . . . . . ^",
        "@ . K . S . H . @",
        settingsGuiLine,
        "@ . . . . . . . @",
        "^ @ ^ @ ^ @ ^ @ ^")
        .addIngredient('K', new MySimpleItem(Material.TARGET, "KOS Players", () -> new KosPanel(this.player).open()))
        .addIngredient('S', new MySimpleItem(Material.DIAMOND_SWORD, "Guard Stats", () -> new GuardStatsPanel(this.player).open()))
        .addIngredient('H', new MySimpleItem(Material.KNOWLEDGE_BOOK, "Help", () -> new HelpPanel(this.player).open()))
        .addIngredient('s', new ScoreboardItem(player))
        .addIngredient('G',
            new InvisTagsItem(InvisTagsType.GUARD))
        .addIngredient('k',
            new InvisTagsItem(InvisTagsType.KOS))
        .build();
  }
}
