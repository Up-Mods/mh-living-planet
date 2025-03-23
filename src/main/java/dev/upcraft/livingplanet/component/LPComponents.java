package dev.upcraft.livingplanet.component;

import dev.upcraft.livingplanet.LivingPlanet;
import dev.upcraft.livingplanet.naturaldisasters.NaturalDisastersComponent;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

import static dev.upcraft.livingplanet.LivingPlanet.id;

public class LPComponents implements EntityComponentInitializer, WorldComponentInitializer {

    public static final ComponentKey<LivingPlanetComponent> LIVING_PLANET = ComponentRegistry.getOrCreate(LivingPlanet.id("living_planet"), LivingPlanetComponent.class);
    public static final ComponentKey<NaturalDisastersComponent> NATURAL_DISASTERS = ComponentRegistry.getOrCreate(id("natural_disasters"), NaturalDisastersComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(LIVING_PLANET, LivingPlanetComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(NATURAL_DISASTERS, NaturalDisastersComponent::new);
    }
}
