package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.SeaGodScepterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import static net.dinomine.potioneer.entities.custom.DivinationRodEntity.lerpRotation;

public class SeaGodRenderer extends GeoEntityRenderer<SeaGodScepterEntity> {
    public SeaGodRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SeaGodModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }



    public ResourceLocation getTextureLocation(SeaGodScepterEntity animatable){
        return new ResourceLocation(Potioneer.MOD_ID, "textures/entity/sea_god_scepter.png");
    }

    @Override
    protected void applyRotations(SeaGodScepterEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        float smoothYaw = lerpRotation(animatable.yRotO, animatable.getYRot(), partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(-smoothYaw));
    }
}
