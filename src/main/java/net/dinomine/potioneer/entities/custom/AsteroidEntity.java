package net.dinomine.potioneer.entities.custom;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AsteroidEntity extends Entity implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Direction> DIRECTION = SynchedEntityData.defineId(AsteroidEntity.class, EntityDataSerializers.DIRECTION);

    private static final Vec3 IMPACT_VECTOR = new Vec3(1, -3, 0);
    private static final int SUMMON_HEIGHT = 200;
    private static final float SPEED = 1.2f;

    public AsteroidEntity(EntityType<AsteroidEntity> entityEntityType, Level level) {
        super(entityEntityType, level);
    }

    public void setToHit(BlockPos pos, RandomSource random){
        //gets the cardinal direction and calculates the rotation vector
        Direction cardinalDir = Direction.getRandom(random);
        entityData.set(DIRECTION, cardinalDir);
        Vec3 direction = rotateImpactVector(cardinalDir);

        //scales the base vector by the distance to calculate the difference
        //between the target position and the summon position
        int diffY = SUMMON_HEIGHT - pos.getY();
        Vec3 diffVector = direction.scale(diffY/3f);

        //updates the entity position and stuff
        setPos(pos.getCenter().subtract(diffVector));
        setDeltaMovement(direction.scale(SPEED));
        setYRot(cardinalDir.toYRot());
    }

    private static Vec3 rotateImpactVector(Direction dir) {
        switch (dir) {
            case NORTH -> { return new Vec3(IMPACT_VECTOR.x(), IMPACT_VECTOR.y(), IMPACT_VECTOR.z()); }
            case EAST  -> { return new Vec3(-IMPACT_VECTOR.z(), IMPACT_VECTOR.y(), IMPACT_VECTOR.x()); }
            case SOUTH -> { return new Vec3(-IMPACT_VECTOR.x(), IMPACT_VECTOR.y(), -IMPACT_VECTOR.z()); }
            case WEST  -> { return new Vec3(IMPACT_VECTOR.z(), IMPACT_VECTOR.y(), -IMPACT_VECTOR.x()); }
        }
        return IMPACT_VECTOR;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, state -> {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("rotate"));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public void tick() {
        super.tick();

        if(onGround()){
            if(!level().isClientSide())
                level().explode(this, PotioneerDamage.asteroid((ServerLevel) level()), null, position(), 5, true,
                        PotioneerCommonConfig.DESTRUCTION_LEVEL_ENUM_VALUE.get() != PotioneerCommonConfig.DestructionLevel.NEVER ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
            kill();
        } else {
            Vec3 motion = rotateImpactVector(entityData.get(DIRECTION)).scale(SPEED);
            this.setDeltaMovement(motion);
            this.move(MoverType.SELF, motion);
        }

        level().addParticle(ParticleTypes.EXPLOSION, true, xOld + random.nextFloat(), yOld, zOld + random.nextFloat(), 0, 0.5, 0);
//        level().addParticle(ParticleTypes.EXPLOSION, true, xOld + random.nextFloat(), yOld, zOld + random.nextFloat(), 0, 0.5, 0);

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DIRECTION, Direction.EAST);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        entityData.set(DIRECTION, Direction.from3DDataValue(compoundTag.getInt("data_val_dir")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        Direction dir = entityData.get(DIRECTION);
        compoundTag.putInt("data_val_dir", dir.get3DDataValue());
    }
}
