package com.flyerzrule.mc.guardutils.protocol.wrappers;

/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;

public class WrapperPlayServerEntityEquipment extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_EQUIPMENT;

    public WrapperPlayServerEntityEquipment() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityEquipment(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     * 
     * @return The current Entity ID
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Entity ID.
     * 
     * @param value - new value.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     * 
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     * 
     * @param event - the packet event.
     * @return The spawned entity.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    public ItemSlot getSlot() {
        return handle.getSlotStackPairLists().read(0).getFirst().getFirst();
    }

    public void setSlot(ItemSlot value) {
        ItemStack currentItem = this.getItem();
        List<Pair<ItemSlot, ItemStack>> values = new ArrayList<>();
        values.add(new Pair<ItemSlot, ItemStack>(value, currentItem));
        handle.getSlotStackPairLists().write(0, values);
    }

    /**
     * Retrieve Item.
     * <p>
     * Notes: item in slot format
     * 
     * @return The current Item
     */
    public ItemStack getItem() {
        return handle.getSlotStackPairLists().read(0).getFirst().getSecond();
    }

    /**
     * Set Item.
     * 
     * @param value - new value.
     */
    public void setItem(ItemStack value) {
        ItemSlot currentSlot = this.getSlot();
        List<Pair<ItemSlot, ItemStack>> values = new ArrayList<>();
        values.add(new Pair<ItemSlot, ItemStack>(currentSlot, value));
        handle.getSlotStackPairLists().write(0, values);
    }

    public Pair<ItemSlot, ItemStack> getSlotStackPair() {
        return handle.getSlotStackPairLists().read(0).getFirst();
    }

    public void setSlotStackPair(Pair<ItemSlot, ItemStack> value) {
        List<Pair<ItemSlot, ItemStack>> values = new ArrayList<>();
        values.add(value);
        handle.getSlotStackPairLists().write(0, values);
    }
}
