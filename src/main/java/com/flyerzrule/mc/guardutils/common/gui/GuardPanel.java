package com.flyerzrule.mc.guardutils.common.gui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.K;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.window.Window;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import com.flyerzrule.mc.guardutils.common.gui.items.KOSPlayerItem;
import com.flyerzrule.mc.guardutils.common.gui.items.ScrollDownItem;
import com.flyerzrule.mc.guardutils.common.gui.items.ScrollUpItem;
import com.flyerzrule.mc.guardutils.kos.KOSTimer;
import com.flyerzrule.mc.guardutils.kos.KOSTimerPlayer;
import com.flyerzrule.mc.guardutils.utils.Permissions;

public class GuardPanel {
  private final Player player;

  private Gui mainGui;

  private Window window;

  private KOSTimer kosTimer;

  public GuardPanel(Player player) {
    this.player = player;

    this.kosTimer = KOSTimer.getInstance();

    this.openMain();
  }

  public void openMain() {
    this.mainGui = createMainGui();
    this.window = Window.single().setViewer(this.player).setTitle("Guard Menu").setGui(this.mainGui)
        .build();
  }

  public void openKos() {
    Gui gui = createKosGui();
    this.window = Window.single().setViewer(this.player).setTitle("Current KOS'd Players").setGui(gui)
        .build();
  }

  public void openStats() {
    Gui gui = createStatsGui();
    this.window = Window.single().setViewer(this.player).setTitle("Your Guard Stats").setGui(gui)
        .build();
  }

  public void openHelp() {
    Gui gui = createHelpGui();
    this.window = Window.single().setViewer(this.player).setTitle("Guard Help").setGui(gui)
        .build();
  }

  public void openOnOff(String title) {
    Gui gui = createOnOffGui();
    this.window = Window.single().setViewer(this.player).setTitle(title).setGui(gui)
        .build();
  }

  public void close() {
    this.window.close();
  }

  private Gui createMainGui() {
    String settingsGuiLine = "^ . . . s . . . ^";
    if (Permissions.hasPermission(player, "guardutils.admin")) {
      settingsGuiLine = "^ . . s . . I . . ^";
    }

    return Gui.normal().setStructure(
        "@ ^ @ ^ @ ^ @ ^ @",
        "^ . . . . . . . ^",
        "@ . K . S . H . @",
        settingsGuiLine,
        "@ . . . . . . . @",
        "^ @ ^ @ ^ @ ^ @ ^").addIngredient('^',
            new SimpleItem(new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE)
                .setDisplayName("§r")))
        .addIngredient('@',
            new SimpleItem(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE)
                .setDisplayName("§r")))
        .addIngredient('K',
            new SimpleItem(new ItemBuilder(Material.TARGET).setDisplayName("KOS Players"), event -> openKos()))
        .addIngredient('S',
            new SimpleItem(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("Guard Stats"), event -> openKos()))
        .addIngredient('H',
            new SimpleItem(new ItemBuilder(Material.KNOWLEDGE_BOOK).setDisplayName("Help"), event -> openKos()))
        .addIngredient('s',
            new SimpleItem(new ItemBuilder(Material.OAK_SIGN).setDisplayName("Toggle Scoreboard"),
                event -> toggleScoreboard()))
        .addIngredient('I',
            new SimpleItem(new ItemBuilder(Material.ENDER_EYE).setDisplayName("Toggle Invis Tags"),
                event -> toggleInvisTags()))
        .build();
  }

  private Gui createKosGui() {
    List<KOSTimerPlayer> kosPlayers = this.kosTimer.getKOSTimerPlayers();

    kosPlayers.sort(Comparator.comparing(KOSTimerPlayer::getEndTime));

    List<Item> kosPlayerItems = kosPlayers.stream().map(KOSPlayerItem::new).collect(Collectors.toList());

    return ScrollGui.items().setStructure(
        "x x x x x x x x u",
        "x x x x x x x x #",
        "x x x x x x x x B",
        "x x x x x x x x R",
        "x x x x x x x x #",
        "x x x x x x x x d")
        .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
        .addIngredient('u', new ScrollUpItem())
        .addIngredient('d', new ScrollDownItem())
        .addIngredient('B', new SimpleItem(new ItemBuilder(Material.ARROW).setDisplayName("Back"), event -> openMain()))
        .addIngredient('R',
            new SimpleItem(new ItemBuilder(Material.COMPASS).setDisplayName("Refresh"), event -> refreshKos()))
        .setContent(kosPlayerItems)
        .build();
  }

  private Gui createStatsGui() {

  }

  private Gui createHelpGui() {

  }

  private Gui createOnOffGui() {

  }

  private void refreshKos() {

  }

  private void toggleScoreboard() {

  }

  private void toggleInvisTags() {
    Boolean currentSetting
  }

}
