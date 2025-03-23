package dev.upcraft.livingplanet.client.tracking;

import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.net.ChasmsPacket;
import dev.upcraft.livingplanet.net.RagePacket;
import dev.upcraft.livingplanet.net.LightningPacket;
import dev.upcraft.livingplanet.net.ShockwavePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PlayerTrackingScreen extends Screen {
    public static final String DESELECT_KEY = "gui.living_planet.tracker.deselect";
    public static final String LIGHTNING_KEY = "gui.living_planet.tracker.lightning";
    public static final String RAGE_KEY = "gui.living_planet.tracker.rage";
    public static final String CHASMS_KEY = "gui.living_planet.tracker.chasms";
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
        var footer = layout.addToFooter(LinearLayout.horizontal().spacing(2));
        footer.addChild(Button.builder(Component.translatable(DESELECT_KEY), button -> this.list.setSelected(null))
                .width(80)
                .build());
        var component = this.minecraft.player.getComponent(LPComponents.LIVING_PLANET);
        var lightningButton = footer.addChild(Button.builder(Component.translatable(LIGHTNING_KEY), button -> {
            ClientPlayNetworking.send(new LightningPacket());
            this.onClose();
                })
                .width(80)
                .build());
        if (!component.canLightning()) {
            lightningButton.active = false;
            lightningButton.setTooltip(Tooltip.create(Component.translatable(ShockwavePacket.COOLDOWN_MESSAGE_KEY)));
        }
        var rageButton = footer.addChild(Button.builder(Component.translatable(RAGE_KEY), button -> {ClientPlayNetworking.send(new RagePacket()); this.onClose();})
                .width(80)
                .build());
        if (!component.canRage()) {
            rageButton.active = false;
            rageButton.setTooltip(Tooltip.create(Component.translatable(ShockwavePacket.COOLDOWN_MESSAGE_KEY)));
        }
        var chasmsButton = footer.addChild(Button.builder(Component.translatable(CHASMS_KEY), button -> {ClientPlayNetworking.send(new ChasmsPacket()); this.onClose();})
                .width(80)
                .build());
        if (!component.canChasms()) {
            chasmsButton.active = false;
            chasmsButton.setTooltip(Tooltip.create(Component.translatable(ShockwavePacket.COOLDOWN_MESSAGE_KEY)));
        }
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
