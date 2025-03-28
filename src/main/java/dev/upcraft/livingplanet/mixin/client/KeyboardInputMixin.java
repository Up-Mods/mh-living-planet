package dev.upcraft.livingplanet.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.upcraft.livingplanet.component.LPComponents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void lp$checkIsImmobilised(boolean isSneaking, float sneakingSpeedMultiplier, CallbackInfo ci, @Share("isImmobilised") LocalRef<Boolean> isImmobilised) {
        var player = Minecraft.getInstance().player;
        isImmobilised.set(player != null && LPComponents.LIVING_PLANET.get(player).isImmobilized());
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z"))
    private boolean lp$ignoreIfImmobilised(KeyMapping instance, Operation<Boolean> original, @Share("isImmobilised") LocalRef<Boolean> isImmobilised) {
        if (isImmobilised.get()) {
            return false;
        }
        return original.call(instance);
    }
}
