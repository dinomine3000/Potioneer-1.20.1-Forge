package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class CraftingBonusAbility extends Ability {

    public CraftingBonusAbility(int sequence){
        this.info = new AbilityInfo(80, 0, "Crafting Bonus", sequence, 0, this.getCooldown());
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(isEnabled(cap.getAbilitiesManager()) && !cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_BONUS, getSequence())){
            cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.PARAGON_CRAFTING_BONUS,
                    getSequence(), 0, -1, true));
        }

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_BONUS, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_BONUS, getSequence(), cap, target);
        }
    }
}
