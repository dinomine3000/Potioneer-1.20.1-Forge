package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import java.util.Objects;

public class ChryonRenderer extends GeoEntityRenderer<ChryonEntity> {
    public ChryonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ChryonModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.addRenderLayer(new BlockAndItemGeoLayer<>(this, (bone, animatable) -> {
            if (Objects.equals(bone.getName(), "sword")) //left hand
                return animatable.getItemInHand(InteractionHand.MAIN_HAND);
            return null;
        }, (bone, animatable) -> null) {

            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, ChryonEntity animatable) {
                return ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            }


            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, ChryonEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.pushPose();
                poseStack.translate(0, -0.8, -0.5);

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
                poseStack.popPose();
            }
        });
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
