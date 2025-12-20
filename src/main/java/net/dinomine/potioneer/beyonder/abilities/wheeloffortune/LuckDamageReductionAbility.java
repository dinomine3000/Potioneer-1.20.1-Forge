package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class LuckDamageReductionAbility extends Ability {

    public LuckDamageReductionAbility(int sequence){
        this.info = new AbilityInfo(5, 224, "Damage Reduction", sequence, 50, getMaxCooldown(), "luck_damage_reduction");
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }


    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {}

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < info.cost()) disable(cap, target);
        if(cap.getAbilitiesManager().isEnabled(this)){
            cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_DAMAGE_REDUCE,
                    getSequence(), info.cost(), -1, true), cap, target);
        }
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_DAMAGE_REDUCE, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.WHEEL_DAMAGE_REDUCE, getSequence(), cap, target);
        }
    }
}
