package com.flyerzrule.mc.guardutils;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.flyerzrule.mc.guardutils.armor.EnitityEquipmentListener;
import com.flyerzrule.mc.guardutils.armorstands.ArmorStandManager;
import com.flyerzrule.mc.guardutils.commands.GuardInvisTagCommand;
import com.flyerzrule.mc.guardutils.commands.ScoreboardToggleCommand;
import com.flyerzrule.mc.guardutils.commands.tabcomplete.OnOffTabComplete;
import com.flyerzrule.mc.guardutils.commands.tabcomplete.PlayerTabComplete;
import com.flyerzrule.mc.guardutils.duty.listeners.GuardKillDeathListener;
import com.flyerzrule.mc.guardutils.invis.InvisPlayers;
import com.flyerzrule.mc.guardutils.invis.listeners.InvisibilityListener;
import com.flyerzrule.mc.guardutils.kos.commands.KOSCommand;
import com.flyerzrule.mc.guardutils.kos.commands.tabcomplete.KOSTabComplete;
import com.flyerzrule.mc.guardutils.kos.listeners.PlayerDeathListener;
import com.flyerzrule.mc.guardutils.requests.commands.BowCommand;
import com.flyerzrule.mc.guardutils.requests.commands.OtherContrabandCommand;
import com.flyerzrule.mc.guardutils.requests.commands.SwordCommand;
import com.flyerzrule.mc.guardutils.requests.commands.tabcomplete.OtherContrabandTabComplete;
import com.flyerzrule.mc.guardutils.requests.listeners.DroppedItemListener;
import com.flyerzrule.mc.guardutils.scoreboard.listeners.PlayerHitListener;

import co.killionrevival.killioncommons.KillionUtilities;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class GuardUtils extends JavaPlugin {
  private final String pluginName = "GuardUtils";

  @Getter
  private static Plugin plugin;
  @Getter
  private static KillionUtilities killionUtilities;
  @Getter
  private static ConsoleUtil myLogger;
  @Getter
  private static LuckPerms luckperms;
  @Getter
  private static ProtocolManager protocolManager;
  @Getter
  private static SimpleClans simpleClans;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    plugin = this;
    killionUtilities = new KillionUtilities(this);
    myLogger = killionUtilities.getConsoleUtil();

    luckperms = LuckPermsProvider.get();

    protocolManager = ProtocolLibrary.getProtocolManager();

    simpleClans = (SimpleClans) Objects.requireNonNull(getServer().getPluginManager().getPlugin("SimpleClans"));

    registerProtocolListeners();

    registerCommands();
    registerTabComplete();
    registerListeners();
    registerGlobalIngredients();

    InvisPlayers.getInstance();
    ArmorStandManager.getInstance().createArmorStandTask();

    myLogger.sendSuccess(this.pluginName + " has been enabled.");
  }

  @Override
  public void onDisable() {
    ArmorStandManager.getInstance().removeAllArmorStands();
    myLogger.sendSuccess(this.pluginName + " has been disabled.");
  }

  private void registerCommands() {
    getCommand("sword").setExecutor(new SwordCommand());
    getCommand("bow").setExecutor(new BowCommand());
    getCommand("cb").setExecutor(new OtherContrabandCommand());
    getCommand("kos").setExecutor(new KOSCommand());
    getCommand("guardsb").setExecutor(new ScoreboardToggleCommand());
    getCommand("guardsinvistag").setExecutor(new GuardInvisTagCommand());

    myLogger.sendSuccess("Commands have been registered.");
  }

  private void registerTabComplete() {
    PlayerTabComplete playerTabComplete = new PlayerTabComplete();
    KOSTabComplete kosTabComplete = new KOSTabComplete();
    OtherContrabandTabComplete otherContrabandTabComplete = new OtherContrabandTabComplete();
    OnOffTabComplete onOffTabComplete = new OnOffTabComplete();

    getCommand("sword").setTabCompleter(playerTabComplete);
    getCommand("bow").setTabCompleter(playerTabComplete);
    getCommand("cb").setTabCompleter(otherContrabandTabComplete);
    getCommand("kos").setTabCompleter(kosTabComplete);
    getCommand("guardsb").setTabCompleter(onOffTabComplete);
    getCommand("guardsinvistag").setTabCompleter(onOffTabComplete);

    myLogger.sendSuccess("Tab completers have been registered.");
  }

  private void registerListeners() {
    getServer().getPluginManager().registerEvents(new DroppedItemListener(), this);
    getServer().getPluginManager().registerEvents(new PlayerHitListener(), this);
    getServer().getPluginManager().registerEvents(new InvisibilityListener(), this);
    getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
    getServer().getPluginManager().registerEvents(new GuardKillDeathListener(), this);

    myLogger.sendSuccess("Listeners have been registered.");
  }

  private void registerProtocolListeners() {
    protocolManager.addPacketListener(new EnitityEquipmentListener());

    myLogger.sendSuccess("Protocol listeners have been registered.");
  }

  private void registerGlobalIngredients() {
    Structure.addGlobalIngredient('#', new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§r"));
    Structure.addGlobalIngredient('^', new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).setDisplayName("§r"));
    Structure.addGlobalIngredient('@', new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("§r"));
  }
}