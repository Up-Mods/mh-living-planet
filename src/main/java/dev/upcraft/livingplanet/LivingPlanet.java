package dev.upcraft.livingplanet;

import dev.upcraft.livingplanet.command.LPCommands;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.net.PhaseThroughWallPacket;
import dev.upcraft.livingplanet.net.ShockwavePacket;
import dev.upcraft.livingplanet.net.ToggleFormPacket;
import dev.upcraft.sparkweave.api.entrypoint.MainEntryPoint;
import dev.upcraft.sparkweave.api.logging.SparkweaveLoggerFactory;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class LivingPlanet implements MainEntryPoint {

	public static final String MODID = "living_planet";
	public static final Logger LOGGER = SparkweaveLoggerFactory.getLogger();

	@Override
	public void onInitialize(ModContainer mod) {
		LPCommands.register();

		PayloadTypeRegistry.playC2S().register(ToggleFormPacket.TYPE, ToggleFormPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(PhaseThroughWallPacket.TYPE, PhaseThroughWallPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(ShockwavePacket.TYPE, ShockwavePacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ToggleFormPacket.TYPE, (payload, context) -> {
			context.server().execute(() -> {
				var component = context.player().getComponent(LPComponents.LIVING_PLANET);
				component.setVisible(!component.isVisible());
				component.sync();
				context.player().refreshDimensions();
			});
		});

//		var registryService = RegistryService.get();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
