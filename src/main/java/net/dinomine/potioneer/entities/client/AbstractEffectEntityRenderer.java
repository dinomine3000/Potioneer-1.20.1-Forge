package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.entities.custom.effects.AbstractEffectEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

public abstract class AbstractEffectEntityRenderer<T extends AbstractEffectEntity> extends EntityRenderer<T> implements IAbstractEffectRenderer{
    public AbstractEffectEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    /*
    *
    @Override
    public void tick() {
        if(!level().isClientSide()){
            LivingEntity target = getTargetEntity();
            if(target == null) return;
            Vector3f offset = new Vector3f(getEntityData().get(OFFSET));

            float targetRotation = rotatesWithHead() ? target.getYHeadRot() : target.getYRot();
            float yawRad = (float) Math.toRadians(-targetRotation);
            offset.rotateY(yawRad);

            Vector3f targetPos = target.position().toVector3f();

            getEntityData().set(TARGET_POS, targetPos);
            getEntityData().set(ROTATION, targetRotation);
        }
        Vector3f targetPos = getEntityData().get(TARGET_POS);
        this.setPos(new Vec3(targetPos));
        setYRot(getEntityData().get(ROTATION));
    }
    * */
    @Override
    public void render(T pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        //pPoseStack.mulPose(Axis.YP.rotationDegrees(-pEntity.getYRot()));
        renderEffect(pEntity, pPoseStack, pPartialTick);
    }
}
