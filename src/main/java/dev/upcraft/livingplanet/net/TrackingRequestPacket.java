package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.component.LPComponents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

import static dev.upcraft.livingplanet.LivingPlanet.id;

public record TrackingRequestPacket(UUID player) implements CustomPacketPayload {
    public static final Type<TrackingRequestPacket> TYPE = new Type<>(id("tracking_request"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, TrackingRequestPacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, TrackingRequestPacket::player,
            TrackingRequestPacket::new);

    @Override
    public Type<TrackingRequestPacket> type() {
        return TYPE;
    }

    public void handle(ServerPlayNetworking.Context context) {
        if (LPComponents.LIVING_PLANET.get(context.player()).isLivingPlanet()) {
            var tracked = context.server().getPlayerList().getPlayer(this.player());
            if (tracked != null) {
                context.responseSender().sendPacket(new TrackingResponsePacket(tracked.position(), tracked.level().dimension()));
            }
        }
    }
}
