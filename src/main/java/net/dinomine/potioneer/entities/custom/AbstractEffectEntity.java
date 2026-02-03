package net.dinomine.potioneer.entities.custom;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.UUID;

public class AbstractEffectEntity extends Entity {
    public static final EntityDataAccessor<Vector3f> TARGET_POS = SynchedEntityData.defineId(AbstractEffectEntity.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Float> ROTATION = SynchedEntityData.defineId(AbstractEffectEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Vector3f> OFFSET = SynchedEntityData.defineId(AbstractEffectEntity.class, EntityDataSerializers.VECTOR3);
    private UUID targetId = null;
    private LivingEntity targetEntity = null;

    public void setTarget(UUID targetId){
        if(level() instanceof ServerLevel lvl){
            if(lvl.getEntity(targetId) instanceof LivingEntity living){
                targetEntity = living;
                this.targetId = targetId;
                getEntityData().set(TARGET_POS, living.position().toVector3f());
            } else {
                System.err.println("Warning: Attempted to set effect entity target as a non-living entity!");
            }
        }
    }

    protected void setOffset(Vector3f offset){
        getEntityData().set(OFFSET, offset);
    }

    public AbstractEffectEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        if(!level().isClientSide()){
            if(targetId == null){
                kill();
                return;
                //System.err.println("Error: no target entity set for charm");
            }
            if(targetEntity == null){
                targetEntity = (LivingEntity) ((ServerLevel) level()).getEntity(targetId);
                if(targetEntity == null) kill();
            }
            if(targetEntity != null){
                Vector3f offset = new Vector3f(getEntityData().get(OFFSET));

                float yawRad = (float) Math.toRadians(-getYRot());
                offset.rotateY(yawRad);

                Vector3f targetPos = targetEntity.position().toVector3f();

                getEntityData().set(TARGET_POS, targetPos.add(offset));
            }
            if(targetEntity != null)
                getEntityData().set(ROTATION, targetEntity.getYRot());
        }
        Vector3f targetPos = getEntityData().get(TARGET_POS);
        this.setPos(new Vec3(targetPos));
        setYRot(getEntityData().get(ROTATION));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TARGET_POS, new Vector3f());
        this.entityData.define(ROTATION, 0f);
        this.entityData.define(OFFSET, new Vector3f());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if(compoundTag.contains("tarX")){
            float x = compoundTag.getFloat("tarX");
            float y = compoundTag.getFloat("tarY");
            float z = compoundTag.getFloat("tarZ");
            getEntityData().set(TARGET_POS, new Vector3f(x, y, z));
        }
        if(compoundTag.contains("offsetX")){
            float x = compoundTag.getFloat("offsetX");
            float y = compoundTag.getFloat("offsetY");
            float z = compoundTag.getFloat("offsetZ");
            getEntityData().set(OFFSET, new Vector3f(x, y, z));
        }
        if(compoundTag.contains("targetId"))
            targetId = compoundTag.getUUID("targetId");

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putFloat("tarX", getEntityData().get(TARGET_POS).x());
        compoundTag.putFloat("tarY", getEntityData().get(TARGET_POS).y());
        compoundTag.putFloat("tarZ", getEntityData().get(TARGET_POS).z());
        compoundTag.putFloat("offsetX", getEntityData().get(OFFSET).x());
        compoundTag.putFloat("offsetY", getEntityData().get(OFFSET).y());
        compoundTag.putFloat("offsetZ", getEntityData().get(OFFSET).z());
        compoundTag.putUUID("targetId", targetId);
    }
}
