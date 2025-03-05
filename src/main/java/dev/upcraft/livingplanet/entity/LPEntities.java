package dev.upcraft.livingplanet.entity;

import dev.upcraft.livingplanet.LivingPlanet;
import dev.upcraft.sparkweave.api.registry.RegistryHandler;
import dev.upcraft.sparkweave.api.registry.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class LPEntities {
    public static final RegistryHandler<EntityType<?>> ENTITY_TYPES = RegistryHandler.create(Registries.ENTITY_TYPE, LivingPlanet.MODID);
    public static final RegistrySupplier<EntityType<ShockwaveBlockEntity>> SHOCKWAVE_BLOCK = ENTITY_TYPES.register("shockwave_block",
            () -> EntityType.Builder.<ShockwaveBlockEntity>of(ShockwaveBlockEntity::new, MobCategory.MISC)
            .sized(0.98F, 0.98F)
            .clientTrackingRange(10)
            .updateInterval(20)
            .build());

    public static final RegistrySupplier<EntityType<ThrownRock>> THROWN_ROCK = ENTITY_TYPES.register("thrown_rock",
            () -> EntityType.Builder.<ThrownRock>of(ThrownRock::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build());
}
