package dev.upcraft.livingplanet.init;

import dev.upcraft.livingplanet.LivingPlanet;
import dev.upcraft.livingplanet.entity.PlanetEntity;
import dev.upcraft.sparkweave.api.registry.RegistryHandler;
import dev.upcraft.sparkweave.api.registry.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class LPEntityTypes {

    public static final RegistryHandler<EntityType<?>> ENTITY_TYPES = RegistryHandler.create(Registries.ENTITY_TYPE, LivingPlanet.MODID);

    public static final RegistrySupplier<EntityType<PlanetEntity>> PLANET = ENTITY_TYPES.register("planet", () -> EntityType.Builder.of(PlanetEntity::new, MobCategory.MISC).sized(1.0F, 0.2F).fireImmune().noSave().noSummon().clientTrackingRange(32).updateInterval(2).build());
}
