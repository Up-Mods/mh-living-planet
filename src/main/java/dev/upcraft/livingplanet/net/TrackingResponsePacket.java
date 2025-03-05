package dev.upcraft.livingplanet.net;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static dev.upcraft.livingplanet.LivingPlanet.id;

public record TrackingResponsePacket(Vec3 position, ResourceKey<Level> dimension) implements CustomPacketPayload {
    public static final Type<TrackingResponsePacket> TYPE = new Type<>(id("tracking_response"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, TrackingResponsePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F.map(Vec3::new, Vec3::toVector3f), TrackingResponsePacket::position,
            ResourceKey.streamCodec(Registries.DIMENSION), TrackingResponsePacket::dimension,
            TrackingResponsePacket::new);

    @Override
    public Type<TrackingResponsePacket> type() {
        return TYPE;
    }
}
