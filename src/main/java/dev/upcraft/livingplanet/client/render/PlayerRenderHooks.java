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
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PlayerRenderHooks {
    public static final int SEED = 1342347;

    public static void renderPlayer(AbstractClientPlayer player, PoseStack poseStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderDispatcher, LivingPlanetComponent component, float partialTicks) {
        poseStack.pushPose();
        var pos = player.blockPosition();
        var yRot = Mth.lerp(partialTicks, player.yHeadRotO, player.yHeadRot);
        //poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
        var states = new BlockState[] {Blocks.DIRT.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState()};
        var stickingOutStates = new BlockState[] {Blocks.DIRT.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState()};
        var centreState = Blocks.STONE.defaultBlockState();


        var random = new SingleThreadedRandomSource(SEED);
        var level = player.level();
        var levelHack = new PinnedBrightnessBlockAndTintGetter(level, pos);

        if(component.isOutOfGround()) {
            double maxY = player.getBoundingBox().maxY - player.getY();
            for (int layerY = - 2; layerY < maxY; layerY++) {
                poseStack.pushPose();
                poseStack.translate(-0.5F, 0.0F, -0.5F);
                renderBlockAt(new Vector3f(0, layerY, 0), component.getRandomState(random::nextInt), poseStack, buffer, blockRenderDispatcher, levelHack, player);
                poseStack.popPose();
                double distanceFromTop = (maxY - layerY)/3.0;
                double displacement = distanceFromTop*distanceFromTop;
                int baseInstances = Math.max((int) (Math.PI * displacement * 4), 4);
                for (int instance = 0; instance < baseInstances; instance++) {
                    poseStack.pushPose();
                    poseStack.translate(0, layerY, displacement);
                    poseStack.rotateAround(Axis.YP.rotation((float) (((float) instance /baseInstances)*Math.PI*2)), 0, 0, (float) -displacement);
                    poseStack.mulPose(Axis.XP.rotation((float) (random.nextFloat()*Math.PI*0.5)));
                    renderBlockAt(new Vector3f(0, 0, 0), component.getRandomState(random::nextInt), poseStack, buffer, blockRenderDispatcher, levelHack, player);
                    poseStack.popPose();
                }
                int stickingOutInstances = (int) Math.ceil(Math.PI * displacement * random.nextDouble() * 4);
                for (int instance = 0; instance < stickingOutInstances; instance++) {
                    double z = displacement*(0.2+random.nextFloat()*0.8);
                    double y = layerY+random.nextDouble()*0.75;
                    poseStack.pushPose();
                    poseStack.translate(0, y, z);
                    poseStack.rotateAround(Axis.YP.rotation((float) (random.nextFloat()*Math.PI*6)), 0, 0, (float) -z);
                    poseStack.mulPose(Axis.ZP.rotation((float) (random.nextFloat()*Math.PI*0.5)));
                    renderBlockAt(new Vector3f(0, 0, 0), component.getRandomState(random::nextInt), poseStack, buffer, blockRenderDispatcher, levelHack, player);
                    poseStack.popPose();
                }
            }
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
