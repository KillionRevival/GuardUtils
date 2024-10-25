package com.flyerzrule.mc.guardutils.duty.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.duty.database.DutyDatabase;
import com.flyerzrule.mc.guardutils.duty.utils.GuardDuty;
import com.flyerzrule.mc.guardutils.utils.time.TimeUtils;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.Arrays;
import java.util.List;

public class GuardPanel {
    private final Player player;

    private Gui mainGui;
    private Gui confirmGui;

    private Window window;

    public GuardPanel(Player player) {
        this.player = player;

        this.createMainGui();
    }

    private void createMainGui() {
        DutyDatabase db = DutyDatabase.getInstance();

        boolean onDuty = db.hasPlayerInfo(this.player.getUniqueId().toString());
        ItemBuilder dutySwitchItem = (onDuty == true) ? new ItemBuilder(Material.GRASS_BLOCK)
                : new ItemBuilder(Material.CHAINMAIL_CHESTPLATE);
        String dutySwitchName = (onDuty == true) ? "Go Off Duty" : "Go On Duty";

        int guardKills = db.getGuardStats(this.player.getUniqueId().toString()).getKills();
        int guardDeaths = db.getGuardStats(this.player.getUniqueId().toString()).getDeaths();
        int guardTime = db.getGuardStats(this.player.getUniqueId().toString()).getGuardTime();

        String guardKillsString = String.format("Kills: %d", guardKills);
        String guardDeathsString = String.format("Deaths: %d", guardDeaths);
        String guardKDString = String.format("K/D: %2f", guardKills / guardDeaths);
        String guardTimeString = String.format("Time On Duty: %s",
                TimeUtils.getFormattedTimeFromSeconds(guardTime));

        List<String> statsLore = Arrays.asList(guardKillsString, guardDeathsString, guardKDString, guardTimeString);

        String.format("Kills: %d\nDeaths: %d\nTime: %d", guardKills, guardDeaths,
                TimeUtils.getFormattedTimeFromSeconds(guardTime));

        this.mainGui = Gui.normal().setStructure(
                "@ ^ @ ^ @ ^ @ ^ @",
                "^ . . . . . . . ^",
                "@ . . . d . . . @",
                "^ . . . s . . . ^",
                "@ . . . . . . . @",
                "^ @ ^ @ ^ @ ^ @ ^").addIngredient('^',
                        new SimpleItem(new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE)
                                .setDisplayName("§r")))
                .addIngredient('@',
                        new SimpleItem(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE)
                                .setDisplayName("§r")))
                .addIngredient('d', new SimpleItem(dutySwitchItem.setDisplayName(dutySwitchName),
                        event -> openConfirmation(onDuty)))
                .addIngredient('s',
                        new SimpleItem(new ItemBuilder(Material.WRITABLE_BOOK).setDisplayName("Guard Stats")
                                .setLegacyLore(statsLore)))
                .build();
    }

    private void openConfirmation(boolean goingOffDuty) {
        this.confirmGui = Gui.normal().setStructure(
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
                                event -> confirmDutySwitch(goingOffDuty)))
                .addIngredient('n',
                        new SimpleItem(new ItemBuilder(Material.RED_WOOL)
                                .setDisplayName("CONFIRM"),
                                event -> cancelDutySwitch()))
                .build();

        String title = (goingOffDuty == true) ? "Confirm: Go Off Duty" : "Confirm: Go On Duty";

        this.window = Window.single().setViewer(this.player).setTitle(title).setGui(this.confirmGui).build();
    }

    private void confirmDutySwitch(boolean goingOffDuty) {
        if (goingOffDuty) {
            GuardDuty.becomePlayer(this.player.getUniqueId().toString());
        } else {
            GuardDuty.becomeGuard(this.player.getUniqueId().toString());
        }
        this.close();
    }

    private void cancelDutySwitch() {
        this.openMain();
    }

    private void openMain() {
        this.window = Window.single().setViewer(this.player).setTitle("Guard Menu").setGui(this.mainGui).build();
    }

    private void close() {
        this.window.close();
    }
}
