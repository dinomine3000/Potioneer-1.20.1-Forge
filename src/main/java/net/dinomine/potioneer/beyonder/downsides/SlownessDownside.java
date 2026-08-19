package net.dinomine.potioneer.beyonder.downsides;

import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class SlownessDownside extends Downside{
    public SlownessDownside() {}

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20*10, 2, false, true, true));
        return true;
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(target.getRandom().nextInt(2000) == 5){
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20*30, 5, false, true, true));
        }
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "d_slowness";
    }
}
