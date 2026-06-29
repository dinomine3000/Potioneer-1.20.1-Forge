package net.dinomine.potioneer.entities.custom.effects;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public abstract class AbstractEffectEntity extends Entity {
    public static final EntityDataAccessor<Vector3f> TARGET_POS = SynchedEntityData.defineId(AbstractEffectEntity.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Float> ROTATION = SynchedEntityData.defineId(AbstractEffectEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> TARGET_INT_ID = SynchedEntityData.defineId(AbstractEffectEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Vector3f> OFFSET = SynchedEntityData.defineId(AbstractEffectEntity.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Boolean> ROTATE_WITH_HEAD = SynchedEntityData.defineId(AbstractEffectEntity.class, EntityDataSerializers.BOOLEAN);
    private LivingEntity targetEntity = null;
    protected UUID targetId = null;

    public void setTarget(LivingEntity targetEntity){
        if(!level().isClientSide){
            this.targetEntity = targetEntity;
            this.targetId = targetEntity.getUUID();
            getEntityData().set(TARGET_POS, targetEntity.position().toVector3f());
            getEntityData().set(TARGET_INT_ID, targetEntity.getId());
            targetEntity.getCapability(BeyonderStatsProvider.EFFECT_ENTITIES).ifPresent(cap -> {
                cap.addEffect(this);
            });
        }
    }

    public int getTargetIntId(){
        return entityData.get(TARGET_INT_ID);
    }

    public UUID getTargetId(){return targetId;}
    public LivingEntity getTargetEntity(){
        if(level().isClientSide) return null;
        if(targetEntity != null) return targetEntity;
        if(targetId == null){
            System.out.println("[Potioneer] Effect entity with no target set. Deleting...");
            kill();
            return null;
        }
        if(level() instanceof ServerLevel serverLevel){
            Entity ent = serverLevel.getEntity(targetId);
            if(ent instanceof LivingEntity living)
                targetEntity = living;
        }

        return targetEntity;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    public void setOffset(Vector3f offset){
        getEntityData().set(OFFSET, offset);
    }

    public Vector3f getOffset(){return entityData.get(OFFSET);}

    public AbstractEffectEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        if(!level().isClientSide()){
            LivingEntity target = getTargetEntity();
            if(target == null) return;

            Vector3f targetPos = target.position().toVector3f();
            getEntityData().set(TARGET_POS, targetPos);
        }
        Vector3f targetPos = getEntityData().get(TARGET_POS);
        this.setPos(new Vec3(targetPos));
    }

    public boolean rotatesWithHead(){
        return entityData.get(ROTATE_WITH_HEAD);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TARGET_POS, new Vector3f());
        this.entityData.define(ROTATION, 0f);
        this.entityData.define(OFFSET, new Vector3f());
        this.entityData.define(ROTATE_WITH_HEAD, false);
        this.entityData.define(TARGET_INT_ID, 0);
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
        if(compoundTag.contains("targetId")){
            targetId = compoundTag.getUUID("targetId");
            LivingEntity target = getTargetEntity();
            if(target != null)
                entityData.set(TARGET_INT_ID, target.getId());
        }
        if(compoundTag.contains("rotateHead")){
            boolean rotateHead = compoundTag.getBoolean("rotateHead");
            getEntityData().set(ROTATE_WITH_HEAD, rotateHead);
        }

    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        if(targetEntity == null) return;
        targetEntity.getCapability(BeyonderStatsProvider.EFFECT_ENTITIES).ifPresent(cap -> cap.stopEffect(this));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putFloat("tarX", getEntityData().get(TARGET_POS).x());
        compoundTag.putFloat("tarY", getEntityData().get(TARGET_POS).y());
        compoundTag.putFloat("tarZ", getEntityData().get(TARGET_POS).z());
        compoundTag.putFloat("offsetX", getEntityData().get(OFFSET).x());
        compoundTag.putFloat("offsetY", getEntityData().get(OFFSET).y());
        compoundTag.putFloat("offsetZ", getEntityData().get(OFFSET).z());
        compoundTag.putBoolean("rotateHead", getEntityData().get(ROTATE_WITH_HEAD));
        if(targetId != null)
           compoundTag.putUUID("targetId", targetId);
    }
}
