package com.flyerzrule.mc.guardutils.common.gui.panels;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.common.gui.items.KOSPlayerItem;
import com.flyerzrule.mc.guardutils.kos.KOSTimer;
import com.flyerzrule.mc.guardutils.kos.KOSTimerPlayer;

import co.killionrevival.killioncommons.ui.panels.ScrollPanel;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;

public class KosPanel {
  private final ScrollPanel scrollPanel;
  private final KOSTimer kosTimer;

  private final Player player;

  public KosPanel(Player player) {
    this.player = player;

    scrollPanel = createGui();
    kosTimer = KOSTimer.getInstance();
  }

  private ScrollPanel createGui() {
    List<KOSTimerPlayer> kosPlayers = this.kosTimer.getKOSTimerPlayers();

    kosPlayers.sort(Comparator.comparing(KOSTimerPlayer::getEndTime));

    List<Item> kosPlayerItems = kosPlayers.stream().map(KOSPlayerItem::new).collect(Collectors.toList());
    return new ScrollPanel(this.player, "Current KOS'd Players", Markers.CONTENT_LIST_SLOT_HORIZONTAL, kosPlayerItems, () -> this.scrollPanel.refresh());
  }

  public void open() {
    this.scrollPanel.open();
  }
}
