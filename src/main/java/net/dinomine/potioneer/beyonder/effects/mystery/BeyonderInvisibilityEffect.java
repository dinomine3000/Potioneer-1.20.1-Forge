package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderInvisibilityEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!target.hasEffect(MobEffects.INVISIBILITY)){
            target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, (this.maxLife-lifetime), 1, false, false));
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.hasEffect(MobEffects.INVISIBILITY)){
            target.removeEffect(MobEffects.INVISIBILITY);
            cap.getAbilitiesManager().disableAbility(Abilities.INVISIBILITY.getAblId(), getSequenceLevel(), cap, target);
        }
    }

}
