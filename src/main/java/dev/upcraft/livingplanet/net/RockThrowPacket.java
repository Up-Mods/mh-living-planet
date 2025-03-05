package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.entity.ThrownRock;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Items;

import static dev.upcraft.livingplanet.LivingPlanet.id;

public record RockThrowPacket(int timeHeld) implements CustomPacketPayload {
    public static final Type<RockThrowPacket> TYPE = new Type<>(id("rock_throw"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, RockThrowPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RockThrowPacket::timeHeld,
            RockThrowPacket::new);

    @Override
    public Type<RockThrowPacket> type() {
        return TYPE;
    }

    public void handle(ServerPlayNetworking.Context context) {
        if (!LPComponents.LIVING_PLANET.get(context.player()).isLivingPlanet()) {
            return;
        }

        var player = context.player();
        float strength = Mth.lerp(Math.clamp(timeHeld, 0, 20)/20f, 0.02f, 2.0f);

        var projectile = new ThrownRock(player, player.level());
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, strength, 0.2f);
        player.level().addFreshEntity(projectile);
        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ARROW_SHOOT,
                SoundSource.PLAYERS,
                1.0F,
                1.0F / (player.level().getRandom().nextFloat() * 0.4F + 1.2F) + strength*0.2f * 0.5F
        );
    }
}
