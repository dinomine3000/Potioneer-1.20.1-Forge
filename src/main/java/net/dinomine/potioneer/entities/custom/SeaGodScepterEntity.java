package net.dinomine.potioneer.entities.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;


public class SeaGodScepterEntity extends DivinationRodEntity implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SeaGodScepterEntity(EntityType<SeaGodScepterEntity> pEntityType, Level pLevel, ItemStack stack) {
        super(pEntityType, pLevel, stack, true);
    }

    public SeaGodScepterEntity(EntityType<SeaGodScepterEntity> pEntityType, Level pLevel){
        this(pEntityType, pLevel, ItemStack.EMPTY);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate)
                .triggerableAnim("divine", RawAnimation.begin().thenPlayAndHold("animation.model.fall")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
