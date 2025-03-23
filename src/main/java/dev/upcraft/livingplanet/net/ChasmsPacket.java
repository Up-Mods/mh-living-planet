package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.component.LPComponents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.upcraft.livingplanet.LivingPlanet.id;
import static dev.upcraft.livingplanet.net.ShockwavePacket.COOLDOWN_MESSAGE_KEY;

public record ChasmsPacket() implements CustomPacketPayload {
    public static final Type<ChasmsPacket> TYPE = new Type<>(id("chasms"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, ChasmsPacket> STREAM_CODEC = StreamCodec.unit(new ChasmsPacket());

    @Override
    public Type<ChasmsPacket> type() {
        return TYPE;
    }

    public void handle(ServerPlayNetworking.Context ctx) {
        var player = ctx.player();
        var cooldowns = player.getComponent(LPComponents.LIVING_PLANET);
        if (!cooldowns.isLivingPlanet()) {
            return;
        }

        if (!cooldowns.isOutOfGround()) {
            return;
        }

        if (!cooldowns.canChasms()) {
            player.displayClientMessage(Component.translatable(COOLDOWN_MESSAGE_KEY).withStyle(ChatFormatting.RED), true);
            return;
        }

        //TODO
    }
}
