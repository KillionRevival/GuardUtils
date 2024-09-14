package com.flyerzrule.mc.guardutils.protocol;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.comphenix.packetwrapper.wrappers.play.clientbound.WrapperPlayServerEntityEquipment;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.protocol.utils.ArmorUtil;

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

        ItemStack item = wrapper.getSlots().getFirst().getSecond();

        if (item != null && ArmorUtil.isGuardArmor(item.getType())) {
            Material chainmailType = ArmorUtil.getChainmailType(item.getType());
            ItemStack chainmailItem = new ItemStack(chainmailType);
            List<Pair<ItemSlot, ItemStack>> pairs = new ArrayList<>();
            pairs.add(new Pair<ItemSlot, ItemStack>(wrapper.getSlots().getFirst().getFirst(), chainmailItem));
            wrapper.setSlots(pairs);
            GuardUtils.getMyLogger().sendDebug("was set");
        } else {
            GuardUtils.getMyLogger().sendDebug("NOT SET");
            if (item != null) {
                GuardUtils.getMyLogger().sendDebug(item.getType().name());
            }
        }
    }
}
