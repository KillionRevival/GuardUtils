package com.flyerzrule.mc.guardutils;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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
}