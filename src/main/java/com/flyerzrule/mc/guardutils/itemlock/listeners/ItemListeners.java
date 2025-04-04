package com.flyerzrule.mc.guardutils.itemlock.listeners;

import com.flyerzrule.mc.guardutils.GuardUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
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
        if (isItemTagged(item, player)) {
            event.setCancelled(true);
            player.getWorld().dropItemNaturally(player.getLocation(), item.clone());
            event.setCurrentItem(null);
            GuardUtils.getMyLogger().sendDebug("Item clicked is a guard item");
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();
        if (isItemTagged(item, player)) {
            event.setCancelled(true);
            GuardUtils.getMyLogger().sendDebug("Item picked up is a guard item");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (isItemTagged(item, player)) {
            event.setCancelled(true);

            player.getWorld().dropItemNaturally(player.getLocation(), item.clone());

            if (event.getHand() == EquipmentSlot.HAND) {
                player.getInventory().setItemInMainHand(null);
            } else if (event.getHand() == EquipmentSlot.OFF_HAND) {
                player.getInventory().setItemInOffHand(null);
            }
            GuardUtils.getMyLogger().sendDebug("Item interacted with is a guard item");
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack item = event.getCursor();
        if (isItemTagged(item, player)) {
            event.setCancelled(true);

            player.getWorld().dropItemNaturally(player.getLocation(), item.clone());
            player.setItemOnCursor(null);
            GuardUtils.getMyLogger().sendDebug("Cursor dragged is a guard item");
        }
    }

    // For arrows that are fired by the guard and stuck into the ground
    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();

        if (isItemTagged(item, player)) {
            event.setCancelled(true);
            GuardUtils.getMyLogger().sendDebug("Arrow picked up is a guard item");
        }
    }

    // For Dispensers putting armor onto player
    @EventHandler
    public void onBlockDispenseArmor(BlockDispenseArmorEvent event) {
        ItemStack item = event.getItem();

        if (isItemTagged(item)) {
            event.setCancelled(true);
            GuardUtils.getMyLogger().sendDebug("Dispensed item is a guard item");
        }
    }

    // For Hoppers
    @EventHandler
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();

        if (isItemTagged(item)) {
            event.setCancelled(true);
            GuardUtils.getMyLogger().sendDebug("Inventory picked up item is a guard item");
        }
    }

    private boolean isItemTagged(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();

        NamespacedKey key = new NamespacedKey("guardutils", "guard_item");
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.has(key, PersistentDataType.STRING);
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
