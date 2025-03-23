package dev.upcraft.livingplanet.mixin.naturaldisasters;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.naturaldisasters.LightningStorm;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public abstract class LevelMixin {
    @ModifyReturnValue(method = "isThundering", at = @At("RETURN"))
    private boolean naturaldisasters$makeThunderingInLightningStorm(boolean original) {
        return original || LPComponents.NATURAL_DISASTERS.get(this).hasDisaster(LightningStorm.class);
    }

    @ModifyReturnValue(method = "isRaining", at = @At("RETURN"))
    private boolean naturaldisasters$makeRainingInLightningStorm(boolean original) {
        return original || LPComponents.NATURAL_DISASTERS.get(this).hasDisaster(LightningStorm.class);
    }

    @ModifyReturnValue(method = "isRainingAt", at = @At("RETURN"))
    private boolean naturaldisasters$makeRainingEverywhereInLightningStorm(boolean original) {
        return original || LPComponents.NATURAL_DISASTERS.get(this).hasDisaster(LightningStorm.class);
    }
}
