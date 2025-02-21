package dev.upcraft.livingplanet.mixin.client;

import dev.upcraft.livingplanet.component.LPComponents;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void planetlordbiomes$desert$dontRenderBurrowed(E entity, Frustum frustum, double d, double e, double f, CallbackInfoReturnable<Boolean> cir) {
        if (LPComponents.LIVING_PLANET.isProvidedBy(entity) && !LPComponents.LIVING_PLANET.get(entity).isOutOfGround()) {
            cir.setReturnValue(false);
        }
    }
}
