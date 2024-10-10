package dev.upcraft.livingplanet.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class LPCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LivingPlanetCommand.register(dispatcher);
        });
    }
}
