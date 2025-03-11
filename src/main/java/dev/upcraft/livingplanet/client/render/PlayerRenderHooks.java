package dev.upcraft.livingplanet.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.upcraft.livingplanet.component.LivingPlanetComponent;
import dev.upcraft.livingplanet.util.Wave;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Arrays;
import java.util.Deque;

public class PlayerRenderHooks {
    public static final int SEED = 1342347;

    public static void renderPlayer(AbstractClientPlayer player, PoseStack poseStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderDispatcher, LivingPlanetComponent component, float partialTicks) {
        poseStack.pushPose();
        var pos = player.blockPosition();

        float ticksSinceChanged = component.ticksSinceChangedState(partialTicks);
        if (!component.isOutOfGround() && ticksSinceChanged <= 20) {
            poseStack.translate(0f, -(ticksSinceChanged/20.0)*player.getBbHeight(), 0f);
        }
        double maxY;
        if (ticksSinceChanged <= 10) {
            double factor = (ticksSinceChanged/10.0);
            if (component.isOutOfGround()) {
                factor = 1-factor;
            }
            maxY = LivingPlanetComponent.OUT_OF_GROUND_DIMENSIONS.scale(
                    1f,
                    (float) Mth.lerp(factor, 1f, LivingPlanetComponent.IN_GROUND_DIMENSIONS.height()/LivingPlanetComponent.OUT_OF_GROUND_DIMENSIONS.height()))
                    .height();
        } else {
            maxY = player.getBbHeight();
        }

        var random = new SingleThreadedRandomSource(SEED);
        var level = player.level();
        var levelHack = new PinnedBrightnessBlockAndTintGetter(level, pos.above((int) maxY));

        var waves = component.getWaves();

        for (int layerY = - 2; layerY < maxY; layerY++) {
            poseStack.pushPose();
            poseStack.translate(-0.5F, 0.0F, -0.5F);
            renderBlockAt(new Vector3f(0, layerY, 0), component.getRandomState(random::nextInt), poseStack, buffer, blockRenderDispatcher, levelHack, player);
            poseStack.popPose();
            double distanceFromTop = Math.min((maxY - layerY), maxY)/3.0;
            double displacement = distanceFromTop*distanceFromTop;
            int baseInstances = Math.max((int) (Math.PI * displacement * 4), 4);
            for (int instance = 0; instance < baseInstances; instance++) {
                poseStack.pushPose();
                poseStack.translate(0, layerY, displacement);
                float angle = (float) (((float) instance / baseInstances) * Math.PI * 2);
                poseStack.rotateAround(Axis.YP.rotation(angle), 0, 0, (float) -displacement);
                poseStack.mulPose(Axis.XP.rotation((float) (random.nextFloat()*Math.PI*0.5)));
                double height = getWaveHeight(partialTicks, waves, level, (float) displacement, angle);
                renderBlockAt(new Vector3f(0, (float) height, 0), component.getRandomState(random::nextInt), poseStack, buffer, blockRenderDispatcher, levelHack, player);
                poseStack.popPose();
            }
            int stickingOutInstances = (int) Math.ceil(Math.PI * displacement * random.nextDouble() * 4);
            for (int instance = 0; instance < stickingOutInstances; instance++) {
                double z = displacement*(0.2+random.nextFloat()*0.8);
                double y = layerY+random.nextDouble()*0.75;
                poseStack.pushPose();
                poseStack.translate(0, y, z);
                float angle = (float) (random.nextFloat() * Math.PI * 6);
                poseStack.rotateAround(Axis.YP.rotation(angle), 0, 0, (float) -z);
                poseStack.mulPose(Axis.ZP.rotation((float) (random.nextFloat()*Math.PI*0.5)));
                double height = getWaveHeight(partialTicks, waves, level, (float) displacement, angle);
                renderBlockAt(new Vector3f(0, (float) height, 0), component.getRandomState(random::nextInt), poseStack, buffer, blockRenderDispatcher, levelHack, player);
                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }

    private static double getWaveHeight(float partialTicks, Deque<Wave> waves, Level level, float displacement, float angle) {
        double height = 0.0;
        for (var wave : waves) {
            double waveTime = (level.getGameTime() - (wave.timeStarted() - partialTicks))/Wave.LIFETIME;
            double distanceOnWavefront = Math.abs(new Vector3f(0, 0, displacement)
                    .rotateY(angle)
                    .rotateY(-wave.angle())
                    .x() - (waveTime*Wave.LENGTH - Wave.LENGTH/2));
            double heightForWave = Math.exp(-Math.PI * distanceOnWavefront * distanceOnWavefront / 7.0);
            double strength = Math.pow((1.0-waveTime), 4);
            height += heightForWave*strength;
        }
        return height;
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
