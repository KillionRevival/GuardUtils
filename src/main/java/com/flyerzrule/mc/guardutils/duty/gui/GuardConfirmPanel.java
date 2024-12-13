package com.flyerzrule.mc.guardutils.duty.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.common.gui.panels.helper.Panel;
import com.flyerzrule.mc.guardutils.duty.GuardDuty;
import com.flyerzrule.mc.guardutils.common.gui.items.MySimpleItem;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

public class GuardConfirmPanel extends Panel {
  private final Player player;
  private final boolean goingOffDuty;

  public GuardConfirmPanel(Player player, boolean goingOffDuty) {
    super(player, (goingOffDuty == true) ? "Confirm: Go Off Duty"
        : "Confirm: Go On Duty");
    this.player = player;
    this.goingOffDuty = goingOffDuty;

    createGui();
  }

  private void createGui() {
    this.gui = Gui.normal().setStructure(
        "@ ^ @ ^ @ ^ @ ^ @",
        "^ . . y . n . . ^",
        "@ ^ @ ^ @ ^ @ ^ @")
        .addIngredient('y',
            new MySimpleItem(Material.GREEN_WOOL, "CONFIRM", new String[] { "You will be killed!" },
                this::confirmDutyChange))
        .addIngredient('n', new MySimpleItem(Material.RED_WOOL, "CANCEL", this::cancelDutyChange))
        .build();

    this.window = Window.single().setViewer(this.player).setGui(gui).setTitle(this.title).build();
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
}
