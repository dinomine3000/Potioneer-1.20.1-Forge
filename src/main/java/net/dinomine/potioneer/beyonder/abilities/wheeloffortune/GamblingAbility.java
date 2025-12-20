package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderGamblingEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class GamblingAbility extends Ability {

    public GamblingAbility(int sequence){
        this.info = new AbilityInfo(5, 296, "Patience", sequence, 0, getMaxCooldown(), "gambling");
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        if(flipEnable(cap, target)){
            target.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1, 1);
        } else {
            target.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 1);
        }
        return true;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        disable(cap, target);
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < info.cost()) disable(cap, target);
        if(cap.getAbilitiesManager().isEnabled(this)
                && !cap.getEffectsManager().hasEffectOrBetter(BeyonderEffects.EFFECT.WHEEL_GAMBLING, getSequence())){
            BeyonderGamblingEffect effect = (BeyonderGamblingEffect) BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_GAMBLING,
                    getSequence(), info.cost(), -1, true);
            effect.setLuckQuantity(cap.getLuckManager().getLuck());
            cap.getEffectsManager().addOrReplaceEffect(effect, cap, target);
        }
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_GAMBLING, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.WHEEL_GAMBLING, getSequence(), cap, target);
        }
    }
}
