package net.dinomine.potioneer.entities.custom.effects;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SlotMachineEntity extends AbstractEffectEntity implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SUCCESS = SynchedEntityData.defineId(SlotMachineEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public SlotMachineEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setOffset(new Vector3f(0, 2.5f, -1));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, state -> {
            if(state.getAnimatable().getEntityData().get(SUCCESS)){
                state.getController().setAnimation(RawAnimation.begin().thenPlay("success"));
            } else {
                state.getController().setAnimation(RawAnimation.begin().thenPlay("fail"));
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount > 3.5*20) kill();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SUCCESS, false);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void setSuccess(boolean success) {
        getEntityData().set(SUCCESS, success);
    }
}
