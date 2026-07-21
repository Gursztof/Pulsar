package com.gursztof.pulsar.chat;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum ChatPrefix {
    PULSAR(Formatting.LIGHT_PURPLE, "Pulsar"),
    WARNING(Formatting.RED, "Warning"),
    DEBUG(Formatting.GOLD, "DEBUG");

    private final Formatting color;
    private final String content;

    ChatPrefix(Formatting color, String content) {
        this.color = color;
        this.content = content;
    }

    public MutableText getPrefix() {
        return Text.literal("[")
            .append(Text.literal(content).formatted(color))
            .append(Text.literal("] "));
    }
}
