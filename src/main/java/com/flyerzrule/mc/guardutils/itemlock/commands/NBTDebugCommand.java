package com.flyerzrule.mc.guardutils.itemlock.commands;

import com.flyerzrule.mc.guardutils.utils.Permissions;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Permission;


public class NBTDebugCommand {
    public NBTDebugCommand() {
    }

    @Permission(Permissions.ADMIN)
    @Command("nbtdebug")
    @CommandDescription("Shows persistent NBT data on the item in your main hand")
    public void onCommand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            player.sendMessage(Component.text("You're not holding an item."));
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage(Component.text("Item has no meta."));
            return;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.getKeys().isEmpty()) {
            player.sendMessage(Component.text("No persistent NBT data found."));
            return;
        }

        player.sendMessage(Component.text("NBT Keys:"));
        for (NamespacedKey key : container.getKeys()) {
            String val = container.get(key, PersistentDataType.STRING);
            player.sendMessage(Component.text("- " + key.toString() + " = " + val));
        }
    }
}
