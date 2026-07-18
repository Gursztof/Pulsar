package com.gursztof.pulsar.command;

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.chat.ChatPrefix;
import com.gursztof.pulsar.macro.antiBanFeatures.AntiBanFeatures;
import com.gursztof.pulsar.macro.FarmingMacro;
import com.gursztof.pulsar.macro.antiBanFeatures.TickFeatures;
import com.gursztof.pulsar.settings.Settings;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class PulsarMainCommands {
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
            player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Macro on"), false);
            Puslar.farmingMacro = true;
            Settings.direction = player.getHorizontalFacing();
            TickFeatures.rotated = false;

            // TEMP
            FarmingMacro.canBeRight = true;
            FarmingMacro.canBeLeft = true;


            if (Settings.debug) {
                player.sendMessage(ChatPrefix.PULSAR.getPrefix().append(String.valueOf(Settings.direction)), false);
            }
        } else if (value.equals("off") && Puslar.farmingMacro) {
            player.sendMessage(ChatPrefix.PULSAR.getPrefix().append("Macro off"), false);
            Puslar.farmingMacro = false;
        }
        return 1;
    }
}
