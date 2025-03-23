package dev.upcraft.livingplanet;

import ca.weblite.objc.Proxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueCategory;

import java.util.function.Supplier;

import static dev.upcraft.livingplanet.LivingPlanet.id;

public class LPOptions {
    public static final MValueCategory CATEGORY = new MValueCategory(id(LivingPlanet.MODID), Items.STONE.getDefaultInstance(), ResourceLocation.withDefaultNamespace("textures/block/dirt.png"));
    public static final MValue.MValueFloat SHOCKWAVE_DAMAGE = MValue.ofFloat(CATEGORY, id("shockwave_damage"), Items.MACE.getDefaultInstance(), 5f, 1f, 20f);
    public static final MValue.MValueInteger SHOCKWAVE_COOLDOWN_SECONDS = MValue.ofInteger(CATEGORY, id("shockwave_cooldown"), Items.CLOCK.getDefaultInstance(), 10, 0, 600);
    public static final MValue.MValueInteger LIGHTNING_COOLDOWN_SECONDS = MValue.ofInteger(CATEGORY, id("lightning_cooldown"), Items.CLOCK.getDefaultInstance(), 600, 0, 600);
    public static final MValue.MValueInteger LIGHTNING_DURATION_SECONDS = MValue.ofInteger(CATEGORY, id("lightning_duration"), Items.CLOCK.getDefaultInstance(), 30, 1, 300);
    public static final MValue.MValueInteger CHASMS_COOLDOWN_SECONDS = MValue.ofInteger(CATEGORY, id("chasms_cooldown"), Items.CLOCK.getDefaultInstance(), 600, 0, 600);
    public static final MValue.MValueInteger GETHISASS_COOLDOWN_SECONDS = MValue.ofInteger(CATEGORY, id("get_his_ass_cooldown"), Items.CLOCK.getDefaultInstance(), 600, 0, 600);
    public static final MValue.MValueInteger GETHISASS_DURATION_SECONDS = MValue.ofInteger(CATEGORY, id("get_his_ass_duration"), Items.CLOCK.getDefaultInstance(), 60, 0, 600);
    public static final MValue.MValueFloat THROWN_ROCK_DAMAGE = MValue.ofFloat(CATEGORY, id("thrown_rock_damage"), Items.STONE.getDefaultInstance(), 3f, 1f, 20f);

    public static void init() {}
}
