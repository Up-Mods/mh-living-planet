package dev.upcraft.livingplanet;

import dev.upcraft.livingplanet.command.LPCommands;
import dev.upcraft.livingplanet.entity.LPEntities;
import dev.upcraft.livingplanet.item.LPItems;
import dev.upcraft.livingplanet.naturaldisasters.NaturalDisasters;
import dev.upcraft.livingplanet.net.LPNetworking;
import dev.upcraft.livingplanet.particle.LPParticles;
import dev.upcraft.livingplanet.tag.LPTags;
import dev.upcraft.sparkweave.api.entrypoint.MainEntryPoint;
import dev.upcraft.sparkweave.api.logging.SparkweaveLoggerFactory;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import dev.upcraft.sparkweave.api.platform.services.RegistryService;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class LivingPlanet implements MainEntryPoint {

	public static final String MODID = "living_planet";
	public static final Logger LOGGER = SparkweaveLoggerFactory.getLogger();

	@Override
	public void onInitialize(ModContainer mod) {
		RegistryService registryService = RegistryService.get();
		LPCommands.register();
		LPEntities.ENTITY_TYPES.accept(registryService);
		LPParticles.PARTICLE_TYPES.accept(registryService);
		LPItems.ITEMS.accept(registryService);
		NaturalDisasters.init();
		LPNetworking.init();
		LPTags.init();
		LPOptions.init();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
