package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.LivingPlanet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ToggleFormPacket implements CustomPacketPayload {

    public static final Type<ToggleFormPacket> TYPE = new Type<>(LivingPlanet.id("toggle_form"));
    public static final ToggleFormPacket INSTANCE = new ToggleFormPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleFormPacket> CODEC = StreamCodec.unit(INSTANCE);

    private ToggleFormPacket() {

    }



    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
