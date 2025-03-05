package dev.upcraft.livingplanet.mixin;

import dev.upcraft.livingplanet.component.LPComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void lp$tick(Player player, CallbackInfo ci) {
        if (LPComponents.LIVING_PLANET.get(player).isLivingPlanet()) {
            ci.cancel();
        }
    }
}
