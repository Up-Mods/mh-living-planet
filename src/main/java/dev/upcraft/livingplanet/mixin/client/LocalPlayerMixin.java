package dev.upcraft.livingplanet.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import dev.upcraft.livingplanet.component.LPComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    private LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
        throw new UnsupportedOperationException();
    }

    @WrapOperation(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isControlledCamera()Z"))
    private boolean lp$cancelMovement(LocalPlayer instance, Operation<Boolean> original) {
        if(!original.call(instance)) {
            return false;
        }

        var component = instance.getComponent(LPComponents.LIVING_PLANET);
        return !component.isLivingPlanet() || !component.isImmobilized();
    }
}
