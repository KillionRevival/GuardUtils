package com.flyerzrule.mc.guardutils.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.flyerzrule.mc.guardutils.GuardUtils;

public class GuardArmorListener extends PacketAdapter {
    public GuardArmorListener() {
        super(GuardUtils.getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        // PacketContainer packet = event.getPacket();

        // ItemStack stack = packet.getItemModifier().read(0);

        // if (stack != null && this.isDiamondArmor(stack.getType())) {

        //     // To fix this, we'll simply clone the packet before we modify it
        //     packet = event.getPacket().deepClone();
        //     event.setPacket(packet);
        //     stack = packet.getItemModifier().read(0);

        //     Material chainmailType = getChainmailType(stack.getType());
        //     ItemStack chainmailItem = new ItemStack(chainmailType);

        //     packet.getItemModifier().write(0, chainmailItem);
        // }

        // Retrieve the list of equipment (slot and item pairs)
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipmentList = event.getPacket().getSlotStackPairLists().read(0);

        // Iterate through the equipment list and replace diamond armor with chainmail visuals
        for (Pair<EnumWrappers.ItemSlot, ItemStack> equipment : equipmentList) {
            ItemStack itemStack = equipment.getSecond();

            // Check if the current item is diamond armor
            if (itemStack != null && isDiamondArmor(itemStack.getType())) {
                // Replace the diamond armor with chainmail armor for visuals
                Material chainmailType = getChainmailType(itemStack.getType());
                ItemStack chainmailItem = new ItemStack(chainmailType);

                // Update the packet to use chainmail armor instead
                equipment.setSecond(chainmailItem);
            }
        }
    }

    private boolean isDiamondArmor(Material material) {
        return material == Material.DIAMOND_HELMET ||
                material == Material.DIAMOND_CHESTPLATE ||
                material == Material.DIAMOND_LEGGINGS ||
                material == Material.DIAMOND_BOOTS;
    }

    private Material getChainmailType(Material diamondType) {
        switch (diamondType) {
            case DIAMOND_HELMET:
                return Material.CHAINMAIL_HELMET;
            case DIAMOND_CHESTPLATE:
                return Material.CHAINMAIL_CHESTPLATE;
            case DIAMOND_LEGGINGS:
                return Material.CHAINMAIL_LEGGINGS;
            case DIAMOND_BOOTS:
                return Material.CHAINMAIL_BOOTS;
            default:
                return Material.CHAINMAIL_HELMET;
        }
    }
}
