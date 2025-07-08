package net.dinomine.potioneer.entities.custom;

import net.dinomine.potioneer.entities.goals.CactusRunGoal;
import net.dinomine.potioneer.entities.goals.CactusWanderGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WanderingCactusEntity extends Animal implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Boolean> WANDERING = SynchedEntityData.defineId(WanderingCactusEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HIDING = SynchedEntityData.defineId(WanderingCactusEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(WanderingCactusEntity.class, EntityDataSerializers.BOOLEAN);
    private int wanderCd = 0;
    private int justHit = 0;

    public WanderingCactusEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 10;
    }

    public boolean isHiding(){
        return this.entityData.get(HIDING);
    }

    public void setHiding(boolean hiding){
        this.entityData.set(HIDING, hiding);
    }

    public boolean isRunning(){
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean running){
        this.entityData.set(RUNNING, running);
    }

    public boolean isWandering(){
        return this.entityData.get(WANDERING);
    }

    public void setWandering(boolean wander){
        this.entityData.set(WANDERING, wander);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HIDING, true);
        this.entityData.define(WANDERING, false);
        this.entityData.define(RUNNING, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "wandering_controller", 0, this::wanderingPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "hiding_controller", 0, this::hidingPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "running_controller", 0, this::runningPredicate));
    }

    private PlayState runningPredicate(AnimationState<WanderingCactusEntity> state) {
        if(!isRunning()){
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        }
        state.getController().setAnimation(RawAnimation.begin().thenLoop("walking"));
        return PlayState.CONTINUE;
    }

    public static boolean canSpawn(EntityType<WanderingCactusEntity> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random){
        return pos.getY() > 60;
    }

    private PlayState hidingPredicate(AnimationState<WanderingCactusEntity> state) {
        if(!isHiding()){
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        }
        state.getController().setAnimation(RawAnimation.begin().then("hidding", Animation.LoopType.PLAY_ONCE).thenLoop("idle_covered"));
        return PlayState.CONTINUE;
    }

    private PlayState wanderingPredicate(AnimationState<WanderingCactusEntity> state) {
        if(isHiding() || isRunning()) return PlayState.STOP;

        if(!isWandering()){
            state.getController().setAnimation(RawAnimation.begin().then("appearing", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }

        if(state.isMoving()){
            state.getController().setAnimation(RawAnimation.begin().then("walking", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        state.getController().setAnimation(RawAnimation.begin().then("idle_standing", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(isHiding() && pSource.getEntity() != null ) pSource.getEntity().level().playSound(null, getOnPos(), SoundEvents.METAL_HIT, SoundSource.HOSTILE);
        justHit = (pSource.getEntity() instanceof Player player && player.isCreative()) ? 0 : 20*5;
        if(pSource.getEntity() != null) pSource.getEntity().hurt(damageSources().mobAttack(this), pAmount/3f);
        if(isHiding()) pAmount /= 3f;
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide()) return;
        if(justHit-- > 0){
            setRunning(true);
            setHiding(false);
            setWandering(false);
        } else {
            setRunning(false);
            if(level().isDay()){
                wanderCd = 0;
                setHiding(true);
                setWandering(false);
            } else {
                setHiding(false);
                if(wanderCd > 40){
                    setWandering(true);
                } else {
                    wanderCd++;
                }
            }
        }
    }

    public void snapToCenterAndFaceCardinal() {
        BlockPos blockPos = this.blockPosition(); // current block position
        double centerX = blockPos.getX() + 0.5;
        double centerY = this.getY(); // keep same Y height
        double centerZ = blockPos.getZ() + 0.5;

        // Move to center of block
        move(MoverType.SELF, new Vec3(centerX, centerY, centerZ));

        // Snap rotation to closest cardinal direction (yaw)
        float yaw = this.getYRot() % 360;
        if (yaw < 0) yaw += 360;

        float snappedYaw;
        if (yaw >= 45 && yaw < 135) {
            snappedYaw = 90f;  // East
        } else if (yaw >= 135 && yaw < 225) {
            snappedYaw = 180f; // South
        } else if (yaw >= 225 && yaw < 315) {
            snappedYaw = 270f; // West
        } else {
            snappedYaw = 0f;   // North
        }

        this.setYRot(snappedYaw);
        this.setYBodyRot(snappedYaw);  // if it's a Mob
        this.setYHeadRot(snappedYaw);  // optional, if applicable
    }


    public static AttributeSupplier setAttributes(){
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20d)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.ATTACK_SPEED, 0.2f)
                .add(Attributes.ARMOR, 10f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 5f)
                .add(Attributes.MOVEMENT_SPEED, 0.8f).build();
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new CactusWanderGoal(this));
        //this.goalSelector.addGoal(3, new CactusHideGoal(this));

        this.targetSelector.addGoal(3, new CactusRunGoal<>(this, Player.class, 10f, 1d, 1d));

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
