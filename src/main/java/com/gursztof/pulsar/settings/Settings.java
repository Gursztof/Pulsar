package com.gursztof.pulsar.settings;

import net.minecraft.util.math.Direction;

public class Settings {
    public static Direction direction;
    public static int delayTicks = Integer.parseInt(SettingsFileManager.properties.getProperty("delayTicks"));
    public static boolean debug = Boolean.parseBoolean(SettingsFileManager.properties.getProperty("debug"));
    public static int maxTicks = Integer.parseInt(SettingsFileManager.properties.getProperty("maxTicks"));
    public static int maxDistance = Integer.parseInt(SettingsFileManager.properties.getProperty("maxDistance"));
    public static int brakeChance = Integer.parseInt(SettingsFileManager.properties.getProperty("brakeChance"));

    static public void init() {

    }
}
