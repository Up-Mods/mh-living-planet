package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.LivingPlanet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ShockwavePacket implements CustomPacketPayload {

    public static final Type<ShockwavePacket> TYPE = new Type<>(LivingPlanet.id("ability_shockwave"));
    public static final ShockwavePacket INSTANCE = new ShockwavePacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, ShockwavePacket> CODEC = StreamCodec.unit(INSTANCE);

    private ShockwavePacket() {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
