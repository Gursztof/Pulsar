package com.gursztof.pulsar.macro;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class Hitting {
    private static boolean isHitting = false;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (isHitting) tick(client);
        });
    }

    public static void start() {
        isHitting = true;
    }

    public static void stop() {
        isHitting = false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.interactionManager != null) {
            client.interactionManager.cancelBlockBreaking();
        }
    }

    private static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null) return;

        if (!client.player.isOnGround()) {
            stop();
            return;
        }

        BlockHitResult hit = raycast(client);
        if (hit.getType() != HitResult.Type.BLOCK) return; // nothing in reach — same as clicking into open sky, not a "check"

        client.interactionManager.updateBlockBreakingProgress(hit.getBlockPos(), hit.getSide());
        client.player.swingHand(Hand.MAIN_HAND);
    }

    private static BlockHitResult raycast(MinecraftClient client) {
        double reach = client.player.getAttributeValue(EntityAttributes.BLOCK_INTERACTION_RANGE);
        Vec3d start = client.player.getCameraPosVec(1.0f);
        Vec3d look = client.player.getRotationVec(1.0f);
        Vec3d end = start.add(look.multiply(reach));

        return (BlockHitResult) client.world.raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                client.player
        ));
    }
}
