package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class SilkTouchAbility extends Ability {

    public SilkTouchAbility(int sequence){
        this.info = new AbilityInfo(5, 32, "Silk Touch Break", sequence, 0, getCooldown());
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        disable(cap, target);
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getSpirituality() < 3*(10-getSequence())) disable(cap, target);
        if(cap.getAbilitiesManager().isEnabled(this)
                && !cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_SILK_TOUCH, getSequence())){
            cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_SILK_TOUCH,
                    getSequence(), 3*(10-getSequence()), -1, true), cap, target);
        }
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_SILK_TOUCH, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.WHEEL_SILK_TOUCH, getSequence(), cap, target);
        }
    }
}
