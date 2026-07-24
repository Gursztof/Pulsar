package com.gursztof.pulsar.command;

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.chat.ChatPrefix;
import com.gursztof.pulsar.chat.ChatSender;
import com.gursztof.pulsar.macro.FarmingMacro;
import com.gursztof.pulsar.macro.legitimacyTools.TickTools;
import com.gursztof.pulsar.settings.Settings;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MainCommands {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("pulsar")
                    .then(ClientCommandManager.literal("on")
                            .executes(context -> farmingMacroSwitch("on"))));

            dispatcher.register(ClientCommandManager.literal("pulsar")
                    .then(ClientCommandManager.literal("off")
                            .executes(context -> farmingMacroSwitch("off"))));

        });
    }

    private static int farmingMacroSwitch(String value) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return 0;

        if (value.equals("on") && !Puslar.farmingMacro) {
            ChatSender.send("Macro on", ChatPrefix.PULSAR);

            FarmingMacro.canBeRight = true;
            FarmingMacro.canBeLeft = true;

            Puslar.farmingMacro = true;
            Settings.direction = player.getHorizontalFacing();
            TickTools.rotated = false;

            ChatSender.send(String.valueOf(Settings.direction), ChatPrefix.DEBUG);
        } else if (value.equals("off") && Puslar.farmingMacro) {
            ChatSender.send("Macro off", ChatPrefix.PULSAR);
            Puslar.farmingMacro = false;
        }
        return 1;
    }
}
