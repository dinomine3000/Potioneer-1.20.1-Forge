package net.dinomine.potioneer.entities.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DoorEntity extends Entity implements GeoEntity {
    public static final EntityDataAccessor<Boolean> IS_DYING = SynchedEntityData.defineId(DoorEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> OPEN = SynchedEntityData.defineId(DoorEntity.class, EntityDataSerializers.BOOLEAN);

    public DoorEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void push(Entity pEntity) {
        if (pEntity instanceof Boat) {
            if (pEntity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(pEntity);
            }
        } else if (pEntity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(pEntity);
        }
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return Boat.canVehicleCollide(this, pEntity);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        entityData.set(IS_DYING, true);
        if(entityData.get(OPEN)) triggerAnim("openClose", "close");
        return false;
    }

    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        if(entityData.get(OPEN)) triggerAnim("openClose", "close");
        else triggerAnim("openClose", "open");

        entityData.set(OPEN, !entityData.get(OPEN));
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "openClose", 20, this::openPredicate)
                .triggerableAnim("open", RawAnimation.begin().then("door_open", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("close", RawAnimation.begin().then("door_idle", Animation.LoopType.PLAY_ONCE)));
    }

    protected PlayState openPredicate(AnimationState<DoorEntity> animationState){
        return PlayState.CONTINUE;
    }

    protected PlayState predicate(AnimationState<DoorEntity> animationState) {
        if(entityData.get(IS_DYING)){
            animationState.getController().setAnimation(RawAnimation.begin().then("door_disappear", Animation.LoopType.PLAY_ONCE));
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("door_appear", Animation.LoopType.PLAY_ONCE).then("door_idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private int tickCount = 20;
    @Override
    public void tick() {
        super.tick();
        if(entityData.get(IS_DYING)) tickCount--;
        if(tickCount < 0) kill();
    }

    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(IS_DYING, false);
        entityData.define(OPEN, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        entityData.set(IS_DYING, compoundTag.getBoolean("open"));
        entityData.set(OPEN, compoundTag.getBoolean("dying"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("open", entityData.get(OPEN));
        compoundTag.putBoolean("dying", entityData.get(IS_DYING));
    }
}
