package com.flyerzrule.mc.guardutils.common.gui.panels;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import com.flyerzrule.mc.guardutils.common.gui.panels.helper.Panel;
import com.flyerzrule.mc.guardutils.common.gui.items.BorderItemWithBack;
import com.flyerzrule.mc.guardutils.common.gui.items.MySimpleItem;

public class HelpPanel extends Panel {

  public HelpPanel(Player player) {
    super(player, "Guard Help");

    createGui();
  }

  private void createGui() {
    String swordHelp = "Requests that a player drops their sword";
    String bowHelp = "Requests that a player drops their bow";
    String[] otherContrabandHelp = new String[] { "Requests that a player drops their other contraband.",
        "You will need to specify the item type (There is autocomplete)." };
    String[] kosHelp = new String[] { "Places a player on KOS for the specified amount of time.",
        "The server will keep track of the time." };
    String[] guardHelp = new String[] { "Open the guard menu.",
        "View the current KOS players, guard stats, ",
        "and toggle the guard scoreboard." };

    this.gui = Gui.normal().setStructure(
        "@ ^ @ ^ @ ^ @ ^ @",
        "^ . . . . . . . ^",
        "@ . S . B . O . @",
        "^ . . K . G . . ^",
        "@ . . . . . . . @",
        "^ @ ^ @ ^ @ ^ @ ^")
        .addIngredient('^',
            new BorderItemWithBack(Material.ORANGE_STAINED_GLASS_PANE,
                this::openMain))
        .addIngredient('@',
            new BorderItemWithBack(Material.BLUE_STAINED_GLASS_PANE,
                this::openMain))
        .addIngredient('S',
            new MySimpleItem(Material.DIAMOND_SWORD, "/sword",
                new String[] { swordHelp }))
        .addIngredient('B',
            new MySimpleItem(Material.BOW, "/bow", new String[] { bowHelp }))
        .addIngredient('O',
            new MySimpleItem(Material.TRIDENT, "/cb", otherContrabandHelp))
        .addIngredient('K',
            new MySimpleItem(Material.CROSSBOW, "/kos", kosHelp))
        .addIngredient('G',
            new MySimpleItem(Material.CHAINMAIL_CHESTPLATE, "/guard", guardHelp))
        .build();

    this.window = Window.single().setViewer(this.player).setGui(gui).setTitle(this.title).build();
  }

  private void openMain() {
    MainPanel mainPanel = new MainPanel(this.player);
    mainPanel.open();
  }

}
