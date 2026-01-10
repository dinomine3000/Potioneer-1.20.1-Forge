package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderPatienceEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class PatienceAbility extends PassiveAbility {

    public PatienceAbility(int sequence){
//        this.info = new AbilityInfo(5, 296, "Patience", sequence, 0, getMaxCooldown(), "gambling");
        super(sequence, BeyonderEffects.WHEEL_PATIENCE, level -> "patience");
        setCost(level -> 0);
    }

    @Override
    public boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        if(flipEnable(cap, target)){
            target.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1, 1);
        } else {
            target.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1, 1);
        }
        return putOnCooldown(target);
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        setEnabled(cap, target, false);
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost()) setEnabled(cap, target, false);
        if(isEnabled()
                && !cap.getEffectsManager().hasEffectOrBetter(BeyonderEffects.WHEEL_PATIENCE.getEffectId(), getSequenceLevel())){
            BeyonderPatienceEffect effect = (BeyonderPatienceEffect) BeyonderEffects.byId(BeyonderEffects.WHEEL_PATIENCE.getEffectId(),
                    getSequenceLevel(), cost(), -1, true);
            effect.setLuckQuantity(cap.getLuckManager().getLuck());
            cap.getEffectsManager().addOrReplaceEffect(effect, cap, target);
        }
    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.WHEEL_PATIENCE.getEffectId(), getSequenceLevel())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.WHEEL_PATIENCE.getEffectId(), getSequenceLevel());
        }
    }
}
