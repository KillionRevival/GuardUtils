package com.flyerzrule.mc.guardutils.protocol;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
        int entityId = wrapper.getEntity();
        Entity entity = event.getPlayer().getWorld().getEntities().stream().filter(e -> e.getEntityId() == entityId)
                .findFirst().get();

        Player player = null;
        if (entity != null && entity instanceof Player) {
            player = (Player) entity;
        }

        if (player != null && ArmorUtil.isPlayerGuard(player) && item != null
                && ArmorUtil.isArmor(item.getType())) {
            Material chainmailType = ArmorUtil.getChainmailType(item.getType());
            ItemStack chainmailItem = new ItemStack(chainmailType);
            ArmorUtil.copyEnchants(item, chainmailItem);
            List<Pair<ItemSlot, ItemStack>> pairs = new ArrayList<>();
            pairs.add(new Pair<ItemSlot, ItemStack>(wrapper.getSlots().getFirst().getFirst(), chainmailItem));
            wrapper.setSlots(pairs);
            GuardUtils.getMyLogger().sendDebug(String.format("Changing appearance of armor on %s for player %s",
                    player.getName(), event.getPlayer().getName()));
        }
    }
}
