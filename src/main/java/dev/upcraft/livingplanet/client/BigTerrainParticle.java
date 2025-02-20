package dev.upcraft.livingplanet.client;

import dev.upcraft.livingplanet.particle.LivingPlanetTerrainParticleOption;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BigTerrainParticle extends TerrainParticle {
    private final BlockState state;
    private final @Nullable Entity player;

    protected BigTerrainParticle(ClientLevel clientLevel,
                                 double x, double y, double z,
                                 BlockState state, @Nullable Entity player) {
		super(clientLevel,
                // pos
                x, y, z,
                // vel
                0, 0.2, 0,
                // blockstate
                state);
        this.state = state;
        this.player = player;
        //this.hasPhysics = false;
        this.lifetime = 40;
        this.quadSize *= 4;
        if (this.shouldHideIfInView()) {
            this.setAlpha(0f);
        }
	}

    private boolean shouldHideIfInView() {
        return this.player != null && this.player == Minecraft.getInstance().cameraEntity && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.shouldHideIfInView()) {
            if (this.age < 10) {
                this.setAlpha(0f);
            } else if (this.age < 20) {
                this.setAlpha((float) (this.age - 10) / 40);
            } else {
                this.setAlpha(0.5f);
            }
        } else {
            this.setAlpha(1f);
        }
    }

    @Override
    public int getLightColor(float f) {
        BlockPos blockPos = BlockPos.containing(this.x, this.y, this.z);
        return this.level.hasChunkAt(blockPos) ? LevelRenderer.getLightColor(this.level, this.state, blockPos) : super.getLightColor(f);
    }

    public static class Provider implements ParticleProvider<LivingPlanetTerrainParticleOption> {
        @Override
        public Particle createParticle(LivingPlanetTerrainParticleOption option, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
            var player = clientLevel.getEntity(option.player());
            return new BigTerrainParticle(clientLevel, x, y, z, option.blockState(), player);
        }
    }
}
