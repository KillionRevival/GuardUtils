package com.flyerzrule.mc.guardutils.protocol;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.protocol.utils.ArmorUtil;
import com.flyerzrule.mc.guardutils.protocol.wrappers.WrapperPlayServerEntityEquipment;

public class EnitityEquipmentListener extends PacketAdapter {
    public EnitityEquipmentListener() {
        super(GuardUtils.getPlugin(), PacketType.Play.Server.ENTITY_EQUIPMENT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        GuardUtils.getMyLogger().sendDebug("Packet sent: " + event.getPacket().getType().name());

        PacketContainer packet = event.getPacket().deepClone();
        event.setPacket(packet);

        WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment(packet);

        ItemStack item = wrapper.getItem();

        if (item != null && ArmorUtil.isDiamondArmor(item.getType())) {
            Material chainmailType = ArmorUtil.getChainmailType(item.getType());
            ItemStack chainmailItem = new ItemStack(chainmailType);
            wrapper.setItem(chainmailItem);
        }
    }
}
