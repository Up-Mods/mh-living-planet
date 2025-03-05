package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.component.LPComponents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class LPNetworking {
    public static void init() {
        registerC2S(ToggleFormPacket.TYPE, ToggleFormPacket.CODEC, (payload, context) -> {
            context.server().execute(() -> {
                var component = context.player().getComponent(LPComponents.LIVING_PLANET);
                component.setOutOfGround(!component.isOutOfGround());
                component.sync();
                context.player().refreshDimensions();
            });
        });

        registerC2S(TogglePhasingPacket.TYPE, TogglePhasingPacket.CODEC, (payload, context) -> {
            context.server().execute(() -> {
                var component = context.player().getComponent(LPComponents.LIVING_PLANET);
                component.setPhasing(!component.isPhasing());
                component.sync();
                context.player().refreshDimensions();
            });
        });

        registerC2S(ShockwavePacket.TYPE, ShockwavePacket.STREAM_CODEC, ShockwavePacket::handle);

        registerC2S(TrackingRequestPacket.TYPE, TrackingRequestPacket.CODEC, TrackingRequestPacket::handle);

        registerS2C(TrackingResponsePacket.TYPE, TrackingResponsePacket.CODEC);
    }

    private static <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(type, codec);
    }

    private static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, handler);
    }
}
