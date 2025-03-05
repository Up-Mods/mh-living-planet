package dev.upcraft.livingplanet.mixin;

import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "maxUpStep", at = @At("HEAD"), cancellable = true)
    private void planetlordbiomes$desert$stepUpOne(CallbackInfoReturnable<Float> cir) {
        if (LPComponents.LIVING_PLANET.maybeGet(this).filter(Predicate.not(LivingPlanetComponent::isOutOfGround)).isPresent()) {
            cir.setReturnValue(4f);
        }
    }
}
