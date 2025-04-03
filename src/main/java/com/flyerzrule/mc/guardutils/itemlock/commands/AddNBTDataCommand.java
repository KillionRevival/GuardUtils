package com.flyerzrule.mc.guardutils.itemlock.commands;

import com.flyerzrule.mc.guardutils.GuardUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.bukkit.entity.Player;

import java.util.List;

public class AddNBTDataCommand {
    public AddNBTDataCommand() {
    }

    @Command("addguardnbt <target>")
    @CommandDescription("Adds guard nbt data to items in player's inventory")
    public void onCommand(CommandSender sender, @Argument("target") Player target) {
        if ((sender instanceof Player player && !player.hasPermission("guardutils.admin")) || !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(Component.text("You do not have permission to use this command!", NamedTextColor.RED));
            return;
        }
        GuardUtils.getMyLogger().sendDebug("Adding guard nbt data to items in player's inventory");

        int numberChanged = 0;
        for (ItemStack item : target.getInventory().getContents()) {
            if (item == null) continue;

            addNBTData(item);
            numberChanged++;
        }
        GuardUtils.getMyLogger().sendDebug(String.format("Changed %d items", numberChanged));
    }

    private void addNBTData(ItemStack itemStack) {
        if (isGuardItem(itemStack)) {
            GuardUtils.getMyLogger().sendDebug("Item is a guard item");
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta == null) return;

            itemMeta.getPersistentDataContainer().set(new NamespacedKey("guardutils", "guard_item"), PersistentDataType.STRING, "guardutils.guard");
            itemStack.setItemMeta(itemMeta);
        }
    }

    private boolean isGuardItem(ItemStack itemStack) {
        if (itemStack.getItemMeta().hasLore()) {
            List<Component> lore = itemStack.getItemMeta().lore();
            if (lore == null) return false;
            for (Component component : lore) {
                GuardUtils.getMyLogger().sendDebug(String.format("Lore line: %s", PlainTextComponentSerializer.plainText().serialize(component)));
                if (PlainTextComponentSerializer.plainText().serialize(component).equals("Guard Item")) {
                    return true;
                }
            }
        }
        return false;
    }
}
