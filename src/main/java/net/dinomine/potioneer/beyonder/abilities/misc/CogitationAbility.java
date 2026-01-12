package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class CogitationAbility extends PassiveAbility {
    private int pathwayId;

    public CogitationAbility(int pathwayId){
        super(pathwayId, BeyonderEffects.COGITATION, integer -> "cogitation");
        this.pathwayId = pathwayId;
    }

    @Override
    public AbilityInfo getAbilityInfo() {
        return Abilities.getInfo(abilityId, getCooldown(), getMaxCooldown(), isEnabled(), getDescId(sequenceLevel), Math.floorDiv(pathwayId, 10));
    }

    @Override
    public void upgradeToLevel(int pathwaySequenceId, LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(pathwayId == pathwaySequenceId) return;
        onUpgrade(pathwayId, pathwaySequenceId, cap, target);
        pathwayId = pathwaySequenceId;
    }

    @Override
    public int getSequenceLevel() {
        return pathwayId%10;
    }

    //
//    @Override
//    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        setEnabled(cap, target, false);
//    }
//
//    @Override
//    public boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        flipEnable(cap, target);
//        return true;
//    }
//
//    @Override
//    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
////        if(isEnabled(cap.getAbilitiesManager())) cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.MISC_COGITATION, getInfo().id(), info.cost(), -1, true), cap, target);
//    }
//
//    @Override
//    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
//    }
//
//    @Override
//    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MISC_COGITATION)){
//            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MISC_COGITATION, cap, target);
//        }
//    }
}
