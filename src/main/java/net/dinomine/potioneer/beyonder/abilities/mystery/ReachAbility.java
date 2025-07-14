package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class ReachAbility extends Ability {

    public ReachAbility(int sequence){
        this.info = new AbilityInfo(57, 104, "Extended reach", 20 + sequence, 0, getCooldown(), "reach");
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        activate(cap, target);
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide() || !isEnabled(cap.getAbilitiesManager())) return;
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_REACH, getSequence(), info.cost(), -1, true),
                cap, target);
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_REACH, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MYSTERY_REACH, getSequence(), cap, target);
        }
    }

}
