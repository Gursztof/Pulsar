package com.gursztof.pulsar.chat;

import com.gursztof.pulsar.settings.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ChatSender {
    public static void send(String content, ChatPrefix prefix) {
        if (prefix.equals(ChatPrefix.DEBUG) && !Settings.debug) {
            return;
        }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        player.sendMessage(prefix.getPrefix().append(content), false);
    }
}
