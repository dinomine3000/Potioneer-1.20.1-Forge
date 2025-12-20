package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class CraftingSpiritualityAbility extends Ability {

    public CraftingSpiritualityAbility(int sequence){
        this.info = new AbilityInfo(109, 32, "Crafting Spirituality", 40 + sequence, 0, this.getMaxCooldown(), "craft");
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.PARAGON_CRAFTING_SPIRITUALITY,
                getSequence(), 0, -1, true), cap, target);

    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_SPIRITUALITY, getSequence())){
//            System.out.println("taketh away");
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_SPIRITUALITY, getSequence(), cap, target);
        }
    }
}
