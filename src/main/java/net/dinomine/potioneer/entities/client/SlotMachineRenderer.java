package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.entities.custom.SlotMachineEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class SlotMachineRenderer extends GeoEntityRenderer<SlotMachineEntity> {
    public SlotMachineRenderer(EntityRendererProvider.Context context) {
        super(context, new SlotMachineModel());
    }

    @Override
    protected void applyRotations(SlotMachineEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(-animatable.getYRot()));
    }
}
