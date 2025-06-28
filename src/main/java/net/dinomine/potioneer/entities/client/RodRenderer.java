package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.entities.custom.DivinationRodEntity;
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

import static net.dinomine.potioneer.entities.custom.DivinationRodEntity.INTENDED_YAW;
import static net.dinomine.potioneer.entities.custom.DivinationRodEntity.lerpRotation;

public class RodRenderer extends GeoEntityRenderer<DivinationRodEntity> {
    public RodRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RodModel());
    }



    public ResourceLocation getTextureLocation(DivinationRodEntity animatable){
        return new ResourceLocation(Potioneer.MOD_ID, "textures/entity/divination_rod.png");
    }

    @Override
    protected void applyRotations(DivinationRodEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        float smoothYaw = lerpRotation(animatable.yRotO, animatable.getYRot(), partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(-smoothYaw));
    }

    @Override
    public void render(DivinationRodEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
