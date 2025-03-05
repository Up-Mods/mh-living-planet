package dev.upcraft.livingplanet.client.tracking;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PlayerTrackingScreen extends Screen {
    private static final String DESELECT_KEY = "gui.living_planet.tracker.deselect";
    private final Tracker tracker;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private PlayerTrackingList list;

    protected PlayerTrackingScreen(Component title, Tracker tracker) {
        super(title);
        this.tracker = tracker;
    }

    @Override
    protected void init() {
        super.init();
        layout.addTitleHeader(this.title, this.font);
        layout.addToContents(this.list = new PlayerTrackingList(this.minecraft, 200, 200, 30, this.tracker.playerToTrack, this.tracker::track));
        layout.addToFooter(Button.builder(Component.translatable(DESELECT_KEY), button -> this.list.setSelected(null))
                .width(100)
                .build());
        layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
        }
    }
}
