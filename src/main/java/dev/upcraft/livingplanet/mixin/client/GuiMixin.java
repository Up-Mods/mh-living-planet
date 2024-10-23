package dev.upcraft.livingplanet.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.upcraft.livingplanet.LivingPlanet;
import dev.upcraft.livingplanet.component.LPComponents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private static ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE;

    @Shadow
    @Final
    private static ResourceLocation EXPERIENCE_BAR_PROGRESS_SPRITE;

    @Shadow @Nullable protected abstract Player getCameraPlayer();



    @ModifyReturnValue(method = "isExperienceBarVisible", at = @At("RETURN"))
    private boolean cancelXpBar(boolean original) {
        return original && !this.minecraft.player.getComponent(LPComponents.LIVING_PLANET).isLivingPlanet();
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"), cancellable = true)
    private void onRenderHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        var component = this.minecraft.player.getComponent(LPComponents.LIVING_PLANET);
        if (component.isLivingPlanet()) {
            this.minecraft.getProfiler().push(LivingPlanet.id("planet_health_bar").toString());
            int barWidth = 182;
            int x = guiGraphics.guiWidth() / 2 - 91;

            int fraction = (int) ((component.getHealth() / component.getMaxHealth()) * (barWidth + 1.0F));
            int barYPos = guiGraphics.guiHeight() - 32 + 3;
            RenderSystem.enableBlend();
            guiGraphics.blitSprite(EXPERIENCE_BAR_BACKGROUND_SPRITE, x, barYPos, barWidth, 5);
            if (fraction > 0) {
                guiGraphics.blitSprite(EXPERIENCE_BAR_PROGRESS_SPRITE, barWidth, 5, 0, 0, x, barYPos, fraction, 5);
            }

            RenderSystem.disableBlend();

            this.minecraft.getProfiler().pop();


            ci.cancel();
        }
    }
}
