package dev.upcraft.livingplanet.client.tracking;

import com.mojang.datafixers.util.Pair;
import dev.upcraft.livingplanet.item.LPItems;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static dev.upcraft.livingplanet.LivingPlanet.id;

public class Tracker {
    public static final ItemStack TRACKER_COMPASS_ITEM = LPItems.PLAYER_TRACKER_COMPASS.get().getDefaultInstance();
    public static final ResourceKey<Level> UNKNOWN = ResourceKey.create(Registries.DIMENSION, id("unknown"));
    @NotNull ResourceKey<Level> dimension = UNKNOWN;
    @Nullable Pair<Vec3, Vec3> prevPosAndCurrentPos;
    @NotNull List<Component> messages = List.of();
    @Nullable UUID playerToTrack;

    public void tick(Level level) {
        this.prevPosAndCurrentPos = null;
        this.messages = List.of();
        this.dimension = UNKNOWN;
        if (this.playerToTrack == null) {
            return;
        }
        var player = level.getPlayerByUUID(this.playerToTrack);
        if (player != null) {
            var pos = player.position();
            var prevPos = this.prevPosAndCurrentPos == null || this.prevPosAndCurrentPos.getSecond() == null ? pos : this.prevPosAndCurrentPos.getSecond();
            this.prevPosAndCurrentPos = Pair.of(prevPos, pos);
            var name = player.getDisplayName();
            if (name == null) {
                name = Component.empty();
            }
            var dist = pos.distanceTo(player.position());
            var distMessage = Component.literal("%.0f Blocks".formatted(dist));
            this.messages = List.of(name, distMessage);
            this.dimension = level.dimension();
        } else {
            this.messages = List.of(Component.literal("???"), Component.literal("??? Blocks"));
            this.dimension = UNKNOWN;
        }
    }

    private @Nullable Vec3 pos(float partialTicks) {
        return this.prevPosAndCurrentPos == null ? null : this.prevPosAndCurrentPos.getFirst().lerp(this.prevPosAndCurrentPos.getSecond(), partialTicks);
    }

    private void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        var pos = this.pos(deltaTracker.getGameTimeDeltaPartialTick(true));
        if (pos == null) {
            return;
        }

        for (int i = 0; i < this.messages.size(); i++) {
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.messages.get(i), guiGraphics.guiWidth()/2, guiGraphics.guiHeight() - 38 - i*10, -1);
        }

        guiGraphics.renderFakeItem(TRACKER_COMPASS_ITEM, guiGraphics.guiWidth()/2 - 8, guiGraphics.guiHeight() - 66);
    }

    public static void init() {
        var tracker = new Tracker();
        ClientTickEvents.START_WORLD_TICK.register(tracker::tick);
        HudRenderCallback.EVENT.register(tracker::render);
        ItemProperties.register(LPItems.PLAYER_TRACKER_COMPASS.get(), id("angle"), new CompassItemPropertyFunction(
                (clientLevel, itemStack, entity) -> tracker.prevPosAndCurrentPos == null
                        ? null
                        : GlobalPos.of(tracker.dimension, BlockPos.containing(tracker.prevPosAndCurrentPos.getSecond()))));
    }
}
