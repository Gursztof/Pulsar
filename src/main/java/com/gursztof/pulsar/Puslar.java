package com.gursztof.pulsar;

import com.gursztof.pulsar.command.ModCommands;
import com.gursztof.pulsar.macro.FarmingMacro;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Puslar implements ModInitializer {
	public static final String MOD_ID = "puslar";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean farmingMacro = false;

	@Override
	public void onInitialize() {
		LOGGER.info("Pulsar init");
		ModCommands.init();
		FarmingMacro.init();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

}
