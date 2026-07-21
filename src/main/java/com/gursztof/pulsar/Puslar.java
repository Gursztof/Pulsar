package com.gursztof.pulsar;

import com.gursztof.pulsar.command.ModCommands;
import com.gursztof.pulsar.macro.legitimacyTools.LegitimacyTools;
import com.gursztof.pulsar.macro.FarmingMacro;
import com.gursztof.pulsar.settings.SettingsFileManager;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Puslar implements ModInitializer {
	public static final String MOD_ID = "pulsar";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean farmingMacro = false;

	@Override
	public void onInitialize() {
		LOGGER.info("Pulsar init");
        try {
            SettingsFileManager.init();
        } catch (IOException e) {
			LOGGER.error("Settings init failed!", new RuntimeException(e));
        }
		ModCommands.init();
        FarmingMacro.init();
		LegitimacyTools.init();
    }

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

}
