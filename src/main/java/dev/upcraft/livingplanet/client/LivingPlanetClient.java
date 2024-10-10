package dev.upcraft.livingplanet.client;

import dev.upcraft.livingplanet.LivingPlanet;
import dev.upcraft.livingplanet.client.renderer.PlanetEntityRenderer;
import dev.upcraft.livingplanet.init.LPEntityTypes;
import dev.upcraft.sparkweave.api.client.event.RegisterEntityRenderersEvent;
import dev.upcraft.sparkweave.api.entrypoint.ClientEntryPoint;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class LivingPlanetClient implements ClientEntryPoint {

    @Override
    public void onInitializeClient(ModContainer mod) {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(client.level != null) {
                if(LPKeybinds.TOGGLE_FORM.consumeClick()) {
                    LivingPlanet.LOGGER.info("toggle form");
                }

                if(LPKeybinds.ABILITY_PHASE.consumeClick()) {
                    LivingPlanet.LOGGER.info("phase through wall");
                }

                if(LPKeybinds.ABILITY_SHOCKWAVE.consumeClick()) {
                    LivingPlanet.LOGGER.info("shockwave");
                }
            }
        });

        RegisterEntityRenderersEvent.EVENT.register(event -> {
            event.registerRenderer(LPEntityTypes.PLANET, PlanetEntityRenderer::new);
        });
    }
}
