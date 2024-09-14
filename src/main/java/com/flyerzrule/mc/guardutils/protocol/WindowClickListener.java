package com.flyerzrule.mc.guardutils.protocol;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.comphenix.packetwrapper.wrappers.play.serverbound.WrapperPlayClientWindowClick;
import com.comphenix.packetwrapper.wrappers.play.serverbound.WrapperPlayClientWindowClick.WrappedClickType;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.Pair;
import com.flyerzrule.mc.guardutils.GuardUtils;
import com.flyerzrule.mc.guardutils.protocol.utils.ArmorUtil;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public class WindowClickListener extends PacketAdapter {
    public WindowClickListener() {
        super(GuardUtils.getPlugin(), PacketType.Play.Client.WINDOW_CLICK);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        GuardUtils.getMyLogger().sendDebug("Packet received: " + event.getPacket().getType().name());

        PacketContainer packet = event.getPacket().deepClone();
        event.setPacket(packet);

        WrapperPlayClientWindowClick wrapper = new WrapperPlayClientWindowClick(packet);

        GuardUtils.getMyLogger().sendDebug("Click Type: " + wrapper.getClickType().name());
        WrappedClickType clickType = wrapper.getClickType();

        if (clickType.equals(WrappedClickType.QUICK_MOVE)) {
            Pair<Integer, Material> movedArmor = getArmorMoved(wrapper.getChangedSlots());
            if (movedArmor != null) {
                GuardUtils.getMyLogger().sendDebug("hi again");
                // Item is diamond armor and was shift clicked to an armor slot
                Material chainmailType = ArmorUtil.getChainmailType(movedArmor.getSecond());
                ItemStack chainmailItem = new ItemStack(chainmailType);

                wrapper.setCarriedItem(chainmailItem);
                this.setArmorMoved(wrapper, 0, chainmailItem);
            }
        }

    }

    private Pair<Integer, Material> getArmorMoved(Int2ObjectMap<ItemStack> input) {
        if (input == null || input.size() < 2) {
            return null;
        }

        Int2ObjectMap.Entry<ItemStack> firstEntry = getEntryByIndex(input, 0);
        Int2ObjectMap.Entry<ItemStack> secondEntry = getEntryByIndex(input, 1);

        int firstSlot = firstEntry.getIntKey();
        int secondSlot = secondEntry.getIntKey();

        ItemStack firstItem = firstEntry.getValue();
        ItemStack secondItem = secondEntry.getValue();

        if (ArmorUtil.isArmorSlot(firstSlot) && ArmorUtil.isGuardArmor(firstItem.getType())) {
            return new Pair<Integer, Material>(firstSlot, firstItem.getType());
        }

        if (ArmorUtil.isArmorSlot(secondSlot) && ArmorUtil.isGuardArmor(secondItem.getType())) {
            return new Pair<Integer, Material>(secondSlot, secondItem.getType());
        }

        return null;
    }

    private Int2ObjectMap.Entry<ItemStack> getEntryByIndex(Int2ObjectMap<ItemStack> map, int index) {
        int i = 0;
        for (Int2ObjectMap.Entry<ItemStack> entry : map.int2ObjectEntrySet()) {
            if (i == index) {
                return entry;
            }
            i++;
        }
        return null;
    }

    private void setArmorMoved(WrapperPlayClientWindowClick wrapper, int slot, ItemStack item) {
        Int2ObjectMap<ItemStack> changedSlots = wrapper.getChangedSlots();

        if (changedSlots != null) {
            changedSlots.put(slot, item);
            wrapper.setChangedSlots(changedSlots);
        }

    }

}
