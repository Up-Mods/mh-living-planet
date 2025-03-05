package dev.upcraft.livingplanet.client.tracking;

import dev.upcraft.livingplanet.item.LPItems;
import dev.upcraft.livingplanet.net.TrackingRequestPacket;
import dev.upcraft.livingplanet.net.TrackingResponsePacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
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
    public static final String TRACKER_KEY = "gui.living_planet.tracker";
    private static Tracker INSTANCE;
    @NotNull ResourceKey<Level> dimension = UNKNOWN;
    @Nullable Vec3 pos;
    @Nullable Vec3 serverPos;
    @NotNull List<Component> messages = List.of();
    @Nullable UUID playerToTrack;

    public void track(UUID uuid) {
        this.playerToTrack = uuid;
    }

    public void tick(Level level) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (this.playerToTrack == null || player == null) {
            return;
        }
        var playerInfo = player.connection.getPlayerInfo(this.playerToTrack);
        var tracked = level.getPlayerByUUID(this.playerToTrack);
        if (tracked == null) {
            ClientPlayNetworking.send(new TrackingRequestPacket(this.playerToTrack));
            this.pos = this.serverPos;
        } else {
            this.pos = tracked.position();
            this.dimension = level.dimension();
        }
        var name = playerInfo == null ? Component.literal("???") : playerInfo.getTabListDisplayName() == null ? Component.literal(playerInfo.getProfile().getName()) : playerInfo.getTabListDisplayName();
        var dist = this.pos == null || this.dimension != level.dimension() ? Double.NaN : this.pos.distanceTo(player.position());
        var distMessage = dist != dist ? Component.literal("??? Blocks") : Component.literal("%.0f Blocks".formatted(dist));
        this.messages = List.of(name, distMessage);
    }

    private void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (this.playerToTrack != null) {
            for (int i = 0; i < this.messages.size(); i++) {
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.messages.get(i), guiGraphics.guiWidth() / 2, guiGraphics.guiHeight() - 38 - i * 10, -1);
            }

            guiGraphics.renderItem(TRACKER_COMPASS_ITEM, guiGraphics.guiWidth() / 2 - 8, guiGraphics.guiHeight() - 66);
        }
    }

    public static void init() {
        var tracker = INSTANCE = new Tracker();
        ClientTickEvents.START_WORLD_TICK.register(tracker::tick);
        HudRenderCallback.EVENT.register(tracker::render);
        ItemProperties.register(LPItems.PLAYER_TRACKER_COMPASS.get(), id("angle"), new CompassItemPropertyFunction(
                (clientLevel, itemStack, entity) -> tracker.pos == null
                        ? null
                        : GlobalPos.of(tracker.dimension, BlockPos.containing(tracker.pos))));
        ClientPlayNetworking.registerGlobalReceiver(TrackingResponsePacket.TYPE, (payload, context) -> {
            tracker.serverPos = payload.position();
            tracker.dimension = payload.dimension();
        });
    }

    public static void onButtonPress(Button button) {
        Minecraft.getInstance().setScreen(new PlayerTrackingScreen(Component.translatable(TRACKER_KEY), INSTANCE));
    }
}
