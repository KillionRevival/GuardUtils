package com.flyerzrule.mc.guardutils.armor.listeners;

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
import com.flyerzrule.mc.guardutils.armor.utils.ArmorUtil;

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

    int entityId = wrapper.getEntity();
    Entity entity = event.getPlayer().getWorld().getEntities().stream().filter(e -> e.getEntityId() == entityId)
        .findFirst().orElse(null);

    if (!(entity instanceof Player)) {
      return; // If the entity is not a player, ignore
    }

    Player player = (Player) entity;

    if (ArmorUtil.isPlayerGuard(player)) {
      List<Pair<ItemSlot, ItemStack>> modifiedSlots = new ArrayList<>();
      for (Pair<ItemSlot, ItemStack> slot : wrapper.getSlots()) {
        ItemSlot itemSlot = slot.getFirst();
        ItemStack item = slot.getSecond();

        if (item != null && ArmorUtil.isArmor(item.getType())) {
          GuardUtils.getMyLogger().sendDebug(String.format("%s is wearing %s in slot %s", player.getName(),
              item.getType().name(), itemSlot.name()));

          Material chainmailType = ArmorUtil.getChainmailType(item.getType());
          ItemStack chainmailItem = new ItemStack(chainmailType);
          ArmorUtil.copyEnchants(item, chainmailItem); // Copy enchantments for appearance consistency

          modifiedSlots.add(new Pair<>(itemSlot, chainmailItem));

        } else {
          modifiedSlots.add(slot);
        }
      }

      wrapper.setSlots(modifiedSlots);
      event.setPacket(packet);
      GuardUtils.getMyLogger().sendDebug(String.format("Updated appearance of armor for %s", player.getName()));
    }
  }
}
