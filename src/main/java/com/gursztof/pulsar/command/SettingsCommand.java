package com.gursztof.pulsar.command;

import com.gursztof.pulsar.settings.Settings;
import com.gursztof.pulsar.settings.SettingsFileManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.io.IOException;

public class PulsarSettingsCommand {
    // TODO add hints to all settings
    public void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("pulsar")
                    .then(ClientCommandManager.literal("settings")
                            .then(ClientCommandManager.argument("settingName", StringArgumentType.string())
                                    .then(ClientCommandManager.argument("value", StringArgumentType.string())
                                            .executes(context -> {
                                                String settingName = StringArgumentType.getString(context, "settingName");
                                                String value = StringArgumentType.getString(context, "value");
                                                try {
                                                    return changeSetting(settingName, value);
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            })
                                    )
                            )
                    )
            );

        });
    }

    private static int changeSetting(String settingName, String value) throws IOException {
        switch (settingName) {
            case "maxTicks" -> Settings.maxTicks = Integer.parseInt(value);
            case "delayTicks" -> Settings.delayTicks = Integer.parseInt(value);
            case "maxDistance" -> Settings.maxDistance = Integer.parseInt(value);
        }

        SettingsFileManager.properties.put(settingName, value);
        SettingsFileManager.updateProperties();
        return 1;
    }
}
