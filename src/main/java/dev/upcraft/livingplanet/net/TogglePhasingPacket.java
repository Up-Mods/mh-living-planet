package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.LivingPlanet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class TogglePhasingPacket implements CustomPacketPayload {

    public static final Type<TogglePhasingPacket> TYPE = new Type<>(LivingPlanet.id("ability_phase"));
    public static final TogglePhasingPacket INSTANCE = new TogglePhasingPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, TogglePhasingPacket> CODEC = StreamCodec.unit(INSTANCE);

    private TogglePhasingPacket() {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
