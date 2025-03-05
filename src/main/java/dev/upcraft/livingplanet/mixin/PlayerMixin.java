package dev.upcraft.livingplanet.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import dev.upcraft.livingplanet.util.DummyFoodData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    private PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        throw new UnsupportedOperationException();
    }

    @WrapMethod(method = "getDefaultDimensions")
    private EntityDimensions lp$overrideDefaultDimensions(Pose pose, Operation<EntityDimensions> original) {
        var planet = this.getComponent(LPComponents.LIVING_PLANET);
        if(planet.isLivingPlanet()) {
            if(!planet.isOutOfGround()) {
                return LivingPlanetComponent.IN_GROUND_DIMENSIONS;
            }
            else {
                return LivingPlanetComponent.OUT_OF_GROUND_DIMENSIONS;
            }
        }

        return original.call(pose);
    }

    @WrapWithCondition(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private boolean lp$onDamageTaken(Player instance, float exhaustion, @Share("planet_component") LocalRef<LivingPlanetComponent> componentStore) {
        var component = this.getComponent(LPComponents.LIVING_PLANET);
        componentStore.set(component);
        return !component.isLivingPlanet();
    }

    @WrapWithCondition(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setHealth(F)V"))
    private boolean lp$onSetHealth(Player instance, float healthMinusFinalDamage, @Share("planet_component") LocalRef<LivingPlanetComponent> componentStore) {
        var component = componentStore.get();
        if(component.isLivingPlanet()) {
            if(!component.isImmobilized()) {
                var finalDamage = -(healthMinusFinalDamage - this.getHealth());
                component.damage(finalDamage);
                component.sync();
            }
            return false;
        }

        return true;
    }

    @ModifyReturnValue(method = "getFoodData", at = @At("RETURN"))
    private FoodData lp$dummyFoodData(FoodData original) {
        if(this.getComponent(LPComponents.LIVING_PLANET).isLivingPlanet()) {
            return DummyFoodData.INSTANCE;
        }

        return original;
    }

}
