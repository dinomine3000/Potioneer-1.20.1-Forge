package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class FireBuffAbility extends Ability {

    public FireBuffAbility(int sequence){
        this.info = new AbilityInfo(83, 80, "Fire Dance", 30 + sequence, 5, this.getCooldown(), "fire_buff");
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        activate(cap, target);
    }

    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(isEnabled(cap.getAbilitiesManager())){
            cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.RED_FIRE_BUFF,
                    getSequence(), info.cost(), -1, true), cap, target);
            if(cap.getSpirituality() < info.cost()) flipEnable(cap, target);
        }
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.RED_FIRE_BUFF, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.RED_FIRE_BUFF, getSequence(), cap, target);
        }
    }
}
