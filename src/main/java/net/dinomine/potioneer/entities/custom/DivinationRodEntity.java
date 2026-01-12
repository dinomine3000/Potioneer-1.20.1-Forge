package net.dinomine.potioneer.entities.custom;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.util.misc.DivinationResult;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;


public class DivinationRodEntity extends PlaceableItemEntity implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Boolean> DIVINING = SynchedEntityData.defineId(DivinationRodEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> INTENDED_YAW = SynchedEntityData.defineId(DivinationRodEntity.class, EntityDataSerializers.FLOAT);

    protected DivinationRodEntity(EntityType<SeaGodScepterEntity> pEntityType, Level pLevel, ItemStack stack, boolean test){
        super(pEntityType, pLevel, true, true, true, stack);
    }

    public DivinationRodEntity(EntityType<DivinationRodEntity> pEntityType, Level pLevel, ItemStack stack) {
        super(pEntityType, pLevel, true, true, true, stack);
    }

    public DivinationRodEntity(EntityType<DivinationRodEntity> pEntityType, Level pLevel){
        this(pEntityType, pLevel, ItemStack.EMPTY);
    }

    @Override
    public void moveTo(double pX, double pY, double pZ, float pYRot, float pXRot) {
        super.moveTo(pX, pY, pZ, pYRot, pXRot);
        setRotation(pYRot);
    }

    public void setRotation(float yaw){
        setYRot(yaw);
        this.entityData.set(INTENDED_YAW, yaw);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate)
                .triggerableAnim("divine", RawAnimation.begin().thenPlayAndHold("animation.model.fall")));
    }

    protected PlayState predicate(AnimationState<DivinationRodEntity> divinationRodEntityAnimationState) {
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected InteractionResult onPlayerRClick(Player pPlayer, InteractionHand pHand) {
        if(pPlayer.level().isClientSide()){
            //on client side, you always update its animation to match its synched data
            //this works bc the client side is called after the server runs
            //so server interact() runs, it sets DIVING to the proper value, and then the client plays the corresponding animation
            triggerAnimation();
            return InteractionResult.SUCCESS;
        }

        if(this.entityData.get(DIVINING)){
            //if, on RClick, the rods status is DIVINING, reset it back to not-diving status
            flipDivination();
            triggerAnimation();
            return InteractionResult.SUCCESS;
        }

        ItemStack target = pPlayer.getMainHandItem();
//        System.out.println("Divining item: " + target);
        int sequenceId = -1;
        boolean seer = false;
        if(pPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent()){
            sequenceId = pPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get().getPathwaySequenceId();
            seer = pPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get().getEffectsManager().hasEffect(BeyonderEffects.MISC_DIVINATION.getEffectId());
        }

        DivinationResult result = MysticismHelper.doDivination(target, pPlayer, 128, this.getOnPos(), sequenceId, pPlayer.getRandom());
        if(result.positions().isEmpty()){
            if(!seer){
                this.entityData.set(INTENDED_YAW, pPlayer.getRandom().nextFloat()*360);
//                System.out.println("Nothing found. Randomizing...");
                flipDivination();
                triggerAnimation();
            }
        } else {
            ArrayList<BlockPos> positions = new ArrayList<>(result.positions());
            positions.sort((a, b) -> this.getOnPos().distManhattan(a) - this.getOnPos().distManhattan(b));
            positions = new ArrayList<>(positions.stream().filter(pos -> pPlayer.getOnPos().distManhattan(pos) > 1).toList());
            if(positions.isEmpty()) positions = new ArrayList<>(result.positions());
//            System.out.println("Found positions: " + positions);
            if(seer){
                this.entityData.set(INTENDED_YAW, getYawFromPosToPos(this.getOnPos(), positions.get(0)));
//                System.out.println("Telling you the way");
            } else {
                this.entityData.set(INTENDED_YAW, pPlayer.getRandom().nextFloat()*360);
//                System.out.println("Not a seer, i dont car");
            }
            flipDivination();
            triggerAnimation();
        }

        if(seer){
            pPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.requestActiveSpiritualityCost(MysticismHelper.divinationCost);
            });
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();

        this.yRotO = this.getYRot();

        float currentYaw = this.getYRot();
        float targetYaw = this.getEntityData().get(INTENDED_YAW);

        float newYaw = lerpRotation(currentYaw, targetYaw, 1 / 8f);
        this.setYRot(newYaw);
        this.setRot(newYaw, this.getXRot());
    }

    public static float lerpRotation(float currentYaw, float targetYaw, float delta) {
        float deltaYaw = wrapDegrees(targetYaw - currentYaw);
        return currentYaw + deltaYaw * delta;
    }

    public static float wrapDegrees(float degrees) {
        degrees = degrees % 360.0F;
        if (degrees >= 180.0F) {
            degrees -= 360.0F;
        }
        if (degrees < -180.0F) {
            degrees += 360.0F;
        }
        return degrees;
    }

    public static float getYawFromPosToPos(BlockPos from, BlockPos to) {
        double dx = to.getX() + 0.5 - (from.getX() + 0.5);
        double dz = to.getZ() + 0.5 - (from.getZ() + 0.5);

        double angleRad = Math.atan2(-dx, dz);
        return (float) Math.toDegrees(angleRad);
    }


    //called AFTER flipDivination
    public void triggerAnimation(){
        if(!this.entityData.get(DIVINING)){
            stopTriggeredAnimation("controller", "divine");
        } else {
            triggerAnim("controller", "divine");
        }
    }

    public void flipDivination(){
        if(this.entityData.get(DIVINING)){
            this.entityData.set(DIVINING, false);
        } else {
            this.entityData.set(DIVINING, true);
        }
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DIVINING, false);
        this.entityData.define(INTENDED_YAW, 0f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(DIVINING, compoundTag.getBoolean("divining"));
        this.entityData.set(INTENDED_YAW, compoundTag.getFloat("yaw"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("divining", this.entityData.get(DIVINING));
        compoundTag.putFloat("yaw", this.entityData.get(INTENDED_YAW));
    }

    public AABB getBoundingBoxForCulling() {
        AABB aabb = this.getBoundingBox();
        return aabb;
    }
}
