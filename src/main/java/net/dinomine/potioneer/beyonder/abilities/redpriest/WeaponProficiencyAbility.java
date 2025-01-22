package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class WeaponProficiencyAbility extends Ability {

    public WeaponProficiencyAbility(int sequence){
        this.info = new AbilityInfo(64, 0, "Weapon Proficiency", sequence, 0, this.getCooldown());
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(isEnabled(cap.getAbilitiesManager()) && !cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY, getSequence())){
            cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY,
                    getSequence(), 0, -1, true));
        }

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY, getSequence(), cap, target);
        }
    }
}
