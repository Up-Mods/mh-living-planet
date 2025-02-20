package dev.upcraft.livingplanet.component;

import dev.upcraft.livingplanet.particle.LPParticles;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.Vec3;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

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
        if (this.isVisible()) {
            var random = player.getRandom();
            for (int i = 0; i < 20; i++) {
                var particle = new BlockParticleOption(LPParticles.BIG_TERRAIN_PARTICLE.get(), Blocks.DIRT.defaultBlockState());
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
}
