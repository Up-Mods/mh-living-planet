package dev.upcraft.livingplanet;

import dev.upcraft.livingplanet.command.LPCommands;
import dev.upcraft.livingplanet.entity.PlanetEntity;
import dev.upcraft.livingplanet.init.LPEntityTypes;
import dev.upcraft.sparkweave.api.entrypoint.MainEntryPoint;
import dev.upcraft.sparkweave.api.logging.SparkweaveLoggerFactory;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import dev.upcraft.sparkweave.api.platform.services.RegistryService;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class LivingPlanet implements MainEntryPoint {

	public static final String MODID = "living_planet";
	public static final Logger LOGGER = SparkweaveLoggerFactory.getLogger();

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Hello Fabric world!");
		LPCommands.register();

		var registryService = RegistryService.get();
		LPEntityTypes.ENTITY_TYPES.accept(registryService);

		FabricDefaultAttributeRegistry.register(LPEntityTypes.PLANET.get(), PlanetEntity.createAttributes());
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
