package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.entity.ThrownRock;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

import static dev.upcraft.livingplanet.LivingPlanet.id;
import static dev.upcraft.livingplanet.net.ShockwavePacket.COOLDOWN_MESSAGE_KEY;

public record LightningPacket() implements CustomPacketPayload {
    public static final Type<LightningPacket> TYPE = new Type<>(id("lightning"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, LightningPacket> STREAM_CODEC = StreamCodec.unit(new LightningPacket());

    @Override
    public Type<LightningPacket> type() {
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

        if (!cooldowns.canLightning()) {
            player.displayClientMessage(Component.translatable(COOLDOWN_MESSAGE_KEY).withStyle(ChatFormatting.RED), true);
            return;
        }

        //TODO
    }
}
