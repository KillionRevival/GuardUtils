package com.flyerzrule.mc.guardutils.requests.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.flyerzrule.mc.guardutils.requests.Requests;
import com.flyerzrule.mc.guardutils.requests.models.ContrabandType;
import com.flyerzrule.mc.guardutils.requests.models.Item;
import com.flyerzrule.mc.guardutils.requests.models.Request;
import com.flyerzrule.mc.guardutils.requests.utils.Utils;
import com.flyerzrule.mc.guardutils.utils.Message;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class DroppedItemListener implements Listener {
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        Requests requests = Requests.getInstance();

        if (requests.isRequested(player)) {
            Request request = requests.getRequested(player);
            List<Item> contrabandItems = Utils.getContrandTypeItems(request.getType());
            ItemStack droppedItem = event.getItemDrop().getItemStack();

            boolean isContrabandItem = false;
            if (request.getType().equals(ContrabandType.OTHER)) {
                Item otherContrabandItem = request.getOtherContrabandItem();
                if (droppedItem.getType().equals(otherContrabandItem.getMaterial()) && otherContrabandItem.isPotion()) {
                    if (droppedItem.getItemMeta() instanceof PotionMeta) {
                        PotionMeta meta = (PotionMeta) droppedItem.getItemMeta();
                        for (PotionEffect effect : meta.getBasePotionType().getPotionEffects()) {
                            PotionEffectType effectType = effect.getType();
                            if (effectType.getCategory() == otherContrabandItem.getPotionEffectTypeCategory()) {
                                isContrabandItem = true;
                            }
                        }
                    }
                } else if (droppedItem.getType().equals(otherContrabandItem.getMaterial())) {
                    isContrabandItem = true;
                }
            } else {
                isContrabandItem = contrabandItems.stream()
                        .anyMatch(item -> item.getMaterial().equals(event.getItemDrop().getItemStack().getType()));
            }

            if (isContrabandItem) {
                String contrabandItemName = "";
                if (request.getType().equals(ContrabandType.OTHER)) {
                    contrabandItemName = request.getOtherContrabandItem().getName();
                } else if (request.getType().equals(ContrabandType.SWORD)) {
                    contrabandItemName = "SWORD";
                } else if (request.getType().equals(ContrabandType.BOW)) {
                    contrabandItemName = "BOW";
                }

                TextComponent playerMessage = Message.formatMessage(NamedTextColor.GREEN,
                        String.format("You have dropped your %s!", contrabandItemName));
                player.sendMessage(playerMessage);

                TextComponent guardMessage = Message.formatMessage(NamedTextColor.GREEN, String
                        .format("%s has dropped their %s!", player.getName(), contrabandItemName));
                request.getGuard().sendMessage(guardMessage);

                requests.cancelRequest(player);
            }
        }
    }
}
