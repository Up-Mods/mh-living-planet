package dev.upcraft.livingplanet.component;

import dev.upcraft.livingplanet.LivingPlanet;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class LPComponents implements EntityComponentInitializer {

    public static final ComponentKey<LivingPlanetComponent> LIVING_PLANET = ComponentRegistry.getOrCreate(LivingPlanet.id("living_planet"), LivingPlanetComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(LIVING_PLANET, LivingPlanetComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
