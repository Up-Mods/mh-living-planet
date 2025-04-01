package dev.upcraft.livingplanet;

import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueCategory;

import java.util.function.Function;

import static dev.upcraft.livingplanet.LivingPlanet.id;

public class LPOptions {
    public static final MValueCategory CATEGORY = new MValueCategory(id(LivingPlanet.MODID), Items.STONE.getDefaultInstance(), ResourceLocation.withDefaultNamespace("textures/block/dirt.png"));
    public static final MValueCategory NATURAL_DISASTERS_CATEGORY = new MValueCategory(id("natural_disasters"), Items.FIRE_CHARGE.getDefaultInstance(), ResourceLocation.withDefaultNamespace("textures/block/magma.png"));
    public static final MValue.MValueFloat SCALE_HORIZONTAL = new ScaleMValueFloat(LPOptions.CATEGORY, LivingPlanet.id("lp_scale_horizontal"), $ -> Items.PLAYER_HEAD.getDefaultInstance(), 1f, 0.2f, 4.5f);
    public static final MValue.MValueFloat SCALE_VERTICAL = new ScaleMValueFloat(LPOptions.CATEGORY, LivingPlanet.id("lp_scale_vertical"), $ -> Items.PLAYER_HEAD.getDefaultInstance(), 1f, 0.2f, 4.5f);
    public static final MValue.MValueFloat SHOCKWAVE_DAMAGE = MValue.ofFloat(CATEGORY, id("shockwave_damage"), Items.MACE.getDefaultInstance(), 5f, 1f, 20f);
    public static final MValue.MValueInteger SHOCKWAVE_COOLDOWN_SECONDS = MValue.ofInteger(CATEGORY, id("shockwave_cooldown"), Items.CLOCK.getDefaultInstance(), 10, 0, 600);
    public static final MValue.MValueInteger LIGHTNING_COOLDOWN_SECONDS = MValue.ofInteger(CATEGORY, id("lightning_cooldown"), Items.CLOCK.getDefaultInstance(), 600, 0, 600);
    public static final MValue.MValueInteger LIGHTNING_DURATION_SECONDS = MValue.ofInteger(NATURAL_DISASTERS_CATEGORY, id("lightning_duration"), Items.CLOCK.getDefaultInstance(), 30, 1, 300);
    public static final MValue.MValueInteger LIGHTNING_STORM_INVERSE_INTENSITY = new MValue.MValueInteger(NATURAL_DISASTERS_CATEGORY, id("lightning_storm/inverse_intensity"), $ -> Items.LIGHTNING_ROD.getDefaultInstance(), 300, 1, 500);
    public static final MValue.MValueInteger CHASMS_COOLDOWN_SECONDS = MValue.ofInteger(CATEGORY, id("chasms_cooldown"), Items.CLOCK.getDefaultInstance(), 600, 0, 600);
    public static final MValue.MValueInteger EARTHQUAKE_MIN_HOLE_COUNT = new MValue.MValueInteger(NATURAL_DISASTERS_CATEGORY, id("earthquake/min_hole_count"), $ -> Items.CRACKED_STONE_BRICKS.getDefaultInstance(), 9, 1, 20);
    public static final MValue.MValueInteger EARTHQUAKE_MAX_HOLE_COUNT = new MValue.MValueInteger(NATURAL_DISASTERS_CATEGORY, id("earthquake/max_hole_count"), $ -> Items.CRACKED_STONE_BRICKS.getDefaultInstance(), 20, 3, 30);
    public static final MValue.MValueInteger RAGE_COOLDOWN_SECONDS = MValue.ofInteger(CATEGORY, id("rage_cooldown"), Items.CLOCK.getDefaultInstance(), 600, 0, 600);
    public static final MValue.MValueFloat THROWN_ROCK_DAMAGE = MValue.ofFloat(CATEGORY, id("thrown_rock_damage"), Items.STONE.getDefaultInstance(), 3f, 1f, 20f);

    public static void init() {}

    private static class ScaleMValueFloat extends MValue.MValueFloat {
        public ScaleMValueFloat(MValueCategory category, ResourceLocation lpWidth, Function<MValue<Float>, ItemStack> icon, float defaultVal, float min, float max) {
            super(category, lpWidth, icon, defaultVal, min, max);
        }

        @Override
        public void setValue(Float value) {
            super.setValue(value);
            LivingPlanetComponent.updateDimensions();
        }

        @Override
        public void readNbt(@NotNull CompoundTag compound) {
            super.readNbt(compound);
            LivingPlanetComponent.updateDimensions();
        }
    }
}
