package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChryonRenderer extends GeoEntityRenderer<ChryonEntity> {
    public ChryonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ChryonModel());
    }

    public ResourceLocation getTextureLocation(ChryonEntity animatable){
        return new ResourceLocation(Potioneer.MOD_ID, "textures/entity/chryon.png");
    }

    @Override
    public void render(ChryonEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
