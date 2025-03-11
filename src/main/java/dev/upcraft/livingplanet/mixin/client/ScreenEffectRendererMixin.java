package dev.upcraft.livingplanet.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
    @WrapOperation(method = "getViewBlockingState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isViewBlocking(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"))
    private static boolean lp$dontBlockView(BlockState instance, BlockGetter blockGetter, BlockPos blockPos, Operation<Boolean> original, Player player) {
        LivingPlanetComponent cmp = LPComponents.LIVING_PLANET.get(player);
        if (cmp.isLivingPlanet() && (cmp.isPhasing() || !cmp.isOutOfGround()) && !(instance.is(Blocks.WATER))) {
            return false;
        }
        return original.call(instance, blockGetter, blockPos);
    }
}
