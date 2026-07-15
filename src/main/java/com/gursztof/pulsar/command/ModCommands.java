package com.gursztof.pulsar.command;

import com.gursztof.pulsar.Puslar;
import com.gursztof.pulsar.macro.AntiBanFeatures;
import com.gursztof.pulsar.macro.FarmingMacro;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Set;

import static net.minecraft.server.command.CommandManager.*;

public class ModCommands implements CommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(literal("pulsar").then(literal("on").executes(context -> {
            context.getSource().sendMessage(Text.literal("Pulsar on"));
            Puslar.farmingMacro = true;
            FarmingMacro.direction = context.getSource().getPlayer().getHorizontalFacing();
            FarmingMacro.lastTurnWasLeft = true;

            AntiBanFeatures.shiftManager();
            AntiBanFeatures.brakeManager();
            AntiBanFeatures.reactionDelayManager();

            context.getSource().sendMessage(Text.literal(String.valueOf(FarmingMacro.direction)));
            return 1;
        })));

        dispatcher.register(literal("pulsar").then(literal("off").executes(context -> {
            context.getSource().sendMessage(Text.literal("Pulsar off"));
            Puslar.farmingMacro = false;
            MinecraftClient.getInstance().options.sprintKey.setPressed(false);
            return 1;
        })));

        dispatcher.register(literal("garden").executes(context -> {
            ServerPlayerEntity player = context.getSource().getPlayer();
            assert player != null;
            if (player.getPermissions().equals(PermissionPredicate.ALL)) {
                player.teleport(
                        player.getEntityWorld(),
                        37.5,
                        -60,
                        -76.650,
                        Set.of(),
                        -180,
                        (float) 6.6,
                        true
                );
                return 1;
            }
            return 0;
        }));
    }
}
