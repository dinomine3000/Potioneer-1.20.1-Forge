package net.dinomine.potioneer.entities.custom;

import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DiceEffectEntity extends AbstractEffectEntity implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public DiceEffectEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, state -> {
            state.getController().setAnimation(RawAnimation.begin().thenPlay("unlucky_animation"));
            return PlayState.CONTINUE;
        })
                .setSoundKeyframeHandler(state -> {
                    // Use helper method to avoid client-code in common class
                    Player player = ClientUtils.getClientPlayer();
                    switch(state.getKeyframeData().getSound()){
                        case "unluck_effect":
                            player.playSound(ModSounds.UNLUCK.get(), 1f, 1.25f - player.getRandom().nextFloat()/2f);
                            break;
                    }
                }));
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount > 20) kill();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
