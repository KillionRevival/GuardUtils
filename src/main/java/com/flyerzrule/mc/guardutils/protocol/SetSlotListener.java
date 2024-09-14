package com.flyerzrule.mc.guardutils.protocol;

import java.security.Guard;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftMetaArmor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.protocol.utils.ArmorUtil;
import com.flyerzrule.mc.guardutils.protocol.wrappers.WrapperPlayServerSetSlot;

public class SetSlotListener extends PacketAdapter {
    public SetSlotListener() {
        super(GuardUtils.getPlugin(), PacketType.Play.Server.SET_SLOT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        GuardUtils.getMyLogger().sendDebug("Packet sent: " + event.getPacket().getType().name());
        PacketContainer packet = event.getPacket().deepClone();
        event.setPacket(packet);

        WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(packet);

        ItemStack itemStack = wrapper.getSlotData();
        int slot = wrapper.getSlot();

        ItemMeta meta = itemStack.getItemMeta();

        if (itemStack != null && ArmorUtil.isDiamondArmor(itemStack.getType()) && ArmorUtil.isArmorSlot(slot)
                && meta != null && meta instanceof CraftMetaArmor) {

            CraftMetaArmor armorMeta = (CraftMetaArmor) meta;
            int armorDamage = 0;
            if (armorMeta.hasDamage()) {
                armorDamage = armorMeta.getDamage();
            }

            Material chainmailType = ArmorUtil.getChainmailType(itemStack.getType());
            ItemStack chainmailItem = new ItemStack(chainmailType);
            ItemMeta chainmailMeta = chainmailItem.getItemMeta();
            if (chainmailMeta instanceof CraftMetaArmor) {
                CraftMetaArmor chainmailArmorMeta = (CraftMetaArmor) chainmailMeta;
                if (armorDamage != 0) {
                    chainmailArmorMeta.setDamage(armorDamage);
                }
            }
            chainmailItem.setItemMeta(chainmailMeta);

            wrapper.setSlotData(chainmailItem);
        }

    }
}
