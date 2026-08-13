package net.dinomine.potioneer.entities.custom.effects;

import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.UUID;

public abstract class AbstractEffectEntity extends Entity {
    public static final EntityDataAccessor<Integer> TARGET_INT_ID = SynchedEntityData.defineId(AbstractEffectEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Vector3f> OFFSET = SynchedEntityData.defineId(AbstractEffectEntity.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Boolean> ROTATE_WITH_HEAD = SynchedEntityData.defineId(AbstractEffectEntity.class, EntityDataSerializers.BOOLEAN);
    private LivingEntity targetEntity = null;
    protected UUID targetUUID = null;

    private int breathingRoom = 10;

    public void setTarget(LivingEntity targetEntity){
        if(!level().isClientSide){
            this.targetEntity = targetEntity;
            this.targetUUID = targetEntity.getUUID();
            getEntityData().set(TARGET_INT_ID, targetEntity.getId());
            targetEntity.getCapability(CapProvider.EFFECT_ENTITIES).ifPresent(cap -> {
                cap.addEffect(this);
            });
        }
    }

    public int getTargetIntId(){
        return entityData.get(TARGET_INT_ID);
    }

    public UUID getTargetUUID(){return targetUUID;}
    public LivingEntity getTargetEntity(){
        if(level().isClientSide) return null;
        if(targetEntity != null){
            if(targetEntity.isAlive()) return targetEntity;
            return null;
        }
        if(targetUUID == null){
            System.out.println("[Potioneer] Effect entity with no target set. Deleting...");
            return null;
        }
        if(level() instanceof ServerLevel serverLevel){
            Entity ent = serverLevel.getEntity(targetUUID);
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
        super.tick();
        int targetId = getTargetIntId();
        Entity target = level().getEntity(targetId);
        if (target != null) {
            if(target instanceof LivingEntity lTarget && lTarget.isDeadOrDying()) kill();
            else this.setPos(target.position());
        } else {
            if(breathingRoom-- < 0)
                this.discard();
        }
    }

    public boolean rotatesWithHead(){
        return entityData.get(ROTATE_WITH_HEAD);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OFFSET, new Vector3f());
        this.entityData.define(ROTATE_WITH_HEAD, false);
        this.entityData.define(TARGET_INT_ID, 0);
    }


    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        if(targetEntity == null) return;
        targetEntity.getCapability(CapProvider.EFFECT_ENTITIES).ifPresent(cap -> cap.stopEffect(this));
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if(compoundTag.contains("offsetX")){
            float x = compoundTag.getFloat("offsetX");
            float y = compoundTag.getFloat("offsetY");
            float z = compoundTag.getFloat("offsetZ");
            getEntityData().set(OFFSET, new Vector3f(x, y, z));
        }
        if(compoundTag.contains("targetId")){
            targetUUID = compoundTag.getUUID("targetId");
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
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putFloat("offsetX", getEntityData().get(OFFSET).x());
        compoundTag.putFloat("offsetY", getEntityData().get(OFFSET).y());
        compoundTag.putFloat("offsetZ", getEntityData().get(OFFSET).z());
        compoundTag.putBoolean("rotateHead", getEntityData().get(ROTATE_WITH_HEAD));
        if(targetUUID != null) compoundTag.putUUID("targetId", targetUUID);
    }
}
