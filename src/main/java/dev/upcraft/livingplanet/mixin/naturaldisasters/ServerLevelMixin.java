package dev.upcraft.livingplanet.mixin.naturaldisasters;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.upcraft.livingplanet.LPOptions;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.naturaldisasters.LightningStorm;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @ModifyExpressionValue(method = "tickChunk",
    at = @At(value = "CONSTANT", args = "intValue=100000"),
    slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isThundering()Z"),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;findLightningTargetAround(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;")
    ))
    private int naturaldisasters$changeLightningChance(int original) {
        return LPComponents.NATURAL_DISASTERS.get(this).hasDisaster(LightningStorm.class) ? LPOptions.LIGHTNING_STORM_INVERSE_INTENSITY.getValue() : original;
    }
}
