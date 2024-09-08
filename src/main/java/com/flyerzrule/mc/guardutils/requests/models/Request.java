package com.flyerzrule.mc.guardutils.requests.models;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import lombok.ToString;
import lombok.Getter;

@Getter
@ToString
public class Request {
    private final Player guard;
    private final ContrabandType type;
    private final Item contrabandItem;
    private final BukkitTask countTask;
    private final BukkitTask cancelTask;

    public Request(Player guard, ContrabandType type, Item contrabandItem, BukkitTask countTask,
            BukkitTask cancelTask) {
        this.guard = guard;
        this.type = type;
        this.contrabandItem = contrabandItem;
        this.countTask = countTask;
        this.cancelTask = cancelTask;
    }
}
