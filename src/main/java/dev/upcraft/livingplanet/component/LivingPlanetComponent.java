package dev.upcraft.livingplanet.component;

import dev.upcraft.livingplanet.LPOptions;
import dev.upcraft.livingplanet.particle.LPParticles;
import dev.upcraft.livingplanet.particle.LivingPlanetTerrainParticleOption;
import dev.upcraft.livingplanet.tag.LPTags;
import dev.upcraft.livingplanet.util.SurroundingBlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityDimensions;
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
import java.util.function.IntUnaryOperator;

public class LivingPlanetComponent implements Component, AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {

    public static final EntityDimensions IN_GROUND_DIMENSIONS = EntityDimensions.fixed(0.25F, 0.25F);
    public static final EntityDimensions OUT_OF_GROUND_DIMENSIONS = EntityDimensions.scalable(4.0F, 7.0F);
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
    private boolean outOfGround = false;

    private int immobilizedTicks = 0;
    private int shockwaveCooldownTicks = 0;

    private float health;
    private boolean phasing = false;
    private long timeChangedState = -1;

    public LivingPlanetComponent(Player player) {
        this.player = player;
    }

    public void setOutOfGround(boolean visible) {
        this.outOfGround = visible;
        this.player.refreshDimensions();
    }

    public boolean isOutOfGround() {
        return this.outOfGround || this.player.isSpectator() || this.player.isDeadOrDying();
    }

    public void setLivingPlanet(boolean livingPlanet) {
        this.livingPlanet = livingPlanet;
        if (!livingPlanet) {
            this.outOfGround = false;
            this.immobilizedTicks = 0;
            this.shockwaveCooldownTicks = 0;
            this.health = MAX_HEALTH;
            this.phasing = false;
        }
    }

    public boolean isLivingPlanet() {
        return this.livingPlanet && !this.player.isSpectator(); // allow being living planet while dying tho
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
        this.outOfGround = tag.getBoolean("visible");
        this.immobilizedTicks = tag.getInt("immobilizedTicks");
        this.health = tag.getFloat("health");
        this.phasing = tag.getBoolean("phasing");
        this.player.refreshDimensions();
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        tag.putBoolean("active", this.livingPlanet);
        tag.putBoolean("visible", this.outOfGround);
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
        float ticksSinceChangedState = this.ticksSinceChangedState(0f);
        boolean isChanging = ticksSinceChangedState <= 10;
        if (this.isOutOfGround() || isChanging) {
            var random = this.player.getRandom();
            double height = isChanging ? Mth.lerp(this.isOutOfGround() ? (1f-(ticksSinceChangedState/10)) : (ticksSinceChangedState/10), OUT_OF_GROUND_DIMENSIONS.height(), IN_GROUND_DIMENSIONS.height()) : this.player.getBbHeight();
            for (int i = 0; i < 20; i++) {
                var particle = new LivingPlanetTerrainParticleOption(LPParticles.BIG_TERRAIN_PARTICLE.get(), this.getRandomState(random::nextInt), this.player.getId());
                double y = 0.3+random.nextGaussian()*(isChanging ? height : Math.min(1, height));
                double yDist = (height-y)/3.0;
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
        ByteBufCodecs.BOOL.encode(buf, this.outOfGround);
        ByteBufCodecs.VAR_INT.encode(buf, this.shockwaveCooldownTicks);
        ByteBufCodecs.VAR_INT.encode(buf, this.immobilizedTicks);
        ByteBufCodecs.FLOAT.encode(buf, this.health);
        ByteBufCodecs.BOOL.encode(buf, this.phasing);
    }

    @Override
    public void applySyncPacket(RegistryFriendlyByteBuf buf) {
        boolean wasOutOfGround = this.isOutOfGround();
        this.livingPlanet = ByteBufCodecs.BOOL.decode(buf);
        this.outOfGround = ByteBufCodecs.BOOL.decode(buf);
        this.shockwaveCooldownTicks = ByteBufCodecs.VAR_INT.decode(buf);
        this.immobilizedTicks = ByteBufCodecs.VAR_INT.decode(buf);
        this.health = ByteBufCodecs.FLOAT.decode(buf);
        this.phasing = ByteBufCodecs.BOOL.decode(buf);
        this.player.refreshDimensions();
        if (wasOutOfGround != this.outOfGround) {
            this.timeChangedState = this.player.level().getGameTime();
        }
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
        return this.phasing && !this.player.isSpectator() && !this.player.isDeadOrDying();
    }

    public void setPhasing(boolean phasing) {
        this.phasing = phasing;
        this.sync();
    }

    public boolean canShockwave() {
        return this.shockwaveCooldownTicks <= 0;
    }

    public void onShockwave() {
        this.shockwaveCooldownTicks = LPOptions.SHOCKWAVE_COOLDOWN_SECONDS.get()*20;
        this.setOutOfGround(false);
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

    private final Map<SurroundingBlockType, Integer> surroundings = new HashMap<>();
    private final Set<SurroundingBlockType> effectiveSurroundings = new HashSet<>();

    public BlockState getRandomState(IntUnaryOperator random) {
        if (this.effectiveSurroundings.isEmpty()) {
            random.applyAsInt(1);
            random.applyAsInt(1);
            return Blocks.DIRT.defaultBlockState();
        }
        int surroundingsIndex = random.applyAsInt(this.effectiveSurroundings.size());
        for (var surroundings : this.effectiveSurroundings) {
            if (surroundingsIndex-- == 0) {
                return surroundings.get(random);
            }
        }

        random.applyAsInt(1);
        return Blocks.DIAMOND_BLOCK.defaultBlockState();
    }

    private void updateSurroundings() {
        Level level = this.player.level();
        Holder<Biome> biome = level.getBiome(this.player.blockPosition());
        RandomSource random = this.player.getRandom();
        Set<SurroundingBlockType> newSurroundings = new HashSet<>();
        for (var surroundingPos : BlockPos.randomBetweenClosed(random,
                30,
                this.player.getBlockX() - 10,
                this.player.getBlockY() - 5,
                this.player.getBlockZ() + 10,
                this.player.getBlockX() + 10,
                this.player.getBlockY() + 0,
                this.player.getBlockZ() + 10)) {
            var state = level.getBlockState(surroundingPos);
            if (!state.is(LPTags.LIVING_PLANET_BLOCKS) || !state.isCollisionShapeFullBlock(level, surroundingPos)) {
                continue;
            }

            SurroundingBlockType type = new SurroundingBlockType.Unknown(state);
            this.surroundings.compute(type, ($, c) -> c == null ? 10 : Math.min(100, c + 1));
        }

        this.effectiveSurroundings.clear();
        for (var iter = this.surroundings.entrySet().iterator(); iter.hasNext();) {
            var entry = iter.next();
            entry.setValue(entry.getValue() - 1);
            if (entry.getValue() <= 0) {
                iter.remove();
            }
            if (entry.getValue() >= 50) {
                this.effectiveSurroundings.add(entry.getKey());
            }
        }
    }

    public float ticksSinceChangedState(float partialTick) {
        return (float) ((this.player.level().getGameTime() + (double) partialTick) - this.timeChangedState);
    }
}
