package dev.upcraft.livingplanet.component;

import dev.upcraft.livingplanet.particle.LPParticles;
import dev.upcraft.livingplanet.particle.LivingPlanetTerrainParticleOption;
import dev.upcraft.livingplanet.util.SurroundingBlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

public class LivingPlanetComponent implements Component, AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {

    private static final int DEFAULT_IMMOBILIZED_TIME = 20 * 120;
    private static final int DEFAULT_SHOCKWAVE_COOLDOWN = 20 * 120;
    private static final float MAX_HEALTH = 100.0F;

    private final Player player;

    /**
     * whether the player currently is a living planet
     */
    private boolean livingPlanet = false;

    /**
     * whether the player is currently visible (= above ground) or not
     */
    private boolean visible = false;

    private int immobilizedTicks = 0;
    private int shockwaveCooldownTicks = 0;

    private float health;
    private boolean phasing = false;

    public LivingPlanetComponent(Player player) {
        this.player = player;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setLivingPlanet(boolean livingPlanet) {
        this.livingPlanet = livingPlanet;
    }

    public boolean isLivingPlanet() {
        return this.livingPlanet;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getHealth() {
        return this.health;
    }

    public float getMaxHealth() {
        return MAX_HEALTH;
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        this.livingPlanet = tag.getBoolean("active");
        this.visible = tag.getBoolean("visible");
        this.immobilizedTicks = tag.getInt("immobilizedTicks");
        this.health = tag.getFloat("health");
        this.phasing = tag.getBoolean("phasing");
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        tag.putBoolean("active", this.livingPlanet);
        tag.putBoolean("visible", this.visible);
        tag.putInt("immobilizedTicks", this.immobilizedTicks);
        tag.putFloat("health", this.health);
        tag.putBoolean("phasing", this.phasing);
    }

    @Override
    public void serverTick() {
        if(this.immobilizedTicks > 0) {
            this.immobilizedTicks--;

            if(this.immobilizedTicks <= 0) {
                this.resetHealth();
                this.sync();
            }
        }
        if (this.shockwaveCooldownTicks > 0) {
            this.shockwaveCooldownTicks--;
            if (this.shockwaveCooldownTicks <= 0) {
                this.sync();
            }
        }
    }

    @Override
    public void clientTick() {
        this.updateSurroundings();
        if (this.isVisible()) {
            var random = this.player.getRandom();
            for (int i = 0; i < 20; i++) {
                var particle = new LivingPlanetTerrainParticleOption(LPParticles.BIG_TERRAIN_PARTICLE.get(), this.getRandomState(random::nextInt), this.player.getId());
                double y = 0.3+random.nextGaussian()*Math.min(1, this.player.getBbHeight());
                double yDist = (this.player.getBbHeight()-y)/3.0;
                double displacement = yDist*yDist*(0.2+random.nextFloat()*0.8)+1;
                double theta = random.nextGaussian()*Math.PI*2;
                Vec3 pos = this.player.position().add(Math.sin(theta)*displacement, y, Math.cos(theta)*displacement);
                this.player.level().addParticle(particle, pos.x(), pos.y(), pos.z(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void writeSyncPacket(RegistryFriendlyByteBuf buf, ServerPlayer recipient) {
        ByteBufCodecs.BOOL.encode(buf, this.livingPlanet);
        ByteBufCodecs.BOOL.encode(buf, this.visible);
        ByteBufCodecs.VAR_INT.encode(buf, this.shockwaveCooldownTicks);
        ByteBufCodecs.VAR_INT.encode(buf, this.immobilizedTicks);
        ByteBufCodecs.FLOAT.encode(buf, this.health);
        ByteBufCodecs.BOOL.encode(buf, this.phasing);
    }

    @Override
    public void applySyncPacket(RegistryFriendlyByteBuf buf) {
        this.livingPlanet = ByteBufCodecs.BOOL.decode(buf);
        this.visible = ByteBufCodecs.BOOL.decode(buf);
        this.shockwaveCooldownTicks = ByteBufCodecs.VAR_INT.decode(buf);
        this.immobilizedTicks = ByteBufCodecs.VAR_INT.decode(buf);
        this.health = ByteBufCodecs.FLOAT.decode(buf);
        this.phasing = ByteBufCodecs.BOOL.decode(buf);
        this.player.refreshDimensions();
    }

    public void setImmobilized() {
        this.immobilizedTicks = DEFAULT_IMMOBILIZED_TIME;
        if(!this.player.level().isClientSide()) {
            this.player.level().globalLevelEvent(LevelEvent.SOUND_END_PORTAL_SPAWN, this.player.blockPosition(), 0);
        }
        this.sync();
    }

    public boolean isImmobilized() {
        return this.immobilizedTicks > 0;
    }

    public boolean isPhasing() {
        return this.phasing;
    }

    public void setPhasing(boolean phasing) {
        this.phasing = phasing;
        this.sync();
    }

    public boolean canShockwave() {
        return this.shockwaveCooldownTicks <= 0;
    }

    public void onShockwave() {
        this.shockwaveCooldownTicks = DEFAULT_SHOCKWAVE_COOLDOWN;
        this.sync();
    }

    public void sync() {
        this.player.syncComponent(LPComponents.LIVING_PLANET);
    }

    public void resetHealth() {
        this.setHealth(MAX_HEALTH);
    }

    public void damage(float damage) {
        this.setHealth(Math.clamp(this.getHealth() - damage, 0.0F, MAX_HEALTH));
        if(this.getHealth() <= 0) {
            this.setImmobilized();
        }
    }

    private final Map<SurroundingBlockType, Integer> surroundingsCount = new HashMap<>();
    private final List<SurroundingBlockType> surroundingsList = new ArrayList<>();
    private int totalSurroundingsCount = 1;

    public BlockState getRandomState(IntUnaryOperator random) {
        if (this.surroundingsList.isEmpty()) {
            random.applyAsInt(1);
            random.applyAsInt(1);
            return Blocks.DIRT.defaultBlockState();
        }
        int surroundingsIndex = random.applyAsInt(this.totalSurroundingsCount);
        for (var surroundings : this.surroundingsList) {
            int count = this.surroundingsCount.get(surroundings);
            if (surroundingsIndex >= count) {
                surroundingsIndex -= count;
            } else {
                return surroundings.get(random);
            }
        }

        random.applyAsInt(1);
        return Blocks.DIRT.defaultBlockState();
    }

    //todo make this wayyyy more stable
    private void updateSurroundings() {
        Level level = this.player.level();
        Holder<Biome> biome = level.getBiome(this.player.blockPosition());
        boolean addedAny = false;
        for (var surroundingPos : BlockPos.randomBetweenClosed(this.player.getRandom(),
                5,
                this.player.getBlockX() - 10,
                this.player.getBlockY() - 4,
                this.player.getBlockZ() + 10,
                this.player.getBlockX() + 10,
                this.player.getBlockY() + 1,
                this.player.getBlockZ() + 10)) {
            var state = level.getBlockState(surroundingPos);
            if (!state.isCollisionShapeFullBlock(level, surroundingPos)) {
                continue;
            }
            addedAny = true;

            SurroundingBlockType type = new SurroundingBlockType.Unknown(state);
            this.surroundingsCount.compute(type, ($, c) -> c == null ? 2 : c > 7 ? c : c + 1);
            if (!this.surroundingsList.contains(type)) {
                this.surroundingsList.add(type);
            }
        }
        if (addedAny && level.getGameTime() % 20 == 0) {
            this.surroundingsList.forEach(l -> this.surroundingsCount.computeIfPresent(l, ($, c) -> c - 1));
            this.surroundingsList.removeIf(s -> this.surroundingsCount.get(s) <= 0);
        }
        this.surroundingsList.forEach(l -> this.surroundingsCount.computeIfPresent(l, ($, c) -> c > 5 ? 5 : c));
        this.totalSurroundingsCount = this.surroundingsCount.values().stream().mapToInt($ -> $).sum();
    }
}
