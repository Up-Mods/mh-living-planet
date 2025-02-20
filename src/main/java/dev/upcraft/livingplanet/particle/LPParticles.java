package dev.upcraft.livingplanet.particle;

import dev.upcraft.livingplanet.LivingPlanet;
import dev.upcraft.sparkweave.api.registry.RegistryHandler;
import dev.upcraft.sparkweave.api.registry.RegistrySupplier;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;

public class LPParticles {
    public static final RegistryHandler<ParticleType<?>> PARTICLE_TYPES = RegistryHandler.create(Registries.PARTICLE_TYPE, LivingPlanet.MODID);
    public static final RegistrySupplier<ParticleType<LivingPlanetTerrainParticleOption>> BIG_TERRAIN_PARTICLE = PARTICLE_TYPES.register("big_terrain",
            () -> FabricParticleTypes.complex(LivingPlanetTerrainParticleOption::codec, LivingPlanetTerrainParticleOption::streamCodec));
}
