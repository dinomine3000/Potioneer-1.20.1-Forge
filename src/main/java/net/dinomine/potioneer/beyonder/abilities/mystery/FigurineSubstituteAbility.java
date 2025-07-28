package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderFigurineEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class FigurineSubstituteAbility extends Ability {

    public FigurineSubstituteAbility(int sequence){
        this.info = new AbilityInfo(57, 224, "Figure Substitute", 20 + sequence, 30 + 10*(9-sequence), 1, "figure");
        this.isActive = true;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        return false;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrReplaceEffect(new BeyonderFigurineEffect(getSequence(), info.cost(), -1, true, BeyonderEffects.EFFECT.MYSTERY_FIGURINE),
                cap, target);
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_FIGURINE))
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MYSTERY_FIGURINE, getSequence(), cap, target);
    }
}
