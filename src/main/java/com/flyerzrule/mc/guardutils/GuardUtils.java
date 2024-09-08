package com.flyerzrule.mc.guardutils;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.flyerzrule.mc.guardutils.commands.BowCommand;
import com.flyerzrule.mc.guardutils.commands.KOSCommand;
import com.flyerzrule.mc.guardutils.commands.OtherContrabandCommand;
import com.flyerzrule.mc.guardutils.commands.SwordCommand;
import com.flyerzrule.mc.guardutils.commands.tabcomplete.KOSTabComplete;
import com.flyerzrule.mc.guardutils.commands.tabcomplete.OtherContrabandTabComplete;
import com.flyerzrule.mc.guardutils.commands.tabcomplete.PlayerTabComplete;
import com.flyerzrule.mc.guardutils.listeners.DroppedItemListener;
import com.flyerzrule.mc.guardutils.listeners.InvisibilityListener;
import com.flyerzrule.mc.guardutils.listeners.PlayerHitListener;

import co.killionrevival.killioncommons.KillionUtilities;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;

public class GuardUtils extends JavaPlugin {
    private final String pluginName = "GuardUtils";
    private static Plugin plugin;
    private static KillionUtilities killionUtilities;
    private static ConsoleUtil logger;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        plugin = this;
        killionUtilities = new KillionUtilities(this);
        logger = killionUtilities.getConsoleUtil();

        registerCommands();
        registerTabComplete();
        registerListeners();

        logger.sendSuccess(this.pluginName + " has been enabled.");
    }

    @Override
    public void onDisable() {
        logger.sendSuccess(this.pluginName + " has been disabled.");
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static ConsoleUtil getMyLogger() {
        return logger;
    }

    private void registerCommands() {
        getCommand("sword").setExecutor(new SwordCommand());
        getCommand("bow").setExecutor(new BowCommand());
        getCommand("cb").setExecutor(new OtherContrabandCommand());
        getCommand("kos").setExecutor(new KOSCommand());

        GuardUtils.logger.sendSuccess("Commands have been registered.");
    }

    private void registerTabComplete() {
        PlayerTabComplete playerTabComplete = new PlayerTabComplete();
        KOSTabComplete kosTabComplete = new KOSTabComplete();
        OtherContrabandTabComplete otherContrabandTabComplete = new OtherContrabandTabComplete();

        getCommand("sword").setTabCompleter(playerTabComplete);
        getCommand("bow").setTabCompleter(playerTabComplete);
        getCommand("cb").setTabCompleter(otherContrabandTabComplete);
        getCommand("kos").setTabCompleter(kosTabComplete);

        GuardUtils.logger.sendSuccess("Tab completers have been registered.");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new DroppedItemListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerHitListener(), this);
        getServer().getPluginManager().registerEvents(new InvisibilityListener(), this);

        GuardUtils.logger.sendSuccess("Listeners have been registered.");
    }
}