package dev.upcraft.livingplanet.net;

import dev.upcraft.livingplanet.LPOptions;
import dev.upcraft.livingplanet.LivingPlanet;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import dev.upcraft.livingplanet.damage.LPDamageTypes;
import dev.upcraft.livingplanet.entity.ShockwaveBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record ShockwavePacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<ShockwavePacket> TYPE = new Type<>(LivingPlanet.id("ability_shockwave"));
    public static final StreamCodec<FriendlyByteBuf, ShockwavePacket> STREAM_CODEC = BlockPos.STREAM_CODEC
            .map(ShockwavePacket::new, ShockwavePacket::pos).cast();

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayNetworking.Context ctx) {
        var player = ctx.player();
        var cooldowns = player.getComponent(LPComponents.LIVING_PLANET);
        if (!cooldowns.canShockwave()) {
            return;
        }

        var level = player.serverLevel();

        var forwards = ctx.player().getDirection();
        var backwards = forwards.getOpposite();
        var left = forwards.getCounterClockWise();
        var right = forwards.getClockWise();
        var mutablePos = this.pos.mutable();

        mutablePos.move(left).move(left);
        throwBlock(level, mutablePos, this.pos, player);

        mutablePos.move(forwards);
        throwBlock(level, mutablePos, this.pos, player);

        mutablePos.move(right);
        throwBlock(level, mutablePos, this.pos, player);

        mutablePos.move(forwards);
        throwBlock(level, mutablePos, this.pos, player);

        mutablePos.move(right);
        throwBlock(level, mutablePos, this.pos, player);

        mutablePos.move(right);
        throwBlock(level, mutablePos, this.pos, player);

        mutablePos.move(backwards);
        throwBlock(level, mutablePos, this.pos, player);

        mutablePos.move(right);
        throwBlock(level, mutablePos, this.pos, player);

        mutablePos.move(backwards);
        throwBlock(level, mutablePos, this.pos, player);

        var damageSource = level.damageSources().source(LPDamageTypes.SHOCKWAVE, player);

        var aabb = new AABB(
                this.pos.getBottomCenter()
                        .add(Vec3.ZERO.with(forwards.getAxis(), forwards.getAxisDirection().getStep()*0.5))
                        .add(Vec3.ZERO.with(left.getAxis(), left.getAxisDirection().getStep()*2.5)),
                this.pos.getBottomCenter()
                        .add(0, 2, 0)
                        .add(Vec3.ZERO.with(forwards.getAxis(), forwards.getAxisDirection().getStep()*2.5))
                        .add(Vec3.ZERO.with(right.getAxis(), right.getAxisDirection().getStep()*2.5)));
        level.getEntities(player, aabb, e -> !(e.isSpectator()) && !(e instanceof Player p && p.isCreative()) && e.isAlive() && e instanceof LivingEntity l && player.canAttack(l)).forEach(entity -> {
            pushEntity(entity, damageSource, this.pos);
        });

        cooldowns.onShockwave();
    }

    public static void pushEntity(Entity entity, DamageSource damageSource, BlockPos centrePos) {
        float damageAmount = LPOptions.SHOCKWAVE_DAMAGE.get();
        entity.hurt(damageSource, damageAmount);
        var dir = entity.position().subtract(centrePos.getBottomCenter()).normalize().add(0.0, 0.7, 0.0).scale(0.3);
        entity.push(dir);
        entity.hurtMarked = true;
    }

    private static void throwBlock(ServerLevel level, BlockPos.MutableBlockPos mutablePos, BlockPos centrePos, Player owner) {
        for (int i = 0; i < 5; i++) {
            if (level.getBlockState(mutablePos.above()).is(Blocks.SAND)) {
                mutablePos.move(Direction.UP);
            }
        }
        var state = level.getBlockState(mutablePos);
        var dir = owner.getLookAngle()
                .with(Direction.Axis.Y, 0)
                .normalize()
                .add(0.0, 0.7, 0.0)
                .scale(0.7);
        ShockwaveBlockEntity.create(level, mutablePos, state, dir, owner, centrePos);
        //todo fx
    }
}
