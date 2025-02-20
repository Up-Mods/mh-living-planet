package dev.upcraft.livingplanet.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.upcraft.livingplanet.LivingPlanet;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record LivingPlanetTerrainParticleOption(ParticleType<LivingPlanetTerrainParticleOption> particleType, BlockState blockState, int player) implements ParticleOptions {
    private static final Codec<BlockState> BLOCK_STATE_CODEC = Codec.withAlternative(
            BlockState.CODEC, BuiltInRegistries.BLOCK.byNameCodec(), Block::defaultBlockState);

    public static MapCodec<LivingPlanetTerrainParticleOption> codec(ParticleType<LivingPlanetTerrainParticleOption> particleType) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                BLOCK_STATE_CODEC.fieldOf("block_state").forGetter(LivingPlanetTerrainParticleOption::blockState),
                Codec.INT.fieldOf("player_id").forGetter(LivingPlanetTerrainParticleOption::player)
			).apply(instance, (bs, pi) -> new LivingPlanetTerrainParticleOption(particleType, bs, pi)));
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, LivingPlanetTerrainParticleOption> streamCodec(ParticleType<LivingPlanetTerrainParticleOption> particleType) {
        return StreamCodec.composite(
                ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), LivingPlanetTerrainParticleOption::blockState,
                ByteBufCodecs.VAR_INT, LivingPlanetTerrainParticleOption::player,
                (bs, pi) -> new LivingPlanetTerrainParticleOption(particleType, bs, pi));
    }

    @Override
    public ParticleType<?> getType() {
        return this.particleType;
    }
}
