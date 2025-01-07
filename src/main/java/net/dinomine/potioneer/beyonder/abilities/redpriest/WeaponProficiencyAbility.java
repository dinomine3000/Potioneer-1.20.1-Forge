package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class WeaponProficiencyAbility extends Ability {

    public WeaponProficiencyAbility(int sequence, boolean enabled){
        this.sequence = sequence;
        this.enabled = enabled;
    }

    @Override
    public void active(EntityBeyonderManager cap, LivingEntity target) {
        flipEnable(cap, target);
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(enabled && !cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY, this.sequence)){
            cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY,
                    this.sequence, 0, -1, true));
        }

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(enabled && cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY, this.sequence)){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY, this.sequence, cap, target);
        }
    }
}
