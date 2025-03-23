package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.component.LPComponents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static dev.upcraft.livingplanet.LivingPlanet.id;
import static dev.upcraft.livingplanet.net.ShockwavePacket.COOLDOWN_MESSAGE_KEY;

public record RagePacket() implements CustomPacketPayload {
    public static final Type<RagePacket> TYPE = new Type<>(id("rage"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, RagePacket> STREAM_CODEC = StreamCodec.unit(new RagePacket());
    private static final double RADIUS = 50;

    @Override
    public Type<RagePacket> type() {
        return TYPE;
    }

    public void handle(ServerPlayNetworking.Context ctx) {
        var player = ctx.player();
        var level = player.level();
        var cooldowns = player.getComponent(LPComponents.LIVING_PLANET);
        if (!cooldowns.isLivingPlanet()) {
            return;
        }

        if (!cooldowns.canRage()) {
            player.displayClientMessage(Component.translatable(COOLDOWN_MESSAGE_KEY).withStyle(ChatFormatting.RED), true);
            return;
        }

        level.getEntities(player, new AABB(player.position().subtract(RADIUS, RADIUS, RADIUS), player.position().add(RADIUS, RADIUS, RADIUS)), e -> e instanceof Mob).forEach(e -> enrage(e, player));
        cooldowns.onRage();
    }

    private static void enrage(Entity entity, Player player) {
        if (!(entity instanceof PathfinderMob mob)) {
            return;
        }

        mob.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(mob, Player.class, false, p -> p != player));
        mob.goalSelector.addGoal(0, new MeleeAttackGoal(mob, 2.0, true));
    }
}
