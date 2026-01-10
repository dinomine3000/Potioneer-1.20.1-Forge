package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class FortuneAbility extends Ability {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public FortuneAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "";
    }
//
//    public FortuneAbility(int sequence){
//        this.info = new AbilityInfo(5, 176, "Fortune Break", sequence, 5, getMaxCooldown(), "fortune");
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
//    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        disable(cap, target);
//    }
//
//    @Override
//    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        if(cap.getSpirituality() < 3*(10-getSequence())) disable(cap, target);
//        if(cap.getAbilitiesManager().isEnabled(this)){
//            cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_FORTUNE,
//                    getSequence(), info.cost(), -1, true), cap, target);
//        }
//    }
//
//    @Override
//    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
//    }
//
//    @Override
//    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_FORTUNE, getSequence())){
//            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.WHEEL_FORTUNE, getSequence(), cap, target);
//        }
//    }
}
