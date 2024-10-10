package dev.upcraft.livingplanet.component;

import dev.upcraft.livingplanet.entity.PlanetEntity;
import dev.upcraft.livingplanet.init.LPEntityTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class LivingPlanetComponent implements Component, AutoSyncedComponent, ServerTickingComponent {

    private static final int DEFAULT_IMMOBILIZED_TIME = 20 * 120;

    @Nullable
    private PlanetEntity planet;
    private final Player player;

    /**
     * whether the player entity has been removed due to disconnect or similar
     */
    private boolean removed = false;

    /**
     * whether the player currently is a living planet
     */
    private boolean livingPlanet = false;

    /**
     * whether the player is currently visible (= above ground) or not
     */
    private boolean visible = false;

    private int immobilizedTicks = 0;

    public LivingPlanetComponent(Player player) {
        this.player = player;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setLivingPlanet(boolean livingPlanet) {
        this.livingPlanet = livingPlanet;
    }

    public boolean isLivingPlanet() {
        return livingPlanet;
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        this.livingPlanet = tag.getBoolean("active");
        this.visible = tag.getBoolean("visible");
        this.removed = tag.getBoolean("removed");
        this.immobilizedTicks = tag.getInt("immobilizedTicks");
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        tag.putBoolean("active", this.livingPlanet);
        tag.putBoolean("visible", this.visible);
        tag.putBoolean("removed", this.removed);
        tag.putInt("immobilizedTicks", this.immobilizedTicks);
    }

    @Override
    public void serverTick() {
        if(!updatePlanetState()) {
            return;
        }

        if(this.immobilizedTicks > 0) {
            this.immobilizedTicks--;

            if(this.immobilizedTicks <= 0) {
                // TODO unfreeze controls
                sync();
            }
        }
    }

    public void setImmobilized() {
        immobilizedTicks = DEFAULT_IMMOBILIZED_TIME;
        sync();
    }

    private boolean updatePlanetState() {
        // player is not currently supposed to be a planet or got invalidated somehow
        if (!livingPlanet || player.isRemoved() || !player.isAlive() || player.isSpectator()) {
            removePlanet();
            return false;
        }

        if (this.planet == null) {
            // not removed but planet does not exist -> spawn it
            if (!this.removed && player instanceof ServerPlayer serverPlayer) {
                this.planet = LPEntityTypes.PLANET.get().create(player.level());
                if (this.planet == null) {
                    throw new IllegalStateException("planet was null after creation");
                }
                this.planet.setPos(player.position());
                this.planet.setOwner(serverPlayer);

                this.player.level().addFreshEntity(this.planet);
                this.planet.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                serverPlayer.setCamera(this.planet);
                serverPlayer.startRiding(planet, true);
            }
        } else if (this.removed) { // planet does exist but should not -> remove it
            removePlanet();
            return false;
        }

        return true;
    }

    private void removePlanet() {
        if(this.planet != null) {
            this.planet.remove(Entity.RemovalReason.DISCARDED);
            this.planet = null;
        }
    }

    public void sync() {
        player.syncComponent(LPComponents.LIVING_PLANET);
    }
}
