package com.flyerzrule.mc.guardutils.duty.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.duty.utils.GuardDuty;

import co.killionrevival.killioncommons.ui.items.MySimpleItem;
import co.killionrevival.killioncommons.ui.panels.Panel;
import xyz.xenondevs.invui.gui.Gui;

public class GuardConfirmPanel extends Panel {
  private final Player player;
  private final boolean goingOffDuty;

  public GuardConfirmPanel(Player player, boolean goingOffDuty) {
    super(player, (goingOffDuty == true) ? "Confirm: Go Off Duty (You will be killed)"
    : "Confirm: Go On Duty (You will be killed)");
    this.player = player;
    this.goingOffDuty = goingOffDuty;

    this.createGui();
  }

  protected Gui createGui() {
    return Gui.normal().setStructure(
        "@ ^ @ ^ @ ^ @ ^ @",
        "^ . . y . n . . ^",
        "@ ^ @ ^ @ ^ @ ^ @")
        .addIngredient('y', new MySimpleItem(Material.GREEN_WOOL, "CONFIRM", this::confirmDutyChange))
        .addIngredient('n', new MySimpleItem(Material.RED_WOOL, "CANCEL", this::cancelDutyChange))
        .build();
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
