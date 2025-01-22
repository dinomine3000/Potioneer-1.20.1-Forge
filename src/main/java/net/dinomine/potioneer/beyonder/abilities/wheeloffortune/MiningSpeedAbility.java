package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class MiningSpeedAbility extends Ability {

    public MiningSpeedAbility(int sequence){
        this.info = new AbilityInfo(32, 0, "Mining Speed", sequence, 0, this.getCooldown());
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getAbilitiesManager().isEnabled(this)
                && !cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_MINING, getSequence())){
            cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_MINING,
                    getSequence(), 0, -1, true));
        }

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_MINING, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.WHEEL_MINING, getSequence(), cap, target);
        }
    }
}
