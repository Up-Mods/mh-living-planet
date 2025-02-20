package dev.upcraft.livingplanet.entity;

import dev.upcraft.livingplanet.damage.LPDamageTypes;
import dev.upcraft.livingplanet.net.ShockwavePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

public class ShockwaveBlockEntity extends FallingBlockEntity implements OwnableEntity {
    private @Nullable UUID ownerUuid;
    private BlockPos centrePos = BlockPos.ZERO;

    public ShockwaveBlockEntity(EntityType<? extends ShockwaveBlockEntity> entityType, Level level, @Nullable LivingEntity owner) {
        super(entityType, level);
        this.ownerUuid = owner == null ? null : owner.getUUID();
        this.noPhysics = true;
    }

    public ShockwaveBlockEntity(EntityType<? extends ShockwaveBlockEntity> entityType, Level level) {
        this(entityType, level, null);
    }

    private ShockwaveBlockEntity(Level level, double x, double y, double z, BlockState blockState, Vec3 vel, LivingEntity owner, BlockPos centrePos) {
        this(LPEntities.SHOCKWAVE_BLOCK.get(), level, owner);
        this.blockState = blockState;
        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(vel);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
        this.centrePos = centrePos;
    }

    public static ShockwaveBlockEntity create(Level level, BlockPos blockPos, BlockState blockState, Vec3 vel, LivingEntity owner, BlockPos centrePos) {
        var fallingBlockEntity = new ShockwaveBlockEntity(
                level,
                (double)blockPos.getX() + 0.5,
                blockPos.getY() + 1.0,
                (double)blockPos.getZ() + 0.5,
                blockState.hasProperty(BlockStateProperties.WATERLOGGED) ? blockState.setValue(BlockStateProperties.WATERLOGGED, false) : blockState,
                vel,
                owner,
                centrePos);
        level.setBlock(blockPos, blockState.getFluidState().createLegacyBlock(), 3);
        level.addFreshEntity(fallingBlockEntity);
        return fallingBlockEntity;
    }

    @Override
    public void tick() {
        if (this.tickCount > 3 && this.noPhysics) {
            this.noPhysics = false;
        }
        super.tick();
        if (!this.level().isClientSide()) {
            Predicate<Entity> predicate = EntitySelector.NO_CREATIVE_OR_SPECTATOR
                    .and(EntitySelector.LIVING_ENTITY_STILL_ALIVE)
                    .and(e -> !e.getUUID().equals(this.ownerUuid));
            var damageSource = this.damageSources().source(LPDamageTypes.SHOCKWAVE, this.getOwner());
            this.level().getEntities(this, this.getBoundingBox().inflate(0.3), predicate).forEach(entity -> {
                ShockwavePacket.pushEntity(entity, damageSource, this.centrePos);

                if (!this.isRemoved()) {
                    this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
                    this.discard();
                }
            });
        }
    }

    @Override
    public void handleEntityEvent(byte b) {
        switch (b) {
            case 3 -> {
                this.playSound(this.getBlockState().getSoundType().getBreakSound());
                for (int i = 0; i < 20; i++) {
                    this.level().addParticle(
                            new BlockParticleOption(ParticleTypes.BLOCK, this.getBlockState()),
                            this.getRandomX(0.5),
                            this.getRandomY(),
                            this.getRandomZ(0.5),
                            this.random.nextDouble()*0.2,
                            this.random.nextGaussian()*0.2,
                            this.random.nextDouble()*0.2);
                }
            }
            default -> super.handleEntityEvent(b);
        }
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.ownerUuid;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.ownerUuid != null) {
            compoundTag.putUUID("owner", this.ownerUuid);
        }
        compoundTag.put("centre_pos", NbtUtils.writeBlockPos(this.centrePos));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("owner")) {
            this.ownerUuid = compoundTag.getUUID("owner");
        }
        this.centrePos = NbtUtils.readBlockPos(compoundTag, "centre_pos").orElse(this.blockPosition());
    }
}