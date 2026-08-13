package net.dinomine.potioneer.entities.custom.effects;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class WaterBlockEffectEntity extends AbstractEffectEntity {
    public static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(WaterBlockEffectEntity.class, EntityDataSerializers.INT);
    public WaterBlockEffectEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        entityData.set(ROTATE_WITH_HEAD, true);
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    public void setDuration(int duration){entityData.set(DURATION, duration);}

    @Override
    public void tick() {
        super.tick();
        if(tickCount > entityData.get(DURATION)){
            kill();
            return;
        }
        if(!level().isClientSide && getTargetEntity() != null){
            getTargetEntity().getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
                if(!cap.getEffectsManager().hasEffect(BeyonderEffects.TYRANT_DROWNING)) kill();
            });
        }
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
        compoundTag.putInt("drowningDuration", getEntityData().get(DURATION) - tickCount);
    }
}
