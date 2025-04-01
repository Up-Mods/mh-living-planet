package dev.upcraft.livingplanet.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static dev.upcraft.livingplanet.LivingPlanet.id;

public class LPTags {
    public static final TagKey<Block> LIVING_PLANET_BLOCKS = TagKey.create(Registries.BLOCK, id("living_planet_blocks"));
    public static final TagKey<Block> LP_DESTROYABLE_BLOCKS = TagKey.create(Registries.BLOCK, id("living_planet_destroyable_blocks"));

    public static void init() {}
}
