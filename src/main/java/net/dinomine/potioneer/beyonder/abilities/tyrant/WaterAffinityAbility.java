package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class WaterAffinityAbility extends Ability {

    public WaterAffinityAbility(int sequence, boolean enabled){
        this.sequence = sequence;
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "water";
    }

    public void active(EntityBeyonderManager cap, LivingEntity target) {
        flipEnable(cap, target);
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(enabled && !cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, this.sequence)){
            cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY,
                    this.sequence, 0, -1, true));
        }
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, this.sequence)){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, this.sequence, cap, target);
        }
    }
}
