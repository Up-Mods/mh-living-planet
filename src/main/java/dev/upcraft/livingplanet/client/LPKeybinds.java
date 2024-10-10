package dev.upcraft.livingplanet.client;

import dev.upcraft.livingplanet.LivingPlanet;
import dev.upcraft.sparkweave.api.entrypoint.ClientEntryPoint;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class LPKeybinds implements ClientEntryPoint {

    public static final String CATEGORY_NAME = Util.makeDescriptionId("key.category", LivingPlanet.id("abilities"));

    public static final KeyMapping TOGGLE_FORM = create("toggle_form", GLFW.GLFW_KEY_G);
    public static final KeyMapping ABILITY_SHOCKWAVE = create("ability_shockwave", GLFW.GLFW_KEY_R);
    public static final KeyMapping ABILITY_PHASE = create("ability_phase", GLFW.GLFW_KEY_V);

    private static KeyMapping create(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(Util.makeDescriptionId("key", LivingPlanet.id(name)), key, CATEGORY_NAME));
    }

    @Override
    public void onInitializeClient(ModContainer mod) {
        // NO-OP
    }
}
