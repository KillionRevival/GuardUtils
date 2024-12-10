package com.flyerzrule.mc.guardutils.common.gui.panels;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.database.GuardStatsDao;
import com.flyerzrule.mc.guardutils.database.models.GuardStats;
import com.flyerzrule.mc.guardutils.utils.time.TimeUtils;

import co.killionrevival.killioncommons.ui.items.BorderItemWithBack;
import co.killionrevival.killioncommons.ui.items.MySimpleItem;
import co.killionrevival.killioncommons.ui.panels.Panel;
import xyz.xenondevs.invui.gui.Gui;

public class GuardStatsPanel extends Panel {
  private final GuardStatsDao guardStatsDao;

  public GuardStatsPanel(Player player) {
    super(player, "Your Guard Stats");

    this.guardStatsDao = GuardStatsDao.getInstance();
  }

  @Override
  protected Gui createGui() {
    GuardStats stats = this.guardStatsDao.getGuardStats(this.player);

    if (stats == null) {
      this.guardStatsDao.addNewGuardStats(player);
      stats = this.guardStatsDao.getGuardStats(this.player);
    }

    String guardTime;
    if (stats.getGuardTime() == 0) {
      guardTime = "N/A";
    } else {
      guardTime = TimeUtils.getFormattedTimeFromSeconds(stats.getGuardTime());
    }
    Double kda = (stats.getDeaths() == 0) ? 0.0 : stats.getKills() / stats.getDeaths();
    String kdaStr = String.format("K/D: %.2f", kda);

    return Gui.normal().setStructure(
        "@ ^ @ ^ @ ^ @ ^ @",
        "^ . K . D . T . ^",
        "@ ^ @ ^ @ ^ @ ^ @")
        .addIngredient('^', new BorderItemWithBack(Material.ORANGE_STAINED_GLASS_PANE, this::openMain))
        .addIngredient('@', new BorderItemWithBack(Material.BLUE_STAINED_GLASS_PANE, this::openMain))
        .addIngredient('K',
            new MySimpleItem(Material.NETHERITE_SWORD, "Kills: " + stats.getKills(), new String[] { kdaStr }))
        .addIngredient('D',
            new MySimpleItem(Material.SKELETON_SKULL, "Deaths: " + stats.getDeaths()))
        .addIngredient('T',
            new MySimpleItem(Material.CLOCK, "Guard Time: " + guardTime))
        .build();
  }

  private void openMain() {
    MainPanel mainPanel = new MainPanel(this.player);
    mainPanel.open();
  }
  
}
