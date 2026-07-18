package com.gursztof.pulsar.command;

public class ModCommandsManager {
    public static void init() {
        PulsarMainCommands.init();
        PulsarDevCommands.init();
    }
}
