package dev.upcraft.livingplanet.client;

import dev.upcraft.livingplanet.client.render.ShockwaveBlockRenderer;
import dev.upcraft.livingplanet.entity.LPEntities;
import dev.upcraft.livingplanet.particle.LPParticles;
import dev.upcraft.sparkweave.api.client.event.RegisterEntityRenderersEvent;
import dev.upcraft.sparkweave.api.entrypoint.ClientEntryPoint;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class LivingPlanetClient implements ClientEntryPoint {

    @Override
    public void onInitializeClient(ModContainer mod) {
        ClientTickEvents.START_CLIENT_TICK.register(LPKeybinds::tickKeyMappings);
        RegisterEntityRenderersEvent.EVENT.register(event -> event.registerRenderer(LPEntities.SHOCKWAVE_BLOCK, ShockwaveBlockRenderer::new));
        ParticleFactoryRegistry.getInstance().register(LPParticles.BIG_TERRAIN_PARTICLE.get(), new BigTerrainParticle.Provider());
    }
}
