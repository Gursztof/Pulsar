package com.gursztof.pulsar.command;

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.chat.ChatPrefix;
import com.gursztof.pulsar.macro.AntiBanFeatures;
import com.gursztof.pulsar.macro.FarmingMacro;
import com.gursztof.pulsar.settings.Settings;
import com.gursztof.pulsar.settings.SettingsFileManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.io.IOException;

public class ModCommands {
    public static void init() {
        PulsarMainCommands.init();

    }
}
