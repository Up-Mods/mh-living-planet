package dev.upcraft.livingplanet.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Mob.class)
public class MobMixin {
    @ModifyReturnValue(method = "createMobAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder living_planet$allAttack(AttributeSupplier.Builder original) {
        return original.add(Attributes.ATTACK_DAMAGE);
    }
}
