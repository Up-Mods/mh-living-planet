package dev.upcraft.livingplanet;

import dev.upcraft.livingplanet.client.tracking.PlayerTrackingScreen;
import dev.upcraft.livingplanet.client.tracking.Tracker;
import dev.upcraft.livingplanet.damage.LPDamageTypes;
import dev.upcraft.livingplanet.entity.LPEntities;
import dev.upcraft.livingplanet.item.LPItems;
import dev.upcraft.livingplanet.net.ShockwavePacket;
import dev.upcraft.livingplanet.tag.LPTags;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class LivingPlanetData implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack  = fabricDataGenerator.createPack();
        pack.addProvider(Lang::new);
        pack.addProvider(DamageTypes::new);
        pack.addProvider(DamageTypeTags::new);
        pack.addProvider(BlockTags::new);
    }

    public static class Lang extends FabricLanguageProvider {
        protected Lang(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
            builder.add("commands.livingplanet.state.enabled", "Made %s a living planet");
            builder.add("commands.livingplanet.state.disabled", "Made %s no longer a living planet");
            builder.add("key.category.living_planet.abilities", "[MH] Living Planet");
            builder.add("key.living_planet.ability_phase", "Phase Through Wall");
            builder.add("key.living_planet.ability_shockwave", "Launch Shockwave");
            builder.add("key.living_planet.toggle_form", "Emerge From Ground");
            builder.add(Tracker.TRACKER_KEY, "Tracker");
            builder.add(PlayerTrackingScreen.DESELECT_KEY, "Deselect");
            builder.add(LPEntities.SHOCKWAVE_BLOCK.get(), "Shockwave Block");
            builder.add(LPEntities.THROWN_ROCK.get(), "Thrown Rock");
            builder.add(LPItems.PLAYER_TRACKER_COMPASS.get(), "Player Tracker Compass");
            builder.add(LPOptions.CATEGORY.getTranslationKey(), "Living Planet");
            builder.add(LPOptions.SHOCKWAVE_DAMAGE.getTranslationKey(), "Shockwave Damage");
            builder.add(LPOptions.SHOCKWAVE_DAMAGE.getDescriptionTranslationKey(), "How many half-hearts of damage a shockwave does.");
            builder.add(LPOptions.SHOCKWAVE_COOLDOWN_SECONDS.getTranslationKey(), "Shockwave Cooldown (secs)");
            builder.add(LPOptions.SHOCKWAVE_COOLDOWN_SECONDS.getDescriptionTranslationKey(), "How many seconds a player must wait between using the shockwave ability.");
            builder.add(LPOptions.THROWN_ROCK_DAMAGE.getTranslationKey(), "Thrown Rock Damage");
            builder.add(LPOptions.THROWN_ROCK_DAMAGE.getDescriptionTranslationKey(), "How many half-hearts of damage a thrown rock does.");
            builder.add(LPOptions.NATURAL_DISASTERS_CATEGORY.getTranslationKey(), "Natural Disasters");
            builder.add(LPOptions.CHASMS_COOLDOWN_SECONDS.getTranslationKey(), "Earthquakes Cooldown (secs)");
            builder.add(LPOptions.CHASMS_COOLDOWN_SECONDS.getDescriptionTranslationKey(), "How many seconds a player must wait between using the earthquakes ability");
            builder.add(LPOptions.EARTHQUAKE_MIN_HOLE_COUNT.getTranslationKey(), "Earthquake Min Chasms");
            builder.add(LPOptions.EARTHQUAKE_MIN_HOLE_COUNT.getDescriptionTranslationKey(), "Earthquake Min Chasms");
            builder.add(LPOptions.EARTHQUAKE_MAX_HOLE_COUNT.getTranslationKey(), "Earthquake Max Chasms");
            builder.add(LPOptions.EARTHQUAKE_MAX_HOLE_COUNT.getDescriptionTranslationKey(), "Earthquake Max Chasms");
            builder.add(LPOptions.LIGHTNING_COOLDOWN_SECONDS.getTranslationKey(), "Storm Cooldown (secs)");
            builder.add(LPOptions.LIGHTNING_COOLDOWN_SECONDS.getDescriptionTranslationKey(), "How many seconds a player must wait between using the storm ability");
            builder.add(LPOptions.LIGHTNING_DURATION_SECONDS.getTranslationKey(), "Storm Duration (secs)");
            builder.add(LPOptions.LIGHTNING_DURATION_SECONDS.getDescriptionTranslationKey(), "How many seconds a storm lasts");
            builder.add(LPOptions.LIGHTNING_STORM_INVERSE_INTENSITY.getTranslationKey(), "Lightning Storm Inverse Intensity");
            builder.add(LPOptions.LIGHTNING_STORM_INVERSE_INTENSITY.getDescriptionTranslationKey(), "Lightning Storm Inverse Intensity");
            builder.add(LPOptions.RAGE_COOLDOWN_SECONDS.getTranslationKey(), "Rage Cooldown (secs)");
            builder.add(LPOptions.RAGE_COOLDOWN_SECONDS.getDescriptionTranslationKey(), "How many seconds a player must wait between using the rage ability");
            builder.add(LPOptions.SCALE_HORIZONTAL.getTranslationKey(), "Horizontal Scale");
            builder.add(LPOptions.SCALE_HORIZONTAL.getDescriptionTranslationKey(), "How big the living planet is (horizontal)");
            builder.add(LPOptions.SCALE_VERTICAL.getTranslationKey(), "Vertical Scale");
            builder.add(LPOptions.SCALE_VERTICAL.getDescriptionTranslationKey(), "How big the living planet is (vertical)");
            builder.add(PlayerTrackingScreen.CHASMS_KEY, "Earthquake");
            builder.add(PlayerTrackingScreen.LIGHTNING_KEY, "Storm");
            builder.add(PlayerTrackingScreen.RAGE_KEY, "Rage");
            builder.add(ShockwavePacket.COOLDOWN_MESSAGE_KEY, "This ability is cooling down.");
        }
    }

    public static class DamageTypes extends FabricDynamicRegistryProvider {
        public DamageTypes(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(HolderLookup.Provider registries, Entries entries) {
            entries.add(LPDamageTypes.SHOCKWAVE, new DamageType(LPDamageTypes.SHOCKWAVE.location().toLanguageKey(), 0.1f));
        }

        @Override
        public String getName() {
            return "damage_types";
        }
    }

    public static class DamageTypeTags extends FabricTagProvider<DamageType> {
        /**
         * Constructs a new {@link FabricTagProvider} with the default computed path.
         *
         * <p>Common implementations of this class are provided.
         *
         * @param output           the {@link FabricDataOutput} instance
         * @param registriesFuture the backing registry for the tag type
         */
        public DamageTypeTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, Registries.DAMAGE_TYPE, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            this.tag(net.minecraft.tags.DamageTypeTags.PANIC_CAUSES)
                    .addOptional(LPDamageTypes.SHOCKWAVE.location());
        }
    }

    public static class BlockTags extends FabricTagProvider<Block> {
        /**
         * Constructs a new {@link FabricTagProvider} with the default computed path.
         *
         * <p>Common implementations of this class are provided.
         *
         * @param output           the {@link FabricDataOutput} instance
         * @param registriesFuture the backing registry for the tag type
         */
        public BlockTags(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, Registries.BLOCK, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            this.tag(net.minecraft.tags.BlockTags.OVERWORLD_CARVER_REPLACEABLES);
            this.tag(net.minecraft.tags.BlockTags.NETHER_CARVER_REPLACEABLES);
            this.tag(net.minecraft.tags.BlockTags.LEAVES);
            this.tag(net.minecraft.tags.BlockTags.LOGS);
            this.tag(net.minecraft.tags.BlockTags.PLANKS);
            this.tag(net.minecraft.tags.BlockTags.BEDS);
            this.tag(net.minecraft.tags.BlockTags.DOORS);
            this.tag(net.minecraft.tags.BlockTags.TERRACOTTA);
            this.tag(net.minecraft.tags.BlockTags.WOODEN_STAIRS);
            this.tag(net.minecraft.tags.BlockTags.WOODEN_FENCES);
            this.tag(net.minecraft.tags.BlockTags.WALLS);
            this.tag(net.minecraft.tags.BlockTags.SLABS);
            this.tag(ConventionalBlockTags.GLASS_PANES);
            this.tag(ConventionalBlockTags.CHESTS);
            this.tag(ConventionalBlockTags.COBBLESTONES);
            this.tag(LPTags.LP_DESTROYABLE_BLOCKS)
                    .addTag(net.minecraft.tags.BlockTags.LEAVES)
                    .addTag(net.minecraft.tags.BlockTags.LOGS)
                    .addTag(net.minecraft.tags.BlockTags.PLANKS)
                    .addTag(ConventionalBlockTags.GLASS_PANES)
                    .add(Blocks.IRON_BARS.builtInRegistryHolder().key())
                    .addTag(ConventionalBlockTags.CHESTS)
                    .addTag(net.minecraft.tags.BlockTags.BEDS)
                    .addTag(net.minecraft.tags.BlockTags.DOORS)
                    .addTag(net.minecraft.tags.BlockTags.TERRACOTTA)
                    .addTag(ConventionalBlockTags.COBBLESTONES)
                    .add(Blocks.BROWN_MUSHROOM_BLOCK.builtInRegistryHolder().key())
                    .add(Blocks.RED_MUSHROOM_BLOCK.builtInRegistryHolder().key())
                    .add(Blocks.MUSHROOM_STEM.builtInRegistryHolder().key())
                    .addTag(net.minecraft.tags.BlockTags.WOODEN_STAIRS)
                    .addTag(net.minecraft.tags.BlockTags.SLABS)
                    .addTag(net.minecraft.tags.BlockTags.WOODEN_FENCES)
                    .addTag(net.minecraft.tags.BlockTags.WALLS);
            this.tag(LPTags.LIVING_PLANET_BLOCKS)
                    .addTag(net.minecraft.tags.BlockTags.OVERWORLD_CARVER_REPLACEABLES)
                    .addTag(net.minecraft.tags.BlockTags.NETHER_CARVER_REPLACEABLES)
                    .add(Blocks.GRASS_BLOCK.builtInRegistryHolder().key(), Blocks.MYCELIUM.builtInRegistryHolder().key(), Blocks.SNOW_BLOCK.builtInRegistryHolder().key(), Blocks.END_STONE.builtInRegistryHolder().key());
        }
    }
}
