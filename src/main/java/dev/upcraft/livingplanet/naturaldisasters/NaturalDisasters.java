package dev.upcraft.livingplanet.naturaldisasters;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static dev.upcraft.livingplanet.LivingPlanet.id;

public class NaturalDisasters {
    public static final ResourceKey<Registry<NaturalDisasterType<?>>> NATURAL_DISASTER_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(id("natural_disasters"));
    public static final Registry<NaturalDisasterType<?>> NATURAL_DISASTER_TYPES = FabricRegistryBuilder.createSimple(NATURAL_DISASTER_TYPE_REGISTRY_KEY)
            .buildAndRegister();

    public static final NaturalDisasterType<LightningStorm> LIGHTNING_STORM = Registry.register(NATURAL_DISASTER_TYPES, id("lightning_storm"), LightningStorm.TYPE);
    public static final NaturalDisasterType<Earthquake> EARTHQUAKE = Registry.register(NATURAL_DISASTER_TYPES, id("earthquake"), Earthquake.TYPE);

    public static final Codec<NaturalDisaster> DISASTER_CODEC = NATURAL_DISASTER_TYPES.byNameCodec().dispatch(NaturalDisaster::type, NaturalDisasterType::codec);

    public static void init() {
    }
}
