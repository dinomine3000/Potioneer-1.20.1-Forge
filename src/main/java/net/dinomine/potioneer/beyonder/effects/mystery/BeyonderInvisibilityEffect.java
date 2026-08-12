package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderInvisibilityEffect extends BeyonderEffect {
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(!target.hasEffect(MobEffects.INVISIBILITY)){
            target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, (this.maxLife-lifetime), 1, false, false));
        }
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        if(target.hasEffect(MobEffects.INVISIBILITY)){
            target.removeEffect(MobEffects.INVISIBILITY);
            cap.getAbilitiesManager().setAbilityEnabled(Abilities.INVISIBILITY.getAblId(), getSequenceLevel(), false, cap, target);
        }
    }

}
