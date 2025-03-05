package dev.upcraft.livingplanet.item;

import dev.upcraft.livingplanet.LivingPlanet;
import dev.upcraft.livingplanet.particle.LivingPlanetTerrainParticleOption;
import dev.upcraft.sparkweave.api.registry.RegistryHandler;
import dev.upcraft.sparkweave.api.registry.RegistrySupplier;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class LPItems {
    public static final RegistryHandler<Item> ITEMS = RegistryHandler.create(Registries.ITEM, LivingPlanet.MODID);
    public static final RegistrySupplier<CompassItem> PLAYER_TRACKER_COMPASS = ITEMS.register("player_tracker_compass",
            () -> new CompassItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
}
