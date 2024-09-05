package co.killionrevival.REPLACE_ME;

import org.bukkit.plugin.java.JavaPlugin;

import co.killionrevival.killioncommons.KillionCommonsApi;

public class Plugin extends JavaPlugin {
    private final String pluginName = "REPLACE_ME";
    private static JavaPlugin instance;
    private static KillionCommonsApi api;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        instance = this;
        api = new KillionCommonsApi(this);
        api.getConsoleUtil().sendSuccess(this.pluginName + " has been enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        api.getConsoleUtil().sendSuccess(this.pluginName + " has been disabled.");
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}