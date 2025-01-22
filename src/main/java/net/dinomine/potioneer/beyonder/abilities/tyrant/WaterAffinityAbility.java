package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class WaterAffinityAbility extends Ability {

    public WaterAffinityAbility(int sequence){
        this.info = new AbilityInfo(96, 0, "Water Affinity", sequence, 0, this.getCooldown());
    }

    @Override
    public String toString() {
        return "water";
    }

    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(isEnabled(cap.getAbilitiesManager()) && !cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, getSequence())){
            cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY,
                    getSequence(), 0, -1, true));
        }
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, getSequence(), cap, target);
        }
    }
}
