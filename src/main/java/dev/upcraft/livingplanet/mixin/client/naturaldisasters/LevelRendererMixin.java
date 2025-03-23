package dev.upcraft.livingplanet.mixin.client.naturaldisasters;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.naturaldisasters.LightningStorm;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow @Nullable private ClientLevel level;

    private @Unique int cloudNumber = 0;

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FDDD)V"))
    private void naturaldisasters$renderMoreClouds(LevelRenderer instance, PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, Operation<Void> original) {
        if (this.level != null && LPComponents.NATURAL_DISASTERS.get(this.level).hasDisaster(LightningStorm.class)) {
            for (int i = 1; i < 10; i++) {
                this.cloudNumber = i;
                original.call(instance, poseStack, frustumMatrix, projectionMatrix, partialTick, camX, camY, camZ);
            }
        } else {
            original.call(instance, poseStack, frustumMatrix, projectionMatrix, partialTick, camX, camY, camZ);
        }
        this.cloudNumber = 0;
    }

    @ModifyVariable(
            method = "renderClouds",
            at = @At(value = "INVOKE", target = "Ljava/lang/Float;isNaN(F)Z"),
            ordinal = 1
    )
    private float naturaldisasters$moveCloudY(float original) {
        return switch(this.cloudNumber) {
            case 1 -> original*0.6f;
            case 2 -> original*0.75f;
            case 3 -> original*0.80f;
            case 4 -> original*0.85f;
            case 5 -> original*0.9f;
            case 6 -> original*1.1f;
            case 7 -> original*1.2f;
            case 8 -> original*2.0f;
            case 9 -> original*2.3f;
            default -> original;
        };
    }

    @ModifyExpressionValue(
            method = "renderClouds",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;ticks:I")
    )
    private int naturaldisasters$changeEffectiveCloudTime(int original) {
        return original + (this.cloudNumber*1037);
    }
}
