package dev.upcraft.livingplanet.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class CameraMixin {
    @WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getScale()F"))
    private float planetlordbiomes$desert$zoomOutWhenBurrowed(LivingEntity instance, Operation<Float> original) {
        float res = original.call(instance);
        if (LPComponents.LIVING_PLANET.maybeGet(instance).filter(LivingPlanetComponent::isLivingPlanet).isPresent()) {
            res *= 4;
        }

        return res;
    }
}
