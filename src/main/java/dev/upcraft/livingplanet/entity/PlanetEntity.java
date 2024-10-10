package dev.upcraft.livingplanet.entity;

import dev.upcraft.livingplanet.component.LPComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PlanetEntity extends Monster {

    private static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(PlanetEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public PlanetEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER, Optional.empty());
    }

    public void setOwner(@Nullable ServerPlayer owner) {
        setOwner(owner == null ? null : owner.getUUID());
    }

    public void setOwner(@Nullable UUID ownerId) {
        this.entityData.set(OWNER, Optional.ofNullable(ownerId));
    }

    public Optional<UUID> getOwnerId() {
        return this.entityData.get(OWNER);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("owner")) {
            this.setOwner(compound.getUUID("owner"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.getOwnerId().ifPresent(uuid -> compound.putUUID("owner", uuid));
    }

    @Override
    protected void tickDeath() {
        this.setHealth(this.getMaxHealth());
        var owner = this.getOwner();
        if(owner != null) {
            owner.getComponent(LPComponents.LIVING_PLANET).setImmobilized();
            level().globalLevelEvent(LevelEvent.SOUND_END_PORTAL_SPAWN, this.blockPosition(), 0);
        }
        else {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        var owner = this.getOwner();
        if (owner != null) {
            return owner;
        }
        return super.getControllingPassenger();
    }

    @Nullable
    public Player getOwner() {
        return this.getOwnerId().map(this.level()::getPlayerByUUID).orElse(null);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 50F).add(Attributes.KNOCKBACK_RESISTANCE, 100F).add(Attributes.MOVEMENT_SPEED).add(Attributes.ATTACK_DAMAGE, 1.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ATTACK_SPEED).add(Attributes.LUCK).add(Attributes.BLOCK_INTERACTION_RANGE, 4.5).add(Attributes.ENTITY_INTERACTION_RANGE, 3.0).add(Attributes.BLOCK_BREAK_SPEED).add(Attributes.SUBMERGED_MINING_SPEED).add(Attributes.SNEAKING_SPEED).add(Attributes.MINING_EFFICIENCY).add(Attributes.SWEEPING_DAMAGE_RATIO);
    }
}
