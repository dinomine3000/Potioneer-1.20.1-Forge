package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class ReachAbility extends Ability {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public ReachAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "";
    }

//    public ReachAbility(int sequence){
//        this.info = new AbilityInfo(57, 104, "Extended reach", 20 + sequence, 0, getMaxCooldown(), "reach");
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
//        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_REACH, getSequence(), info.cost(), -1, true),
//                cap, target);
//    }
//
//    @Override
//    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
//    }
//
//    @Override
//    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_REACH, getSequence())){
//            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MYSTERY_REACH, getSequence(), cap, target);
//        }
//    }

}
