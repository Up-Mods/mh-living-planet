package dev.upcraft.livingplanet.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.upcraft.livingplanet.client.render.PlayerRenderHooks;
import dev.upcraft.livingplanet.component.LPComponents;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    @Unique
    private BlockRenderDispatcher blockRenderDispatcher;

    private PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(context, model, shadowRadius);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(EntityRendererProvider.Context context, boolean useSlimModel, CallbackInfo ci) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }


    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"), cancellable = true)
    private void onRender(AbstractClientPlayer entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        var planet = entity.getComponent(LPComponents.LIVING_PLANET);
        if(planet.isLivingPlanet()) {
            PlayerRenderHooks.renderPlayer(entity, poseStack, buffer, blockRenderDispatcher, planet, partialTicks);
            ci.cancel();
        }
    }

}
