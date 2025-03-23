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

public record GetHisAssPacket() implements CustomPacketPayload {
    public static final Type<GetHisAssPacket> TYPE = new Type<>(id("get_his_ass"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, GetHisAssPacket> STREAM_CODEC = StreamCodec.unit(new GetHisAssPacket());

    @Override
    public Type<GetHisAssPacket> type() {
        return TYPE;
    }

    public void handle(ServerPlayNetworking.Context ctx) {
        var player = ctx.player();
        var cooldowns = player.getComponent(LPComponents.LIVING_PLANET);
        if (!cooldowns.isLivingPlanet()) {
            return;
        }

        if (!cooldowns.canGetHisAss()) {
            player.displayClientMessage(Component.translatable(COOLDOWN_MESSAGE_KEY).withStyle(ChatFormatting.RED), true);
            return;
        }

        //TODO
    }
}
