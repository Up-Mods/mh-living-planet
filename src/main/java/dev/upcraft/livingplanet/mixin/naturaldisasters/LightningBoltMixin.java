package dev.upcraft.livingplanet.mixin.naturaldisasters;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.naturaldisasters.LightningStorm;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin extends Entity {
    public LightningBoltMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    private boolean naturaldisasters$lessNoisePls(boolean original) {
        if (LPComponents.NATURAL_DISASTERS.get(this.level()).hasDisaster(LightningStorm.class)) {
            return original && this.random.nextInt(100) == 0;
        }

        return original;
    }
}
