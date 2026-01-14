package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;

public class StepUpAbility extends Ability {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public StepUpAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "";
    }

//    public StepUpAbility(int sequence){
//        this.info = new AbilityInfo(57, 152, "Step Assist", 20 + sequence, 0, this.getMaxCooldown(), "step_up");
//    }
//
//    @Override
//    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        activate(cap, target);
//    }
//
//    @Override
//    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        if(target.level().isClientSide()) return false;
//        flipEnable(cap, target);
//        return true;
//    }
//
//    @Override
//    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        if(target.level().isClientSide() || !isEnabled(cap.getAbilitiesManager())) return;
//        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_STEP, getSequence(), info.cost(), -1, true),
//                cap, target);
//    }
//
//    @Override
//    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
//
//    }
//
//    @Override
//    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_STEP, getSequence())){
//            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MYSTERY_STEP, getSequence(), cap, target);
//        }
//    }

}
