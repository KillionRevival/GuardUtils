package com.flyerzrule.mc.guardutils;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.flyerzrule.mc.guardutils.armorstands.ArmorStandManager;
import com.flyerzrule.mc.guardutils.commands.BowCommand;
import com.flyerzrule.mc.guardutils.commands.GuardInvisTagCommand;
import com.flyerzrule.mc.guardutils.commands.KOSCommand;
import com.flyerzrule.mc.guardutils.commands.OtherContrabandCommand;
import com.flyerzrule.mc.guardutils.commands.ScoreboardToggleCommand;
import com.flyerzrule.mc.guardutils.commands.SwordCommand;
import com.flyerzrule.mc.guardutils.commands.tabcomplete.KOSTabComplete;
import com.flyerzrule.mc.guardutils.commands.tabcomplete.OnOffTabComplete;
import com.flyerzrule.mc.guardutils.commands.tabcomplete.OtherContrabandTabComplete;
import com.flyerzrule.mc.guardutils.commands.tabcomplete.PlayerTabComplete;
import com.flyerzrule.mc.guardutils.invis.InvisPlayers;
import com.flyerzrule.mc.guardutils.listeners.DroppedItemListener;
import com.flyerzrule.mc.guardutils.listeners.InvisibilityListener;
import com.flyerzrule.mc.guardutils.listeners.PlayerDeathListener;
import com.flyerzrule.mc.guardutils.listeners.PlayerHitListener;
import com.flyerzrule.mc.guardutils.protocol.EnitityEquipmentListener;

import co.killionrevival.killioncommons.KillionUtilities;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class GuardUtils extends JavaPlugin {
    private final String pluginName = "GuardUtils";
    private static Plugin plugin;
    private static KillionUtilities killionUtilities;
    private static ConsoleUtil logger;
    private static LuckPerms luckperms;
    private static ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        plugin = this;
        killionUtilities = new KillionUtilities(this);
        logger = killionUtilities.getConsoleUtil();

        luckperms = LuckPermsProvider.get();

        protocolManager = ProtocolLibrary.getProtocolManager();
        registerProtocolListeners();

        registerCommands();
        registerTabComplete();
        registerListeners();

        InvisPlayers.getInstance();
        ArmorStandManager.getInstance().createArmorStandTask();

        logger.sendSuccess(this.pluginName + " has been enabled.");
    }

    @Override
    public void onDisable() {
        ArmorStandManager.getInstance().removeAllArmorStands();
        logger.sendSuccess(this.pluginName + " has been disabled.");
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static ConsoleUtil getMyLogger() {
        return logger;
    }

    public static LuckPerms getLuckperms() {
        return luckperms;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    private void registerCommands() {
        getCommand("sword").setExecutor(new SwordCommand());
        getCommand("bow").setExecutor(new BowCommand());
        getCommand("cb").setExecutor(new OtherContrabandCommand());
        getCommand("kos").setExecutor(new KOSCommand());
        getCommand("guardsb").setExecutor(new ScoreboardToggleCommand());
        getCommand("guardsinvistag").setExecutor(new GuardInvisTagCommand());

        GuardUtils.logger.sendSuccess("Commands have been registered.");
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

        GuardUtils.logger.sendSuccess("Tab completers have been registered.");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new DroppedItemListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerHitListener(), this);
        getServer().getPluginManager().registerEvents(new InvisibilityListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);

        GuardUtils.logger.sendSuccess("Listeners have been registered.");
    }

    private void registerProtocolListeners() {
        protocolManager.addPacketListener(new EnitityEquipmentListener());

        GuardUtils.logger.sendSuccess("Protocol listeners have been registered.");
    }
}