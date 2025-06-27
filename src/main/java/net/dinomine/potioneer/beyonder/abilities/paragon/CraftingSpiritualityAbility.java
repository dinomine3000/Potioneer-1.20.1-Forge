package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class CraftingSpiritualityAbility extends Ability {

    public CraftingSpiritualityAbility(int sequence){
        this.info = new AbilityInfo(109, 32, "Crafting Spirituality", 40 + sequence, 0, this.getCooldown(), "craft");
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(!cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_SPIRITUALITY, getSequence())){
            cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.PARAGON_CRAFTING_SPIRITUALITY,
                    getSequence(), 0, -1, true), cap, target);
        }

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_SPIRITUALITY, getSequence())){
//            System.out.println("taketh away");
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_SPIRITUALITY, getSequence(), cap, target);
        }
    }
}
