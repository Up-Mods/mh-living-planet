package dev.upcraft.livingplanet.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyReturnValue(method = "getBoundingBoxForCulling", at = @At("RETURN"))
    private AABB lp$getBiggerBox(AABB original) {
        return LPComponents.LIVING_PLANET.maybeGet(this)
                .filter(LivingPlanetComponent::isOutOfGround)
                .filter(LivingPlanetComponent::isOutOfGround)
                .isPresent()
                ? original.inflate(50.0, 50.0, 50.0)
                : original;
    }
}
