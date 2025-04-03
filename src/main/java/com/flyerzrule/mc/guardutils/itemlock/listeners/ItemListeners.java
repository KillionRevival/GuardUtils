package com.flyerzrule.mc.guardutils.itemlock.listeners;

import com.flyerzrule.mc.guardutils.GuardUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemListeners implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        GuardUtils.getMyLogger().sendDebug(String.format("Item clicked: %s", item.getType().name()));
        if (isItemTagged(item, player)) {
            event.setCancelled(true);
            player.getWorld().dropItemNaturally(player.getLocation(), item.clone());
            event.setCurrentItem(null);

            player.sendMessage(Component.text("This is a guard item! You are not a guard!", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();
        GuardUtils.getMyLogger().sendDebug(String.format("Item picked up: %s", item.getType().name()));
        if (isItemTagged(item, player)) {
            event.setCancelled(true);

            player.sendMessage(Component.text("This is a guard item! You are not a guard!", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item == null) return;
        GuardUtils.getMyLogger().sendDebug(String.format("Item interacted with: %s", item.getType().name()));
        if (isItemTagged(item, player)) {
            event.setCancelled(true);

            player.getWorld().dropItemNaturally(player.getLocation(), item.clone());

            if (event.getHand() == EquipmentSlot.HAND) {
                player.getInventory().setItemInMainHand(null);
            } else if (event.getHand() == EquipmentSlot.OFF_HAND) {
                player.getInventory().setItemInOffHand(null);
            }

            player.sendMessage(Component.text("This is a guard item! You are not a guard!", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack item = event.getCursor();
        if (item == null) return;
        GuardUtils.getMyLogger().sendDebug(String.format("Cursor dragged: %s", item.getType().name()));
        if (isItemTagged(item, player)) {
            event.setCancelled(true);

            player.getWorld().dropItemNaturally(player.getLocation(), item.clone());
            player.setItemOnCursor(null);

            player.sendMessage(Component.text("This is a guard item! You are not a guard!", NamedTextColor.RED));
        }
    }

    private boolean isItemTagged(ItemStack item, Player player) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();

        NamespacedKey key = new NamespacedKey("guardutils", "guard_item");
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(key, PersistentDataType.STRING)) {
            String requiredPermission = container.get(key, PersistentDataType.STRING);
            if (requiredPermission != null && !player.hasPermission(requiredPermission)) {
                return true;
            }
        }
        return false;
    }
}
