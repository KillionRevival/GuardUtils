package com.flyerzrule.mc.guardutils.utils;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;

public class Permissions {
  public static boolean hasPermission(Player player, String permission) {
    return player.hasPermission(permission);
  }

  public static void enablePermission(Player player, String permission) {
    setNode(player, permission, true);
  }

  public static void disablePermission(Player player, String permission) {
    setNode(player, permission, false);
  }

  private static void setNode(Player player, String permission, boolean value) {
    LuckPerms luckPerms = GuardUtils.getLuckperms();
    Node newNode = Node.builder(permission).value(value).build();

    luckPerms.getUserManager().loadUser(player.getUniqueId()).thenAccept(user -> {
      Node node = Node.builder(permission).build();
      if (user.data().contains(node, NodeEqualityPredicate.ONLY_KEY).asBoolean()) {
        user.data().remove(node);
      }
      user.data().add(newNode);

      // Save changes to LuckPerms
      luckPerms.getUserManager().saveUser(user);
    });
  }
}
