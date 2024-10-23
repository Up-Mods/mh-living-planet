package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.LivingPlanet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class PhaseThroughWallPacket implements CustomPacketPayload {

    public static final Type<PhaseThroughWallPacket> TYPE = new Type<>(LivingPlanet.id("ability_phase"));
    public static final PhaseThroughWallPacket INSTANCE = new PhaseThroughWallPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, PhaseThroughWallPacket> CODEC = StreamCodec.unit(INSTANCE);

    private PhaseThroughWallPacket() {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
