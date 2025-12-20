package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class AbstractPassiveAbility extends Ability{
    private boolean canFlip = true;
    private boolean enabledOnAcquire = true;

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target, String cAblId) {
        if(!enabledOnAcquire){
            disable(cap, target, cAblId);
            Pathways.WHEEL_OF_FORTUNE.get()
        }
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target, String cAblId) {
        if(isEnabled(cap.getAbilitiesManager(),)){
            cap.getEffectsManager().addOrReplaceEffect(effect);
            if(cap.getSpirituality() <= cap.getMaxSpirituality()*0.15f) flipEnable(cap, target);
        } else {
            deactivate(cap, target);
        }
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target, String cAblId) {

    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target, String cAblId) {

    }

    @Override
    public AbilityInfo getAbilityinfo(int sequenceLevel) {
        return null;
    }
}
