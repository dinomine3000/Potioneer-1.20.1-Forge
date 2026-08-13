package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.entities.custom.effects.AbstractEffectEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public interface IAbstractEffectRenderer {
    default <T extends AbstractEffectEntity> void renderEffect(T effectEntity, PoseStack pPoseStack, float pPartialTick){
        int targetId = effectEntity.getTargetIntId();
        Entity targetEntity = effectEntity.level().getEntity(targetId);

        if (targetEntity instanceof LivingEntity livingTargetEntity) {
            // Interpolate the target's exact position between ticks
            /*double targetX = Mth.lerp(pPartialTick, livingTargetEntity.xOld, livingTargetEntity.getX());
            double targetY = Mth.lerp(pPartialTick, livingTargetEntity.yOld, livingTargetEntity.getY());
            double targetZ = Mth.lerp(pPartialTick, livingTargetEntity.zOld, livingTargetEntity.getZ());*/
            Vec3 targetEye = livingTargetEntity.getEyePosition(pPartialTick);
            double targetX = targetEye.x;
            double targetY = targetEye.y;
            double targetZ = targetEye.z;

            // Interpolate the effect entity's position between ticks
            double effectX = Mth.lerp(pPartialTick, effectEntity.xOld, effectEntity.getX());
            double effectY = Mth.lerp(pPartialTick, effectEntity.yOld, effectEntity.getY());
            double effectZ = Mth.lerp(pPartialTick, effectEntity.zOld, effectEntity.getZ());

            // Translate to the exact smooth offset
            pPoseStack.translate(targetX - effectX, targetY - effectY, targetZ - effectZ);

            // Interpolate rotation smoothly too
            float targetRot = effectEntity.rotatesWithHead()
                    ? Mth.rotLerp(pPartialTick, livingTargetEntity.yHeadRotO, livingTargetEntity.getYHeadRot())
                    : Mth.rotLerp(pPartialTick, livingTargetEntity.yRotO, livingTargetEntity.getYRot());

            pPoseStack.mulPose(Axis.YP.rotationDegrees(-targetRot));
        } else {
            float effectRot = Mth.rotLerp(pPartialTick, effectEntity.yRotO, effectEntity.getYRot());
            pPoseStack.mulPose(Axis.YP.rotationDegrees(-effectRot));
        }

        Vector3f offset = effectEntity.getOffset();
        pPoseStack.translate(offset.x, offset.y, offset.z);
    }
}
