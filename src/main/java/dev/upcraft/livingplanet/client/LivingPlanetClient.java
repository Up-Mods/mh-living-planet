package dev.upcraft.livingplanet.client;

import dev.upcraft.sparkweave.api.entrypoint.ClientEntryPoint;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class LivingPlanetClient implements ClientEntryPoint {

    @Override
    public void onInitializeClient(ModContainer mod) {
        ClientTickEvents.START_CLIENT_TICK.register(LPKeybinds::tickKeyMappings);
    }
}
