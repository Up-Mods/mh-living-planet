package dev.upcraft.livingplanet;

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
            builder.add(LPEntities.SHOCKWAVE_BLOCK.get(), "Shockwave Block");
            builder.add(LPItems.PLAYER_TRACKER_COMPASS.get(), "Player Tracker Compass");
            builder.add(LPOptions.CATEGORY.getTranslationKey(), "Living Planet");
            builder.add(LPOptions.SHOCKWAVE_DAMAGE.getTranslationKey(), "Shockwave Damage");
            builder.add(LPOptions.SHOCKWAVE_DAMAGE.getDescriptionTranslationKey(), "How many half-hearts of damage a shockwave does.");
            builder.add(LPOptions.SHOCKWAVE_COOLDOWN_SECONDS.getTranslationKey(), "Shockwave Cooldown (secs)");
            builder.add(LPOptions.SHOCKWAVE_COOLDOWN_SECONDS.getDescriptionTranslationKey(), "How many seconds a player must wait between using the shockwave ability.");
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
            this.tag(LPTags.LIVING_PLANET_BLOCKS)
                    .addTag(net.minecraft.tags.BlockTags.OVERWORLD_CARVER_REPLACEABLES)
                    .addTag(net.minecraft.tags.BlockTags.NETHER_CARVER_REPLACEABLES)
                    .add(Blocks.GRASS_BLOCK.builtInRegistryHolder().key(), Blocks.MYCELIUM.builtInRegistryHolder().key(), Blocks.SNOW_BLOCK.builtInRegistryHolder().key(), Blocks.END_STONE.builtInRegistryHolder().key());
        }
    }
}
