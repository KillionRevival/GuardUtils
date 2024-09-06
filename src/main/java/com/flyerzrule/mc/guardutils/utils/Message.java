package com.flyerzrule.mc.guardutils.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class Message {
    public static TextComponent formatMessage(NamedTextColor color, String message) {
        return Component.text().color(color).content(message).build();
    }
}
