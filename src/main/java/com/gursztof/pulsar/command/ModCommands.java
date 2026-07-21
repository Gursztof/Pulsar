package com.gursztof.pulsar.command;

public class ModCommands {
    public static void init() {
        MainCommands.init();
        DevCommands.init();
        SettingsCommand.init();
    }
}
