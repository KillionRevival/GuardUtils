package com.flyerzrule.mc.guardutils.common.gui.panels;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.common.gui.items.InvisTagsItem;
import com.flyerzrule.mc.guardutils.common.gui.items.InvisTagsType;
import com.flyerzrule.mc.guardutils.common.gui.items.ScoreboardItem;
import com.flyerzrule.mc.guardutils.utils.Permissions;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import com.flyerzrule.mc.guardutils.common.gui.items.MySimpleItem;
import com.flyerzrule.mc.guardutils.common.gui.panels.helper.Panel;

public class MainPanel extends Panel {

  public MainPanel(Player player) {
    super(player, "Guard Menu");

    createGui();
  }

  private void createGui() {
    String settingsGuiLine = "^ . . . s . . . ^";
    if (Permissions.hasPermission(this.player, Permissions.ADMIN)) {
      settingsGuiLine = "^ . s . G . k . ^";
    }

    this.gui = Gui.normal().setStructure(
        "@ ^ @ ^ @ ^ @ ^ @",
        "^ . . . . . . . ^",
        "@ . K . S . H . @",
        settingsGuiLine,
        "@ . . . . . . . @",
        "^ @ ^ @ ^ @ ^ @ ^")
        .addIngredient('K', new MySimpleItem(Material.TARGET, "KOS Players", this::openKosPanel))
        .addIngredient('S', new MySimpleItem(Material.DIAMOND_SWORD, "Guard Stats", this::openGuardStatsPanel))
        .addIngredient('H', new MySimpleItem(Material.KNOWLEDGE_BOOK, "Help", this::openHelpPanel))
        .addIngredient('s', new ScoreboardItem(this.player))
        .addIngredient('G',
            new InvisTagsItem(InvisTagsType.GUARD))
        .addIngredient('k',
            new InvisTagsItem(InvisTagsType.KOS))
        .build();

    this.window = Window.single().setViewer(this.player).setGui(gui).setTitle(this.title).build();
  }

  private void openKosPanel() {
    KosPanel kosPanel = new KosPanel(this.player);
    kosPanel.open();
  }

  private void openGuardStatsPanel() {
    GuardStatsPanel guardStatsPanel = new GuardStatsPanel(this.player);
    guardStatsPanel.open();
  }

  private void openHelpPanel() {
    HelpPanel helpPanel = new HelpPanel(this.player);
    helpPanel.open();
  }
}
