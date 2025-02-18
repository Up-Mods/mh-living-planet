package dev.upcraft.livingplanet.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PlayerRenderHooks {
    public static void renderPlayer(AbstractClientPlayer player, PoseStack poseStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderDispatcher, LivingPlanetComponent component, float partialTicks) {
        poseStack.pushPose();
        var pos = player.blockPosition();
        var yRot = Mth.lerp(partialTicks, player.yHeadRotO, player.yHeadRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        var state = Blocks.DIRT.defaultBlockState();


        var level = player.level();
        var levelHack = new PinnedBrightnessBlockAndTintGetter(level, pos);

        renderBlockAt(new Vector3f(0, 0, 0), state, poseStack, buffer, blockRenderDispatcher, levelHack, player);

        if(component.isVisible()) {
            renderBlockAt(new Vector3f(0, 1, 0), state, poseStack, buffer, blockRenderDispatcher, levelHack, player);
        }

        poseStack.popPose();
    }

    private static void renderBlockAt(Vector3fc offset, BlockState state, PoseStack poseStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderDispatcher, PinnedBrightnessBlockAndTintGetter levelHack, AbstractClientPlayer player) {
        poseStack.pushPose();
        poseStack.translate(offset.x(), offset.y(), offset.z());
        var vertexConsumer = buffer.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
        var model = blockRenderDispatcher.getBlockModel(state);
        var seed = state.getSeed(BlockPos.ZERO);
        blockRenderDispatcher.getModelRenderer().tesselateWithoutAO(levelHack, model, state, levelHack.getOrigin(), poseStack, vertexConsumer, false, player.getRandom(), seed, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
