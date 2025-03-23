package dev.upcraft.livingplanet.mixin.client.naturaldisasters;

import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.naturaldisasters.NaturalDisasters;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void move(float zoom, float dy, float dx);

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V", shift = At.Shift.AFTER))
    private void naturaldisasters$shakeCamera(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float f, CallbackInfo ci) {
        double strength = LPComponents.NATURAL_DISASTERS.get(entity.level()).getDisasterStrength(NaturalDisasters.EARTHQUAKE, e -> e.getStrengthAt(entity.getEyePosition(f)));
        if (strength > 0) {
            this.move((float) (entity.level().random.nextGaussian() * 0.005 * strength), (float) (entity.level().random.nextGaussian() * 0.04 * strength), (float) (entity.level().random.nextGaussian() * 0.01 * strength));
        }
    }
}
