package com.flyerzrule.mc.guardutils.common.gui;

import java.security.Guard;
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

import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.common.gui.items.BorderItem;
import com.flyerzrule.mc.guardutils.common.gui.items.InvisTagsItem;
import com.flyerzrule.mc.guardutils.common.gui.items.InvisTagsType;
import com.flyerzrule.mc.guardutils.common.gui.items.KOSPlayerItem;
import com.flyerzrule.mc.guardutils.common.gui.items.ScoreboardItem;
import com.flyerzrule.mc.guardutils.common.gui.items.ScrollDownItem;
import com.flyerzrule.mc.guardutils.common.gui.items.ScrollUpItem;
import com.flyerzrule.mc.guardutils.common.gui.items.MySimpleItem;
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
    GuardUtils.getMyLogger().sendDebug("Created main GUI");
    this.window = Window.single().setViewer(this.player).setTitle("Guard Menu").setGui(this.mainGui)
        .build();
    GuardUtils.getMyLogger().sendDebug("Opened main window");
    this.window.open();
  }

  public void openKos() {
    Gui gui = createKosGui();
    this.window = Window.single().setViewer(this.player).setTitle("Current KOS'd Players").setGui(gui)
        .build();
    this.window.open();
  }

  public void openStats() {
    Gui gui = createStatsGui();
    this.window = Window.single().setViewer(this.player).setTitle("Your Guard Stats").setGui(gui)
        .build();
    this.window.open();
  }

  public void openHelp() {
    Gui gui = createHelpGui();
    this.window = Window.single().setViewer(this.player).setTitle("Guard Help").setGui(gui)
        .build();
    this.window.open();
  }

  public void close() {
    this.window.close();
  }

  private Gui createMainGui() {
    String settingsGuiLine = "^ . . . s . . . ^";
    if (Permissions.hasPermission(player, "guardutils.admin")) {
      settingsGuiLine = "^ . s . G . k . ^";
    }

    return Gui.normal().setStructure(
        "@ ^ @ ^ @ ^ @ ^ @",
        "^ . . . . . . . ^",
        "@ . K . S . H . @",
        settingsGuiLine,
        "@ . . . . . . . @",
        "^ @ ^ @ ^ @ ^ @ ^")
        .addIngredient('K', new MySimpleItem(Material.TARGET, "KOS Players", this::openKos))
        .addIngredient('S', new MySimpleItem(Material.DIAMOND_SWORD, "Guard Stats", this::openStats))
        .addIngredient('H', new MySimpleItem(Material.KNOWLEDGE_BOOK, "Help", this::openHelp))
        .addIngredient('s', new ScoreboardItem(player))
        .addIngredient('G',
            new InvisTagsItem(InvisTagsType.GUARD))
        .addIngredient('k',
            new InvisTagsItem(InvisTagsType.KOS))
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
        .addIngredient('B',
            new MySimpleItem(Material.ARROW, "Back", this::openMain))
        .addIngredient('R',
            new MySimpleItem(Material.COMPASS, "Refresh", this::refreshKos))
        .setContent(kosPlayerItems)
        .build();
  }

  private Gui createStatsGui() {
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
        .addIngredient('^', new BorderItem(Material.ORANGE_STAINED_GLASS_PANE, this::openMain))
        .addIngredient('@', new BorderItem(Material.BLUE_STAINED_GLASS_PANE, this::openMain))
        .addIngredient('K',
            new MySimpleItem(Material.NETHERITE_SWORD, "Kills: " + stats.getKills(), new String[] { kdaStr }))
        .addIngredient('D',
            new MySimpleItem(Material.SKELETON_SKULL, "Deaths: " + stats.getDeaths()))
        .addIngredient('T',
            new MySimpleItem(Material.CLOCK, "Guard Time: " + guardTime))
        .build();
  }

  private Gui createHelpGui() {
    String swordHelp = "Requests that a player drops their sword";
    String bowHelp = "Requests that a player drops their bow";
    String[] otherContrabandHelp = new String[] { "Requests that a player drops their other contraband.",
        "You will need to specify the item type (There is autocomplete)." };
    String[] kosHelp = new String[] { "Places a player on KOS for the specified amount of time.",
        "The server will keep track of the time." };
    String[] guardHelp = new String[] { "Open the guard menu.",
        "View the current KOS players, guard stats, ",
        "and toggle the guard scoreboard." };

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
            new MySimpleItem(Material.DIAMOND_SWORD, "/sword", new String[] { swordHelp }))
        .addIngredient('B',
            new MySimpleItem(Material.BOW, "/bow", new String[] { bowHelp }))
        .addIngredient('O',
            new MySimpleItem(Material.TRIDENT, "/cb", otherContrabandHelp))
        .addIngredient('K',
            new MySimpleItem(Material.CROSSBOW, "/kos", kosHelp))
        .addIngredient('G',
            new MySimpleItem(Material.CHAINMAIL_CHESTPLATE, "/guard", guardHelp))
        .build();
  }

  private void refreshKos() {
    this.openKos();
  }
}
