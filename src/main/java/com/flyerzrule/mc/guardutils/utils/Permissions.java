package com.flyerzrule.mc.guardutils.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.guardutils.GuardUtils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;

public class Permissions {
  public static final String ADMIN = "guardutils.admin";
  public static final String CAN_BE_GUARD = "guardutils.canBeGuard";
  public static final String PLAYER = "guardutils.player";
  public static final String GUARD = "guardutils.guard";

  private static final LuckPerms luckPerms = GuardUtils.getLuckperms();

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

  public static void removeGroup(Player player, String groupName) {
    GuardUtils.getMyLogger().sendDebug(String.format("Removing group %s from player %s", groupName, player.getName()));

    User user = getUser(player);

    Node removeNode = InheritanceNode.builder(groupName).build();
    if (user.data().contains(removeNode, NodeEqualityPredicate.EXACT).asBoolean()) {
      user.data().remove(removeNode);
      GuardUtils.getMyLogger().sendDebug("Removed group '" + groupName + "' from " + player.getName());
    } else {
      GuardUtils.getMyLogger().sendDebug(player.getName() + " is not in group '" + groupName + "'");
    }

    luckPerms.getUserManager().saveUser(user);
  }

  public static void addGroup(Player player, String groupName) {
    GuardUtils.getMyLogger().sendDebug(String.format("Adding group %s to player %s", groupName, player.getName()));

    User user = getUser(player);

    Node addNode = InheritanceNode.builder(groupName).build();
    if (!user.data().contains(addNode, NodeEqualityPredicate.EXACT).asBoolean()) {
      user.data().add(addNode);
      GuardUtils.getMyLogger().sendDebug("Added group '" + groupName + "' to " + player.getName());
    } else {
      GuardUtils.getMyLogger().sendDebug(player.getName() + " is already in group '" + groupName + "'");
    }

    luckPerms.getUserManager().saveUser(user);
  }

  public static void replaceGroup(Player player, String oldGroupName, String newGroupName) {
    removeGroup(player, oldGroupName);
    addGroup(player, newGroupName);
  }

  public static List<String> getGroups(Player player) {
    User user = getUser(player);

    return user.data().toCollection().stream()
        .filter(node -> node instanceof InheritanceNode)
        .map(node -> ((InheritanceNode) node).getGroupName())
        .collect(Collectors.toList());
  }

  private static User getUser(Player player) {
    return luckPerms.getUserManager().loadUser(player.getUniqueId()).join();
  }
}
