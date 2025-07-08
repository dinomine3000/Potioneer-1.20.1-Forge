package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.PecanEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PecanRenderer extends GeoEntityRenderer<PecanEntity> {
    public PecanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PecanModel());
    }

    public ResourceLocation getTextureLocation(PecanEntity animatable){
        return new ResourceLocation(Potioneer.MOD_ID, "textures/entity/pecan.png");
    }

    @Override
    public void render(PecanEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
