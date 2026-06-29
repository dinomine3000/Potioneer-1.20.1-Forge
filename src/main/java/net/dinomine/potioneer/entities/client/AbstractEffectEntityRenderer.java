package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.entities.custom.effects.AbstractEffectEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractEffectEntityRenderer<T extends AbstractEffectEntity> extends EntityRenderer<T> {
    public AbstractEffectEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(T pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(-pEntity.getYRot()));
        /*int targetId = pEntity.getTargetIntId();

        Entity ent = pEntity.level().getEntity(targetId);
        if(ent == null)
            pPoseStack.mulPose(Axis.YP.rotationDegrees(-pEntity.getYRot()));
        else{

        }*/
    }
}
