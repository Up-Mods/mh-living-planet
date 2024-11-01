package dev.upcraft.livingplanet;

import dev.upcraft.livingplanet.command.LPCommands;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.entity.ShockwaveBlockEntity;
import dev.upcraft.livingplanet.net.PhaseThroughWallPacket;
import dev.upcraft.livingplanet.net.ShockwavePacket;
import dev.upcraft.livingplanet.net.ToggleFormPacket;
import dev.upcraft.sparkweave.api.entrypoint.MainEntryPoint;
import dev.upcraft.sparkweave.api.logging.SparkweaveLoggerFactory;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import dev.upcraft.sparkweave.api.platform.services.RegistryService;
import dev.upcraft.sparkweave.api.registry.RegistryHandler;
import dev.upcraft.sparkweave.api.registry.RegistrySupplier;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.apache.logging.log4j.Logger;

public class LivingPlanet implements MainEntryPoint {

	public static final String MODID = "living_planet";
	public static final Logger LOGGER = SparkweaveLoggerFactory.getLogger();

	public static final RegistryHandler<EntityType<?>> ENTITY_TYPES = RegistryHandler.create(Registries.ENTITY_TYPE, MODID);
	public static final RegistrySupplier<EntityType<ShockwaveBlockEntity>> SHOCKWAVE_BLOCK = ENTITY_TYPES.register("shockwave_block", () -> EntityType.Builder.of(ShockwaveBlockEntity::new, MobCategory.MISC).sized(EntityType.FALLING_BLOCK.getWidth(), EntityType.FALLING_BLOCK.getHeight()).clientTrackingRange(EntityType.FALLING_BLOCK.clientTrackingRange()).updateInterval(EntityType.FALLING_BLOCK.updateInterval()).build(null));

	@Override
	public void onInitialize(ModContainer mod) {
		RegistryService registryService = RegistryService.get();
		LPCommands.register();
		ENTITY_TYPES.accept(registryService);

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

		ServerPlayNetworking.registerGlobalReceiver(ShockwavePacket.TYPE, (payload, context) -> {
			context.server().execute(() -> {
				ServerPlayer player = context.player();
				ServerLevel level = player.serverLevel();

				if(player.onGround())
					ShockwaveBlockEntity.newShockwave(level, player.blockPosition(), player.getViewVector(1f), 16, player.yBodyRot);
			});
		});

//		var registryService = RegistryService.get();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
