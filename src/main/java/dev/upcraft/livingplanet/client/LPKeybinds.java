package dev.upcraft.livingplanet.client;

import dev.upcraft.livingplanet.LivingPlanet;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.net.PhaseThroughWallPacket;
import dev.upcraft.livingplanet.net.ShockwavePacket;
import dev.upcraft.livingplanet.net.ToggleFormPacket;
import dev.upcraft.sparkweave.api.entrypoint.ClientEntryPoint;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class LPKeybinds implements ClientEntryPoint {

    public static final String CATEGORY_NAME = Util.makeDescriptionId("key.category", LivingPlanet.id("abilities"));

    public static final KeyMapping TOGGLE_FORM = create("toggle_form", GLFW.GLFW_KEY_G);
    public static final KeyMapping ABILITY_SHOCKWAVE = create("ability_shockwave", GLFW.GLFW_KEY_R);
    public static final KeyMapping ABILITY_PHASE = create("ability_phase", GLFW.GLFW_KEY_V);

    private static KeyMapping create(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(Util.makeDescriptionId("key", LivingPlanet.id(name)), key, CATEGORY_NAME));
    }

    public static void tickKeyMappings(Minecraft client) {
        processKeybind(LPKeybinds.TOGGLE_FORM, client, LPKeybinds::onToggleForm);
        processKeybind(LPKeybinds.ABILITY_PHASE, client, LPKeybinds::onAbiltiyPhase);
        processKeybind(LPKeybinds.ABILITY_SHOCKWAVE, client, LPKeybinds::onAbilityShockwave);
    }

    private static void processKeybind(KeyMapping keyMapping, Minecraft client, Runnable action) {
        if(keyMapping.consumeClick() && client.level != null && client.player != null && client.player.getComponent(LPComponents.LIVING_PLANET).isLivingPlanet()) {
            action.run();
        }
    }

    private static void onToggleForm() {
        ClientPlayNetworking.send(ToggleFormPacket.INSTANCE);
    }

    private static void onAbiltiyPhase() {
        ClientPlayNetworking.send(PhaseThroughWallPacket.INSTANCE);
    }

    private static void onAbilityShockwave() {
        ClientPlayNetworking.send(ShockwavePacket.INSTANCE);
    }

    @Override
    public void onInitializeClient(ModContainer mod) {
        // NO-OP
    }
}
