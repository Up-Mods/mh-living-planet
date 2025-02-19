package dev.upcraft.livingplanet;

import dev.upcraft.livingplanet.damage.LPDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.concurrent.CompletableFuture;

public class LivingPlanetData implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack  = fabricDataGenerator.createPack();
                pack.addProvider(Lang::new);
                pack.addProvider(DamageTypes::new);
                pack.addProvider(DamageTypeTags::new);
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
}
