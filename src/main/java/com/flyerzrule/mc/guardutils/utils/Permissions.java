package com.flyerzrule.mc.guardutils.utils;

import java.security.Guard;
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
      saveUser(user);
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

    saveUser(user);
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

    saveUser(user);
  }

  public static void replaceGroup(Player player, String oldGroupName, String newGroupName) {
    GuardUtils.getMyLogger().sendDebug(
        String.format("Replacing group %s with %s for player %s", oldGroupName, newGroupName, player.getName()));

    User user = getUser(player);

    Node removeNode = InheritanceNode.builder(oldGroupName).build();
    Node addNode = InheritanceNode.builder(newGroupName).build();

    if (!user.data().contains(addNode, NodeEqualityPredicate.EXACT).asBoolean()) {
      user.data().add(addNode);
      GuardUtils.getMyLogger().sendDebug("Added group '" + newGroupName + "' to " + player.getName());
    } else {
      GuardUtils.getMyLogger().sendDebug(player.getName() + " is already in group '" + newGroupName + "'");
    }

    if (user.data().contains(removeNode, NodeEqualityPredicate.EXACT).asBoolean()) {
      user.data().remove(removeNode);
      GuardUtils.getMyLogger().sendDebug("Removed group '" + oldGroupName + "' from " + player.getName());
    } else {
      GuardUtils.getMyLogger().sendDebug(player.getName() + " is not in group '" + oldGroupName + "'");
    }

    Node defaultNode = Node.builder("defaault").build();
    if (user.data().contains(defaultNode, NodeEqualityPredicate.EXACT).asBoolean()) {
      user.data().remove(defaultNode);
      GuardUtils.getMyLogger().sendDebug("Removed default group from " + player.getName());
    }

    saveUser(user);
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

  private static void saveUser(User user) {
    luckPerms.getUserManager().saveUser(user);
  }
}
