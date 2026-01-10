package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class InvisibilityAbility extends Ability {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public InvisibilityAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "";
    }
//
//    public InvisibilityAbility(int sequence){
//        this.info = new AbilityInfo(57, 128, "Invisibility", 20 + sequence, 40 + 20*(9-sequence), 5*10 + 2*40*((9-sequence)*10 + 5), "invisibility");
//        this.isActive = true;
//    }
//
//    @Override
//    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
//    }
//
//    @Override
//    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        if(target.level().isClientSide() && cap.getSpirituality() >= info.cost()) return true;
//
//        if(cap.getSpirituality() >= info.cost()){
//            if(cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_INVIS,
//                    getSequence(), 0, 2*40*((9-getSequence())*10 + 5), true), cap, target)){
//                cap.requestActiveSpiritualityCost(info.cost());
//                return true;
//            }
//            target.sendSystemMessage(Component.literal("Could not give effect: one already exists"));
//        }
//        return false;
//    }
//
//    @Override
//    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
//    }
//
//    @Override
//    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
//
//    }
//
//    @Override
//    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_INVIS, getSequence())){
//            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MYSTERY_INVIS, getSequence(), cap, target);
//        }
//    }
}
