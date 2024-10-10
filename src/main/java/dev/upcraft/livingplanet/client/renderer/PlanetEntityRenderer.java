package dev.upcraft.livingplanet.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.upcraft.livingplanet.component.LPComponents;
import dev.upcraft.livingplanet.entity.PlanetEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class PlanetEntityRenderer extends EntityRenderer<PlanetEntity> {

    public PlanetEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(PlanetEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        var owner = entity.getOwner();
        if(owner != null) {
            var planetData = owner.getComponent(LPComponents.LIVING_PLANET);
            if(planetData.isVisible()) {
                // TODO rendering above ground
            }
            else {
                // TODO rendering in ground
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(PlanetEntity entity) {
        return TextureManager.INTENTIONAL_MISSING_TEXTURE;
    }
}
