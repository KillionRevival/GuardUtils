package com.flyerzrule.mc.guardutils;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.exception.NoPermissionException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.setting.ManagerSetting;

import com.flyerzrule.mc.guardutils.armor.listeners.EnitityEquipmentListener;
import com.flyerzrule.mc.guardutils.armorstands.ArmorStandManager;
import com.flyerzrule.mc.guardutils.database.GuardStatsDao;
import com.flyerzrule.mc.guardutils.database.SavedPlayerInfoDao;
import com.flyerzrule.mc.guardutils.database.SettingsDao;
import com.flyerzrule.mc.guardutils.database.UserSettingsDao;
import com.flyerzrule.mc.guardutils.duty.commands.GuardCommand;
import com.flyerzrule.mc.guardutils.duty.listeners.GuardKillDeathListener;
import com.flyerzrule.mc.guardutils.duty.listeners.SignCommandListener;
import com.flyerzrule.mc.guardutils.duty.listeners.SignCreationListener;
import com.flyerzrule.mc.guardutils.invis.InvisPlayers;
import com.flyerzrule.mc.guardutils.invis.listeners.InvisibilityListener;
import com.flyerzrule.mc.guardutils.kos.commands.KOSCommand;
import com.flyerzrule.mc.guardutils.kos.listeners.PlayerDeathListener;
import com.flyerzrule.mc.guardutils.requests.commands.BowCommand;
import com.flyerzrule.mc.guardutils.requests.commands.OtherContrabandCommand;
import com.flyerzrule.mc.guardutils.requests.commands.SwordCommand;
import com.flyerzrule.mc.guardutils.requests.listeners.DroppedItemListener;
import com.flyerzrule.mc.guardutils.scoreboard.GuardHitsScoreboard;
import com.flyerzrule.mc.guardutils.scoreboard.listeners.PlayerHitListener;

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.KillionUtilities;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import xyz.xenondevs.invui.InvUI;
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
  @Getter
  private static PaperCommandManager<Source> commandManager;
  @Getter
  private static AnnotationParser<Source> annotationParser;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    plugin = this;
    killionUtilities = new KillionUtilities(this);
    myLogger = killionUtilities.getConsoleUtil();

    InvUI.getInstance().setPlugin(this);

    initDaos();

    luckperms = LuckPermsProvider.get();

    protocolManager = ProtocolLibrary.getProtocolManager();

    simpleClans = (SimpleClans) Objects.requireNonNull(getServer().getPluginManager().getPlugin("SimpleClans"));

    KillionCommons.getInstance().getScoreboardManager().registerAddition(this, new GuardHitsScoreboard());

    registerProtocolListeners();

    registerCommands();
    registerListeners();
    registerGlobalIngredients();

    InvisPlayers.getInstance();
    ArmorStandManager.getInstance().createArmorStandTask();

    myLogger.sendSuccess(this.pluginName + " has been enabled.");
  }

  @Override
  public void onDisable() {
    ArmorStandManager.getInstance().removeAllArmorStands();
    Bukkit.getScheduler().cancelTasks(this);

    myLogger.sendSuccess(this.pluginName + " has been disabled.");
  }

  private void registerCommands() {
    commandManager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
        .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
        .buildOnEnable(this);

    commandManager.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
    commandManager.settings().set(ManagerSetting.OVERRIDE_EXISTING_COMMANDS, true);

    commandManager.parameterInjectorRegistry().registerInjector(
        TypeToken.get(Player.class),
        (context, annotationAccessor) -> {
          final Source sender = context.sender();

          if (sender instanceof PlayerSource playerSource) {
            return playerSource.source();
          }

          return null;
        });

    // Register exception handler for permissions
    commandManager.exceptionController().registerHandler(NoPermissionException.class,
        (context) -> {
          String commandName = context.context().rawInput().toString().split(" ")[0].replaceFirst("^/", "");
          CommandSender sender = context.context().sender().source();

          if (commandName.equals("guard")) {
            sender.sendMessage(
                "You don't have permission to use this command! Please contact an admin if you would like to become a guard!");
          } else {
            sender.sendMessage("You don't have permission to use this command!");
          }
        });
    commandManager.exceptionController().registerHandler(Exception.class, (context) -> {
      Bukkit.getLogger().severe("Command exception: " + context.exception().getMessage());
      context.context().sender().source().sendMessage("An error occurred while processing the command.");
    });

    annotationParser = new AnnotationParser<>(commandManager, Source.class, params -> SimpleCommandMeta.empty());
    annotationParser.parse(new GuardCommand());
    annotationParser.parse(new KOSCommand());
    annotationParser.parse(new SwordCommand());
    annotationParser.parse(new BowCommand());
    annotationParser.parse(new OtherContrabandCommand());

    getLogger().info("Command manager initialized!");
  }

  private void registerListeners() {
    getServer().getPluginManager().registerEvents(new DroppedItemListener(), this);
    getServer().getPluginManager().registerEvents(new PlayerHitListener(), this);
    getServer().getPluginManager().registerEvents(new InvisibilityListener(), this);
    getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
    getServer().getPluginManager().registerEvents(new GuardKillDeathListener(), this);
    getServer().getPluginManager().registerEvents(new SignCreationListener(), this);
    getServer().getPluginManager().registerEvents(new SignCommandListener(), this);

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

  private void initDaos() {
    GuardStatsDao.getInstance();
    SavedPlayerInfoDao.getInstance();
    SettingsDao.getInstance();
    UserSettingsDao.getInstance();
  }
}