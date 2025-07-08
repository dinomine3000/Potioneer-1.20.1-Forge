package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.DivinationRodEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

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
}
