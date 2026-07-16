package com.gursztof.pulsar.command;

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.chat.ChatPrefix;
import com.gursztof.pulsar.macro.AntiBanFeatures;
import com.gursztof.pulsar.macro.FarmingMacro;
import com.gursztof.pulsar.settings.Settings;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ModCommands {
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
            case "on" -> FarmingMacroSwitch("on");
            case "off" -> FarmingMacroSwitch("off");
            case "debug" -> debugSwitch();
            default -> 0;
        };
    }

    private static int FarmingMacroSwitch(String value) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;

        if (value.equals("on") && !Puslar.farmingMacro) {
            player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Macro on"), false);
            Puslar.farmingMacro = true;
            Settings.direction = player.getHorizontalFacing();
            FarmingMacro.lastTurnWasLeft = true;

            AntiBanFeatures.shiftManager();
            AntiBanFeatures.brakeManager();
            AntiBanFeatures.reactionDelayManager();

            if (Settings.debug) {
                player.sendMessage(ChatPrefix.PULSAR.getPrefix().append(String.valueOf(Settings.direction)), false);
            }
        } else if (value.equals("off") && Puslar.farmingMacro) {
            player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Macro off"), false);
            Puslar.farmingMacro = false;
        }
        return 1;
    }

    private static int debugSwitch() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;

        if (Settings.debug) {
            Settings.debug = false;
            player.sendMessage(ChatPrefix.INFO.getPrefix().append("Pulsar debug mode off"), false);
        } else {
            Settings.debug = true;
            player.sendMessage(ChatPrefix.INFO.getPrefix().append("Pulsar debug mode on"), false);
        }

        return 1;
    }
}
