package dev.upcraft.livingplanet.client.rockthrow;

import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.mixin.client.MinecraftMixin;
import dev.upcraft.livingplanet.net.RockThrowPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

public class RockThrow {
    private static long timeStartedHolding;

    public static boolean isThrowing() {
        return timeStartedHolding > 0;
    }

    public static boolean canStart(LocalPlayer player) {
        var cmp = LPComponents.LIVING_PLANET.get(player);
        return cmp.isLivingPlanet();
    }

    public static void start(ClientLevel level) {
        timeStartedHolding = level.getGameTime();
    }

    public static void stop(ClientLevel level) {
        long timeHeld = level.getGameTime() - timeStartedHolding;
        ClientPlayNetworking.send(new RockThrowPacket((int) timeHeld));
        timeStartedHolding = -1;
    }

    public static void tick(LocalPlayer player) {
        if (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
            timeStartedHolding = -1;
        }
    }
}
