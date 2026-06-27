package net.dinomine.potioneer.entities.custom.effects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.UUID;

public class WaterBlockEffectEntity extends AbstractEffectEntity {
    public static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(WaterBlockEffectEntity.class, EntityDataSerializers.INT);
    public WaterBlockEffectEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    public void setDuration(int duration){entityData.set(DURATION, duration);}

    @Override
    public void tick() {
        super.tick();
        if(tickCount > entityData.get(DURATION) || (targetEntity != null && targetEntity.isDeadOrDying()))
            kill();
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DURATION, 20*5);

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if(compoundTag.contains("drowningDuration")){
            getEntityData().set(DURATION, compoundTag.getInt("drowningDuration"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("drowningDuration", getEntityData().get(DURATION));
    }
}
