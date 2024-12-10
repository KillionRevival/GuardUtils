package com.flyerzrule.mc.guardutils.common.gui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.window.Window;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import com.flyerzrule.mc.guardutils.common.gui.items.BorderItem;
import com.flyerzrule.mc.guardutils.common.gui.items.InvisTagsItem;
import com.flyerzrule.mc.guardutils.common.gui.items.KOSPlayerItem;
import com.flyerzrule.mc.guardutils.common.gui.items.ScoreboardItem;
import com.flyerzrule.mc.guardutils.common.gui.items.ScrollDownItem;
import com.flyerzrule.mc.guardutils.common.gui.items.ScrollUpItem;
import com.flyerzrule.mc.guardutils.database.GuardStatsDao;
import com.flyerzrule.mc.guardutils.database.models.GuardStats;
import com.flyerzrule.mc.guardutils.kos.KOSTimer;
import com.flyerzrule.mc.guardutils.kos.KOSTimerPlayer;
import com.flyerzrule.mc.guardutils.utils.Permissions;
import com.flyerzrule.mc.guardutils.utils.time.TimeUtils;

public class GuardPanel {
  private final Player player;

  private Gui mainGui;

  private Window window;

  private final KOSTimer kosTimer;
  private final GuardStatsDao guardStatsDao;

  public GuardPanel(Player player) {
    this.player = player;

    this.kosTimer = KOSTimer.getInstance();
    this.guardStatsDao = GuardStatsDao.getInstance();

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
        "^ @ ^ @ ^ @ ^ @ ^")
        .addIngredient('K',
            new SimpleItem(new ItemBuilder(Material.TARGET).setDisplayName("KOS Players"), event -> openKos()))
        .addIngredient('S',
            new SimpleItem(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("Guard Stats"), event -> openKos()))
        .addIngredient('H',
            new SimpleItem(new ItemBuilder(Material.KNOWLEDGE_BOOK).setDisplayName("Help"), event -> openKos()))
        .addIngredient('s', new ScoreboardItem(player))
        .addIngredient('I',
            new InvisTagsItem())
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
    GuardStats stats = this.guardStatsDao.getGuardStats(this.player.getUniqueId().toString());

    String guardTime = TimeUtils.getFormattedTimeFromSeconds(stats.getGuardTime());
    String kda = String.format("K/D: %2f", stats.getKills() / stats.getDeaths());

    return Gui.normal().setStructure(
        "@ ^ @ ^ @ ^ @ ^ @",
        "^ . K . D . T . ^",
        "@ ^ @ ^ @ ^ @ ^ @")
        .addIngredient('^', new BorderItem(Material.ORANGE_STAINED_GLASS_PANE, this::openMain))
        .addIngredient('@', new BorderItem(Material.BLUE_STAINED_GLASS_PANE, this::openMain))
        .addIngredient('K',
            new SimpleItem(
                new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("Kills: " + stats.getKills()).addLoreLines(kda)))
        .addIngredient('D',
            new SimpleItem(new ItemBuilder(Material.SKELETON_SKULL).setDisplayName("Deaths: " + stats.getDeaths())))
        .addIngredient('T',
            new SimpleItem(new ItemBuilder(Material.CLOCK).setDisplayName("Guard Time: " + guardTime)))
        .build();
  }

  private Gui createHelpGui() {
    String swordHelp = "Requests that a player drops their sword";
    String bowHelp = "Requests that a player drops their bow";
    String otherContrabandHelp = "Requests that a player drops their other contraband. You will need to specify the item type (There is autocomplete).";
    String kosHelp = "Places a player on KOS for the specified amount of time. The server will keep track of the time.";
    String guardHelp = "Open the guard menu. View the current KOS players, guard stats, and toggle the guard scoreboard.";

    return Gui.normal().setStructure(
        "@ ^ @ ^ @ ^ @ ^ @",
        "^ . . . . . . . ^",
        "@ . S . B . O . @",
        "^ . . K . G . . ^",
        "@ . . . . . . . @",
        "^ @ ^ @ ^ @ ^ @ ^")
        .addIngredient('^',
            new BorderItem(Material.ORANGE_STAINED_GLASS_PANE, this::openMain))
        .addIngredient('@',
            new BorderItem(Material.BLUE_STAINED_GLASS_PANE, this::openMain))
        .addIngredient('S',
            new SimpleItem(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("/sword").addLoreLines(swordHelp)))
        .addIngredient('B',
            new SimpleItem(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("/bow").addLoreLines(bowHelp)))
        .addIngredient('O',
            new SimpleItem(
                new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("/cb").addLoreLines(otherContrabandHelp)))
        .addIngredient('K',
            new SimpleItem(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("/kos").addLoreLines(kosHelp)))
        .addIngredient('G',
            new SimpleItem(new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("/guard").addLoreLines(guardHelp)))
        .build();
  }

  private void refreshKos() {
    this.openKos();
  }
}
