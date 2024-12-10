package com.flyerzrule.mc.guardutils.duty.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.duty.utils.GuardDuty;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class GuardConfirmPanel {
  private final Player player;
  private final boolean goingOffDuty;

  private Gui gui;
  private Window window;

  public GuardConfirmPanel(Player player, boolean goingOffDuty) {
    this.player = player;
    this.goingOffDuty = goingOffDuty;

    this.createGui();
  }

  private void createGui() {
    this.gui = Gui.normal().setStructure(
        "@ ^ @ ^ @ ^ @ ^ @",
        "^ . . y . n . . ^",
        "@ ^ @ ^ @ ^ @ ^ @").addIngredient('^',
            new SimpleItem(new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE)
                .setDisplayName("§r")))
        .addIngredient('@',
            new SimpleItem(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE)
                .setDisplayName("§r")))
        .addIngredient('y',
            new SimpleItem(new ItemBuilder(Material.GREEN_WOOL)
                .setDisplayName("CONFIRM"),
                event -> confirmDutyChange()))
        .addIngredient('n',
            new SimpleItem(new ItemBuilder(Material.RED_WOOL)
                .setDisplayName("CANCEL"),
                event -> cancelDutyChange()))
        .build();

    String title = (goingOffDuty == true) ? "Confirm: Go Off Duty (You will be killed)"
        : "Confirm: Go On Duty (You will be killed)";

    this.window = Window.single().setViewer(this.player).setTitle(title).setGui(this.gui).build();
  }

  private void confirmDutyChange() {
    if (goingOffDuty) {
      GuardDuty.becomePlayer(player);
    } else {
      GuardDuty.becomeGuard(player);
    }
    this.close();
  }

  private void cancelDutyChange() {
    this.close();
  }

  private void close() {
    this.window.close();
  }
}
