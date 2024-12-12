package com.flyerzrule.mc.guardutils.common.gui.panels.helper;

import org.bukkit.entity.Player;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

public abstract class Panel {
  protected final Player player;
  protected final String title;

  protected Gui gui;
  protected Window window;

  public Panel(Player player, String title) {
    this.player = player;
    this.title = title;
  }

  public void open() {
    this.window.open();
  }

  public void close() {
    this.window.close();
  }
}
