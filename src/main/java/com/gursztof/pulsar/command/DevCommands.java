package com.gursztof.pulsar.command;

import com.gursztof.pulsar.chat.ChatPrefix;
import com.gursztof.pulsar.chat.ChatSender;
import com.gursztof.pulsar.settings.Settings;
import com.gursztof.pulsar.settings.SettingsFileManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class DevCommands {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("pulsar")
                    .then(ClientCommandManager.argument("value", StringArgumentType.string())
                            .executes(context -> {
                                String value = StringArgumentType.getString(context, "value");
                                return executeCommandWithArg(value);
                            })
                    )
            );
        });
    }

    private static int executeCommandWithArg(String context) {
        return switch (context) {
            case "debug" -> debugSwitch();
            // TODO settings for debug messages
            case "debugSettings" -> 1;
            default -> 0;
        };
    }

    private static int debugSwitch() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return 0;

        if (Settings.debug) {
            ChatSender.send("Pulsar debug mode off", ChatPrefix.DEBUG);
            SettingsFileManager.properties.put("debug", false);
            Settings.debug = false;
        } else {
            SettingsFileManager.properties.put("debug", true);
            Settings.debug = true;
            ChatSender.send("Pulsar debug mode on", ChatPrefix.DEBUG);
        }

        return 1;
    }
}
