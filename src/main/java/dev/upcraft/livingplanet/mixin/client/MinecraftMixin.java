package dev.upcraft.livingplanet.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.upcraft.livingplanet.client.rockthrow.RockThrow;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow @Final public GameRenderer gameRenderer;

    @Shadow @Nullable public ClientLevel level;

    @Shadow @Nullable public LocalPlayer player;

    @Inject(method = "startUseItem", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void lp$startRockThrow(CallbackInfo ci, InteractionHand[] var1, int var2, int var3) {
        if (RockThrow.canStart(this.player)) {
            RockThrow.start(Objects.requireNonNull(this.level));
            ci.cancel();
        }
    }

    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;releaseUsingItem(Lnet/minecraft/world/entity/player/Player;)V"))
    private void lp$endRockThrow(MultiPlayerGameMode instance, Player player, Operation<Void> original) {
        if (RockThrow.isThrowing()) {
            RockThrow.stop(Objects.requireNonNull(this.level));
        } else {
            original.call(instance, player);
        }
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isHandsBusy()Z"), cancellable = true)
    private void lp$noAttacking(CallbackInfoReturnable<Boolean> cir) {
        LivingPlanetComponent cmp = LPComponents.LIVING_PLANET.get(Objects.requireNonNull(this.player));
        if (cmp.isLivingPlanet() && !cmp.isOutOfGround()) {
            cir.setReturnValue(false);
        }
    }
}
