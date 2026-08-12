package net.dinomine.potioneer.beyonder.effects.redpriest;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderLightBuffEffect extends BeyonderEffect {

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(!target.hasEffect(ModEffects.LIGHT_BUFF.get())){
            target.addEffect(new MobEffectInstance(ModEffects.LIGHT_BUFF.get(), (this.maxLife-lifetime)/2, 1, true, true));
        }
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        if(target.hasEffect(ModEffects.LIGHT_BUFF.get())){
            target.removeEffect(ModEffects.LIGHT_BUFF.get());
        }
    }
}
