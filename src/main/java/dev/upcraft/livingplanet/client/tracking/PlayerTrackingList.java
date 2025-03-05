package dev.upcraft.livingplanet.client.tracking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlayerTrackingList extends ObjectSelectionList<PlayerTrackingList.Entry> {
    private final Consumer<@Nullable UUID> select;

    public PlayerTrackingList(Minecraft minecraft, int width, int height, int y, @Nullable UUID selected, Consumer<@Nullable UUID> select) {
        super(minecraft, width, height, y, 30);
        if (minecraft.player != null) {
            ClientPacketListener clientPacketListener = minecraft.player.connection;
            Collection<UUID> collection = minecraft.player.connection.getOnlinePlayerIds();
            for (var id : collection) {
                PlayerInfo playerInfo = clientPacketListener.getPlayerInfo(id);
                if (playerInfo != null && !minecraft.getGameProfile().equals(playerInfo.getProfile())) {
                    Entry entry = new Entry(minecraft, id, playerInfo.getTabListDisplayName() == null ? Component.literal(playerInfo.getProfile().getName()) : playerInfo.getTabListDisplayName(), playerInfo::getSkin);
                    this.addEntry(entry);
                    if (id.equals(selected)) {
                        this.setSelected(entry);
                    }
                }
            }
        }
        this.select = select;
    }

    @Override
    public void setSelected(@Nullable PlayerTrackingList.Entry selected) {
        super.setSelected(selected);
        if (this.select != null) {
            this.select.accept(selected == null ? null : selected.id);
        }
    }

    public class Entry extends ObjectSelectionList.Entry<PlayerTrackingList.Entry> {
        private final Minecraft minecraft;
        private final UUID id;
        private final Component playerName;
        private final Supplier<PlayerSkin> skinGetter;

        public Entry(Minecraft minecraft, UUID id, Component playerName, Supplier<PlayerSkin> skinGetter) {
            this.minecraft = minecraft;
            this.id = id;
            this.playerName = playerName;
            this.skinGetter = skinGetter;
        }

        @Override
        public Component getNarration() {
            return playerName;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            PlayerFaceRenderer.draw(guiGraphics, this.skinGetter.get(), left + 4, top + (height - 24) / 2, 24);
            guiGraphics.drawString(this.minecraft.font, this.playerName, left + 30, top + (height - 9) / 2, -1, false);
        }
    }
}
