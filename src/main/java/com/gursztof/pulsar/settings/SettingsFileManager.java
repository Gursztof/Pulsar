package com.gursztof.pulsar.settings;

import com.gursztof.pulsar.Puslar;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class SettingsFileManager {
    public static Properties properties;

    public static void init() throws IOException {
        try {
            // Fabric default folder for configs
            Path configDir = FabricLoader.getInstance().getConfigDir();
            File settingsFile = configDir.resolve("pulsarSettings.properties").toFile();

            if (settingsFile.createNewFile()) {
                Puslar.LOGGER.info("File created");
                generateDefaults();
            } else {
                Puslar.LOGGER.info("File already exists!");
            }
        } catch (IOException e) {
            Puslar.LOGGER.error("Something went wrong when creating file!", new RuntimeException(e));
        }

        Path configDir = FabricLoader.getInstance().getConfigDir();
        File settingsFile = configDir.resolve("pulsarSettings.properties").toFile();
        properties = new Properties();
        properties.load(new FileInputStream(settingsFile));


        Settings.init();
    }

    public static void updateProperties() throws IOException {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path settingsFilePath = configDir.resolve("pulsarSettings.properties");
        File settingsFile = settingsFilePath.toFile();
        new FileWriter(settingsFile, false).close();

        List<String> NEW_SETTINGS = List.of(
                "delayTicks=" + Settings.delayTicks,
                "debug=" + Settings.debug,
                "maxTicks=" + Settings.maxTicks,
                "maxDistance=" + Settings.maxDistance,
                "brakeChance=" + Settings.brakeChance
        );

        Files.write(settingsFilePath, NEW_SETTINGS);
        properties.load(new FileInputStream(settingsFile));
    }

    private static final List<String> DEFAULTS_SETTINGS = List.of(
            "delayTicks=43",
            "debug=false",
            "maxTicks=252000",
            "maxDistance=100",
            "brakeChance=25"
    );

    private static void generateDefaults() throws IOException {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path settingsFilePath = Paths.get(String.valueOf(configDir), "pulsarSettings.properties");

        Files.write(settingsFilePath, DEFAULTS_SETTINGS);
    }
}
