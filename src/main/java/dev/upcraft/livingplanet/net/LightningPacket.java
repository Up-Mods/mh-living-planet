package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.naturaldisasters.NaturalDisasters;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

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

        if (!cooldowns.canLightning()) {
            player.displayClientMessage(Component.translatable(COOLDOWN_MESSAGE_KEY).withStyle(ChatFormatting.RED), true);
            return;
        }

        LPComponents.NATURAL_DISASTERS.get(player.level()).addDisaster(NaturalDisasters.LIGHTNING_STORM.spawnFunc().apply(player.serverLevel(), List.of(player)));
        cooldowns.onLightning();
    }
}
