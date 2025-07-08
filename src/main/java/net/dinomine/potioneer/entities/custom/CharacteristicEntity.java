package net.dinomine.potioneer.entities.custom;

import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.util.GeoTintable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CharacteristicEntity extends PlaceableItemEntity implements GeoEntity, GeoTintable {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Integer> BEYONDER_ID = SynchedEntityData.defineId(CharacteristicEntity.class, EntityDataSerializers.INT);

    public CharacteristicEntity(EntityType<CharacteristicEntity> pEntityType, Level pLevel, ItemStack item, int sequenceId) {
        super(pEntityType, pLevel, false, true, true, item);
        entityData.set(BEYONDER_ID, sequenceId);
        setNoGravity(false);
    }

    public CharacteristicEntity(EntityType<CharacteristicEntity> pEntityType, Level pLevel){
        this(pEntityType, pLevel, ItemStack.EMPTY, -1);
    }

    public int getSequenceId(){
        return entityData.get(BEYONDER_ID);
    }

    public void setSequenceId(int sequenceId){
        entityData.set(BEYONDER_ID, sequenceId);
    }

    @Override
    protected InteractionResult onPlayerRClick(Player pPlayer, InteractionHand pHand) {
        return InteractionResult.SUCCESS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public void tick() {
        super.tick();

        Vec3 motion = this.getDeltaMovement();

        if (this.isInWater()) {
            // Sink slowly in water
            motion = motion.multiply(0.9, 0.9, 0.9).add(0, -0.02, 0);
        } else if (isInFluidType()) {
            // Float slowly in lava
            motion = motion.multiply(0.9, 0.5, 0.9).add(0, 0.04, 0);
        } else {
            // Gravity in air
            if (!this.isNoGravity()) {
                motion = motion.add(0, -0.08, 0);
            }
            motion = motion.multiply(0.8, 0.8, 0.8); // air resistance
        }

        this.setDeltaMovement(motion);
        this.move(MoverType.SELF, motion);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public int getHexColor() {
       return Beyonder.getSequenceColorFromId(entityData.get(BEYONDER_ID));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BEYONDER_ID, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(BEYONDER_ID, compoundTag.getInt("b_id"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("b_id", this.entityData.get(BEYONDER_ID));
    }
}
