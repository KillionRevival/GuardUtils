package com.flyerzrule.mc.guardutils.common.gui.panels;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.common.gui.items.KOSPlayerItem;
import com.flyerzrule.mc.guardutils.common.gui.items.MySimpleItem;
import com.flyerzrule.mc.guardutils.common.gui.items.ScrollDownItem;
import com.flyerzrule.mc.guardutils.common.gui.items.ScrollUpItem;
import com.flyerzrule.mc.guardutils.common.gui.panels.helper.Panel;
import com.flyerzrule.mc.guardutils.kos.KOSTimer;
import com.flyerzrule.mc.guardutils.kos.KOSTimerPlayer;

import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

public class KosPanel extends Panel {
  private final KOSTimer kosTimer;

  public KosPanel(Player player) {
    super(player, "Current KOS'd Players");

    this.kosTimer = KOSTimer.getInstance();

    createGui();

  }

  private void createGui() {
    List<KOSTimerPlayer> kosPlayers = this.kosTimer.getKOSTimerPlayers();

    kosPlayers.sort(Comparator.comparing(KOSTimerPlayer::getEndTime));

    List<Item> kosPlayerItems = kosPlayers.stream().map(KOSPlayerItem::new).collect(Collectors.toList());
    this.gui = ScrollGui.items().setStructure(
        "x x x x x x x x u",
        "x x x x x x x x #",
        "x x x x x x x x B",
        "x x x x x x x x R",
        "x x x x x x x x #",
        "x x x x x x x x d")
        .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
        .addIngredient('u', new ScrollUpItem())
        .addIngredient('d', new ScrollDownItem())
        .addIngredient('B',
            new MySimpleItem(Material.ARROW, "Back", this::openMain))
        .addIngredient('R',
            new MySimpleItem(Material.COMPASS, "Refresh", this::refreshKos))
        .setContent(kosPlayerItems)
        .build();
    this.window = Window.single().setViewer(this.player).setGui(gui).setTitle(this.title).build();
  }

  private void openMain() {
    MainPanel mainPanel = new MainPanel(this.player);
    mainPanel.open();
  }

  private void refreshKos() {
    this.createGui();
    this.open();
  }
}
