package dev.upcraft.livingplanet.mixin;

import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "isInWall", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityDimensions;width()F"), cancellable = true)
    private void planetlordbiomes$desert$dontSuffocateIfBurrowed(CallbackInfoReturnable<Boolean> cir) {
        if (LPComponents.LIVING_PLANET.maybeGet(this).filter(LivingPlanetComponent::isPhasing).isPresent()) {
            cir.setReturnValue(false);
        }
    }
}
