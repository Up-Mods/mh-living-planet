package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.LPOptions;
import dev.upcraft.livingplanet.LivingPlanet;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import dev.upcraft.livingplanet.damage.LPDamageTypes;
import dev.upcraft.livingplanet.entity.ShockwaveBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static dev.upcraft.livingplanet.LivingPlanet.id;

public record ShockwavePacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<ShockwavePacket> TYPE = new Type<>(id("ability_shockwave"));
    public static final StreamCodec<FriendlyByteBuf, ShockwavePacket> STREAM_CODEC = BlockPos.STREAM_CODEC
            .map(ShockwavePacket::new, ShockwavePacket::pos).cast();
    public static final String COOLDOWN_MESSAGE_KEY = Util.makeDescriptionId("message", id("shockwave.cooldown"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayNetworking.Context ctx) {
        var player = ctx.player();
        var cooldowns = player.getComponent(LPComponents.LIVING_PLANET);
        if (!cooldowns.isOutOfGround()) {
            return;
        }

        if (!cooldowns.canShockwave()) {
            player.displayClientMessage(Component.translatable(COOLDOWN_MESSAGE_KEY).withStyle(ChatFormatting.RED), true);
            return;
        }
        
        var random = player.getRandom();

        var level = player.serverLevel();

        AABB boundingBox = player.getBoundingBox();
        BlockPos.randomBetweenClosed(random,
                (int) (20*LPOptions.SCALE.get()*LPOptions.SCALE.get()),
                (int) boundingBox.minX,
                (int) boundingBox.minY,
                (int) boundingBox.minZ,
                (int) Math.ceil(boundingBox.maxX),
                (int) Math.ceil(boundingBox.minY + player.getBbHeight()*0.6),
                (int) Math.ceil(boundingBox.maxZ)).forEach(pos ->
            throwBlock(level, pos, this.pos, cooldowns.getRandomState(random::nextInt), player));

        var damageSource = level.damageSources().source(LPDamageTypes.SHOCKWAVE, player);

        var aabb = player.getBoundingBox().inflate(10.0);
        level.getEntities(player, aabb, e -> !(e.isSpectator()) && !(e instanceof Player p && p.isCreative()) && e.isAlive() && e instanceof LivingEntity l && player.canAttack(l)).forEach(entity -> {
            pushEntity(entity, damageSource, this.pos);
        });

        cooldowns.onShockwave();
    }

    public static void pushEntity(Entity entity, DamageSource damageSource, BlockPos centrePos) {
        float damageAmount = LPOptions.SHOCKWAVE_DAMAGE.get();
        entity.hurt(damageSource, damageAmount);
        var dir = entity.position().subtract(centrePos.getBottomCenter()).normalize().add(0.0, 2.0, 0.0).scale(2.7);
        entity.push(dir);
        entity.hurtMarked = true;
    }

    private static void throwBlock(ServerLevel level, BlockPos pos, BlockPos centrePos, BlockState state, Player owner) {
        var dir = owner.getLookAngle()
                .with(Direction.Axis.Y, 0)
                .normalize()
                .add(0.0, 0.4, 0.0)
                .scale(2.7);
        ShockwaveBlockEntity.create(level, pos, state, dir, owner, centrePos);
    }
}
