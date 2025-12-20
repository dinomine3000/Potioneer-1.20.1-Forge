package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class StepUpAbility extends Ability {

    public StepUpAbility(int sequence){
        this.info = new AbilityInfo(57, 152, "Step Assist", 20 + sequence, 0, this.getMaxCooldown(), "step_up");
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        activate(cap, target);
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() || !isEnabled(cap.getAbilitiesManager())) return;
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_STEP, getSequence(), info.cost(), -1, true),
                cap, target);
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_STEP, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MYSTERY_STEP, getSequence(), cap, target);
        }
    }

}
