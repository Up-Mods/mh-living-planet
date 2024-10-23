package dev.upcraft.livingplanet.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
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

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        var planet = this.getComponent(LPComponents.LIVING_PLANET);
        if(planet.isLivingPlanet()) {
            if(!planet.isVisible()) {
                return EntityDimensions.fixed(1.0F, 1.0F);
            }
            else {
                return EntityDimensions.fixed(1.0F, 2.0F);
            }
        }

        return super.getDimensions(pose);
    }

    @WrapWithCondition(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private boolean onDamageTaken(Player instance, float exhaustion, @Share("planet_component") LocalRef<LivingPlanetComponent> componentStore) {
        var component = getComponent(LPComponents.LIVING_PLANET);
        componentStore.set(component);
        if(component.isLivingPlanet()) {
            return false;
        }
        return true;
    }

    @WrapWithCondition(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setHealth(F)V"))
    private boolean onSetHealth(Player instance, float healthMinusFinalDamage, @Share("planet_component") LocalRef<LivingPlanetComponent> componentStore) {
        var component = componentStore.get();
        if(component.isLivingPlanet()) {
            if(!component.isImmobilized()) {
                var finalDamage = -(healthMinusFinalDamage - getHealth());
                component.damage(finalDamage);
                component.sync();
            }
            return false;
        }

        return true;
    }

    @ModifyReturnValue(method = "getFoodData", at = @At("RETURN"))
    private FoodData dummyFoodData(FoodData original) {
        if(this.getComponent(LPComponents.LIVING_PLANET).isLivingPlanet()) {
            return DummyFoodData.INSTANCE;
        }

        return original;
    }

}
