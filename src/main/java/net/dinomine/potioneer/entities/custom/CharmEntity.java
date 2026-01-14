package net.dinomine.potioneer.entities.custom;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.entities.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class CharmEntity extends Entity implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final EntityDataAccessor<Vector3f> TARGET_POS = SynchedEntityData.defineId(CharmEntity.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Boolean> DISTANCE_CHECK = SynchedEntityData.defineId(CharmEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> PATHWAY_ID = SynchedEntityData.defineId(CharmEntity.class, EntityDataSerializers.INT);
    private UUID targetId = null;
    private LivingEntity targetEntity = null;
//    private UUID targetId;
    private int tick = 0;
    public float yaw = 0;
    private BeyonderEffect effect = null;

    public CharmEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static CharmEntity createCharm(UUID targetId, BeyonderEffect effect, Player player, int pathwayId){
        CharmEntity charmEnt = new CharmEntity(ModEntities.CHARM_ENTITY.get(), player.level());
        charmEnt.setTarget(targetId);
        charmEnt.setPos(player.position());
        charmEnt.setEffect(effect);
        charmEnt.getEntityData().set(PATHWAY_ID, Math.floorDiv(pathwayId, 10));
        return charmEnt;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DISTANCE_CHECK, false);
        this.entityData.define(TARGET_POS, new Vector3f());
        this.entityData.define(PATHWAY_ID, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        getEntityData().set(DISTANCE_CHECK, compoundTag.getBoolean("distanceCheck"));
        if(compoundTag.contains("tarX")){
            float x = compoundTag.getFloat("tarX");
            float y = compoundTag.getFloat("tarY");
            float z = compoundTag.getFloat("tarZ");
            getEntityData().set(TARGET_POS, new Vector3f(x, y, z));
        }
        if(compoundTag.contains("targetId"))
            targetId = compoundTag.getUUID("targetId");
        if(compoundTag.contains("associatedEffect")){
            CompoundTag effTag = compoundTag.getCompound("associatedEffect");
            BeyonderEffect effect = BeyonderEffects.byId(
                    effTag.getString("ID"),
                    effTag.getInt("level"),
                    effTag.getInt("cost"),
                    effTag.getInt("maxLife"),
                    effTag.getBoolean("active"));
            effect.setLifetime(effTag.getInt("lifetime"));
            effect.loadNBTData(effTag);
            this.effect = effect;
        }
        getEntityData().set(PATHWAY_ID, compoundTag.getInt("pathwayId"));

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("distanceCheck", getEntityData().get(DISTANCE_CHECK));
        compoundTag.putFloat("tarX", getEntityData().get(TARGET_POS).x());
        compoundTag.putFloat("tarY", getEntityData().get(TARGET_POS).y());
        compoundTag.putFloat("tarZ", getEntityData().get(TARGET_POS).z());
        compoundTag.putUUID("targetId", targetId);
        CompoundTag effectTag = new CompoundTag();
        effect.toNbt(effectTag);
        compoundTag.put("associatedEffect", effectTag);
        compoundTag.putInt("pathwayId", getEntityData().get(PATHWAY_ID));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<CharmEntity> charmEntityAnimationState) {
        if(getEntityData().get(DISTANCE_CHECK)){
            charmEntityAnimationState.setAnimation(RawAnimation.begin().then("strike", Animation.LoopType.PLAY_ONCE));
        } else {
            charmEntityAnimationState.setAnimation(RawAnimation.begin().then("flying", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    public void setEffect(BeyonderEffect effect){
        this.effect = effect;
    }

    public void setTarget(UUID targetId){
        if(level() instanceof ServerLevel lvl){
            if(lvl.getEntity(targetId) instanceof LivingEntity living){
                targetEntity = living;
                this.targetId = targetId;
                getEntityData().set(TARGET_POS, living.position().toVector3f());
            } else {
                System.err.println("Warning: Attempted to set charm target as a non-living entity!");
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide()){
            if(targetId == null || effect == null){
                kill();
                return;
                //System.err.println("Error: no target entity set for charm");
            }
            if(targetEntity == null){
                targetEntity = (LivingEntity) ((ServerLevel) level()).getEntity(targetId);
                if(targetId == null) kill();
            }
            if(targetEntity != null)
                getEntityData().set(TARGET_POS, targetEntity.position().toVector3f());
        }
        Vector3f targetPos = getEntityData().get(TARGET_POS);

        if(random.nextFloat() < 0.6){
            level().addParticle(ParticleTypes.GLOW, xOld, yOld, zOld, 1, 0, 0);
        }
        if(entityData.get(DISTANCE_CHECK)){
            Vec3 newPos = new Vec3(targetPos).add(0, 1, 0);
            setPos(newPos);
            if(tick++ > 20*2 - 5 && !level().isClientSide()){
                applyEffect(targetEntity);
                kill();
            }
        } else {
            Vector3f diffVector = new Vector3f(targetPos).sub(position().toVector3f());
            double dist = diffVector.length();
            if(dist < 2){
                entityData.set(DISTANCE_CHECK, true);
            }
            Vector3f motion = diffVector.normalize().mul(0.5f);

            faceTarget(targetPos);
            setYRot(yaw);
            this.move(MoverType.SELF, new Vec3(motion));
        }
    }

    private void applyEffect(LivingEntity target){
        if(target == null){
            System.out.println("Warning: Charm entity target is null when tried to apply effect!");
            return;
        }
        target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            level().playSound(null, target, SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 2, 1);
            cap.getEffectsManager().addOrReplaceEffect(this.effect, cap, target);
        });
    }

    private void faceTarget(Vector3f target) {
        // Positions
        double dx = target.x() - getX();
        double dz = target.z() - getZ();

        // Calculate angle in degrees (-180 to 180)
        double angle = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;

        // Normalize to [-180, 180]
        yaw = (float) Mth.wrapDegrees(angle);
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
