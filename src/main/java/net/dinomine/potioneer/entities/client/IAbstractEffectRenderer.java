package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.entities.custom.effects.AbstractEffectEntity;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

public interface IAbstractEffectRenderer {
    default <T extends AbstractEffectEntity> void renderEffect(T pEntity, PoseStack pPoseStack){
        //pPoseStack.mulPose(Axis.YP.rotationDegrees(-pEntity.getYRot()));
        int targetId = pEntity.getTargetIntId();

        Entity ent = pEntity.level().getEntity(targetId);
        Vector3f offset = pEntity.getOffset();
        if(ent == null){
            pPoseStack.mulPose(Axis.YP.rotationDegrees(-pEntity.getYRot()));
        }
        else{
            pPoseStack.translate(
                    ent.position().x - pEntity.position().x,
                    ent.position().y - pEntity.position().y,
                    ent.position().z - pEntity.position().z);
            if(pEntity.rotatesWithHead())
                pPoseStack.mulPose(Axis.YP.rotationDegrees(-ent.getYHeadRot()));
            else
                pPoseStack.mulPose(Axis.YP.rotationDegrees(-ent.getYRot()));
        }
        pPoseStack.translate(offset.x, offset.y, offset.z);
    }
}
