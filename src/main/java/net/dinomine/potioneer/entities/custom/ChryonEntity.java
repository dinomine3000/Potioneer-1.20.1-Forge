package net.dinomine.potioneer.entities.custom;

import net.dinomine.potioneer.entities.goals.ChryonPierceGoal;
import net.dinomine.potioneer.entities.goals.ChryonSwingGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;

public class ChryonEntity extends Monster implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int pierceCooldown;
    public int pierceTime;
    public int swingTime;
    public static final EntityDataAccessor<Boolean> IS_PIERCING = SynchedEntityData.defineId(ChryonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_SWINGING = SynchedEntityData.defineId(ChryonEntity.class, EntityDataSerializers.BOOLEAN);


    public ChryonEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        pierceCooldown = 0;
        pierceTime = 0;
        swingTime = 0;
        this.xpReward = 25;
    }

    public boolean isPiercing(){
        return this.entityData.get(IS_PIERCING);
    }

    public void setPiercing(boolean pierc){
        this.entityData.set(IS_PIERCING, pierc);
    }

    public boolean isSwinging(){
        return this.entityData.get(IS_SWINGING);
    }

    public void setSwinging(boolean pierc){
        this.entityData.set(IS_SWINGING, pierc);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_PIERCING, false);
        this.entityData.define(IS_SWINGING, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "pierceController", 0, this::piercePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "swingController", 0, this::swingPredicate));
    }

    public static boolean canSpawn(EntityType<ChryonEntity> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random){
        return Mob.checkMobSpawnRules(entityType, level, spawnType, pos, random) && pos.getY() > 60;
    }

    private PlayState piercePredicate(AnimationState<ChryonEntity> event){
        if(!this.isPiercing()){
            return PlayState.STOP;
        }
        event.getController().setAnimation(RawAnimation.begin().thenPlay("pierce_attack"));
        return PlayState.CONTINUE;
    }

    private PlayState swingPredicate(AnimationState<ChryonEntity> event){
        if(!this.isSwinging()){
            return PlayState.STOP;
        }
        event.getController().setAnimation(RawAnimation.begin().thenPlay("sword_attack"));
        return PlayState.CONTINUE;
    }

    private PlayState predicate(AnimationState<ChryonEntity> chryonEntityAnimationState) {
        if(this.isPiercing() || this.isSwinging()){
            return PlayState.STOP;
        }
        if(chryonEntityAnimationState.isMoving()){
            chryonEntityAnimationState.getController().setAnimation(RawAnimation.begin().thenPlay("walk"));
            return PlayState.CONTINUE;
        }
        chryonEntityAnimationState.getController().setAnimation(RawAnimation.begin().thenPlay("idle_shiver"));
        return PlayState.CONTINUE;
    }

    public boolean canPierce(){
        return pierceCooldown < 1 && !this.isSwinging();
    }

    public boolean canSwing(){
        return !this.isPiercing();
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide()){
            if(pierceCooldown > 0) {
                pierceCooldown--;
            }


            if(this.isPiercing()){
                this.pierceTime++;
                if(this.canPierce() && this.pierceTime > 20 && this.getTarget() != null){
                    double dist = this.getPerceivedTargetDistanceSquareForMeleeAttack(this.getTarget());
                    double reach = this.getAttackReachSqr(this.getTarget());
                    if (dist <= reach && this.isAlive()) {
                        //TODO
                        //make custom damage source
                        //DamageSource dmg = new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypesRegistry.CHRYON_PIERCE));
                        this.getTarget().hurt(this.damageSources().mobAttack(this), 12);
                    }
                    this.pierceCooldown = 200;
                } if (this.pierceTime > 50) {
                    this.setPiercing(false);
                    this.pierceTime = 0;
                }
            }

            if(this.isSwinging()){
                this.swingTime++;
                if(this.canSwing() && this.swingTime == 20 && this.getTarget() != null){
                    double dist = this.getPerceivedTargetDistanceSquareForMeleeAttack(this.getTarget());
                    double reach = getAttackReachSqr(this.getTarget());
                    if (dist <= reach && this.isAlive()) {
                        this.doHurtTarget(this.getTarget());
                    }
                } if (this.swingTime > 50) {
                    this.setSwinging(false);
                    this.swingTime = 0;
                }
            }
        }
        
    }


    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        return 1.6 * (double)(this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + pAttackTarget.getBbWidth());
    }
    
    @Override
    public int getCurrentSwingDuration() {
        return 60;
    }

    public static AttributeSupplier setAttributes(){
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50d)
                .add(Attributes.ATTACK_DAMAGE, 11)
                .add(Attributes.ATTACK_SPEED, 0.2f)
                .add(Attributes.ARMOR, 20f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 5f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f).build();
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ChryonPierceGoal(this, 1.2D));
        this.goalSelector.addGoal(3, new ChryonSwingGoal(this, 1.2D));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
