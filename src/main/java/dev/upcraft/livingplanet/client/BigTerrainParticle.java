package dev.upcraft.livingplanet.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.state.BlockState;

public class BigTerrainParticle extends TerrainParticle {
    private final BlockState state;

    protected BigTerrainParticle(ClientLevel clientLevel,
                                 double x, double y, double z,
                                 BlockState state) {
		super(clientLevel,
                // pos
                x, y, z,
                // vel
                0, 0.2, 0,
                // blockstate
                state);
        this.state = state;
        //this.hasPhysics = false;
        this.lifetime = 40;
        this.quadSize *= 4;
	}

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public int getLightColor(float f) {
        BlockPos blockPos = BlockPos.containing(this.x, this.y, this.z);
        return this.level.hasChunkAt(blockPos) ? LevelRenderer.getLightColor(this.level, this.state, blockPos) : super.getLightColor(f);
    }

    public static class Provider implements ParticleProvider<BlockParticleOption> {
        @Override
        public Particle createParticle(BlockParticleOption option, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
            return new BigTerrainParticle(clientLevel, x, y, z, option.getState());
        }
    }
}
