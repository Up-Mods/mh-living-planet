package dev.upcraft.livingplanet.component;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LevelEvent;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class LivingPlanetComponent implements Component, AutoSyncedComponent, ServerTickingComponent {

    private static final int DEFAULT_IMMOBILIZED_TIME = 20 * 120;
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
    }

    @Override
    public void writeSyncPacket(RegistryFriendlyByteBuf buf, ServerPlayer recipient) {
        ByteBufCodecs.BOOL.encode(buf, this.livingPlanet);
        ByteBufCodecs.BOOL.encode(buf, this.visible);
        ByteBufCodecs.VAR_INT.encode(buf, this.immobilizedTicks);
        ByteBufCodecs.FLOAT.encode(buf, this.health);
        ByteBufCodecs.BOOL.encode(buf, this.phasing);
    }

    @Override
    public void applySyncPacket(RegistryFriendlyByteBuf buf) {
        this.livingPlanet = ByteBufCodecs.BOOL.decode(buf);
        this.visible = ByteBufCodecs.BOOL.decode(buf);
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
