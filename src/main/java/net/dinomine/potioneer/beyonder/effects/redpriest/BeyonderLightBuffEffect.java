package net.dinomine.potioneer.beyonder.effects.redpriest;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderLightBuffEffect extends BeyonderEffect {

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!target.hasEffect(ModEffects.LIGHT_BUFF.get())){
            target.addEffect(new MobEffectInstance(ModEffects.LIGHT_BUFF.get(), (this.maxLife-lifetime)/2, 1, true, true));
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.hasEffect(ModEffects.LIGHT_BUFF.get())){
            target.removeEffect(ModEffects.LIGHT_BUFF.get());
        }
    }
}
