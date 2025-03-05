package dev.upcraft.livingplanet.entity;

import dev.upcraft.livingplanet.LPOptions;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownRock extends ThrowableItemProjectile {
    public ThrownRock(LivingEntity shooter, Level level) {
        super(LPEntities.THROWN_ROCK.get(), shooter, level);
    }

    public ThrownRock(double x, double y, double z, Level level) {
        super(LPEntities.THROWN_ROCK.get(), x, y, z, level);
    }

    public ThrownRock(EntityType<? extends ThrownRock> entityType, Level level) {
        super(entityType, level);
    }

    private ParticleOptions getParticle() {
        ItemStack itemStack = this.getItem();
        return !itemStack.isEmpty() && !itemStack.is(this.getDefaultItem())
                ? new ItemParticleOption(ParticleTypes.ITEM, itemStack)
                : new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState());
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleOptions = this.getParticle();

            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particleOptions, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), LPOptions.THROWN_ROCK_DAMAGE.get());
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.STONE;
    }
}
