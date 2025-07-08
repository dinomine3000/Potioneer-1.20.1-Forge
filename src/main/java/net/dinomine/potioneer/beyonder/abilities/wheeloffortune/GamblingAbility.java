package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderGamblingEffect;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class GamblingAbility extends Ability {

    public GamblingAbility(int sequence){
        this.info = new AbilityInfo(5, 296, "Patience", sequence, 0, getCooldown(), "gambling");
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        System.out.println("Disabling gambling on acquire...");
        disable(cap, target);
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getSpirituality() < info.cost()) disable(cap, target);
        if(cap.getAbilitiesManager().isEnabled(this)
                && !cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_GAMBLING, getSequence())){
            BeyonderGamblingEffect effect = (BeyonderGamblingEffect) BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_GAMBLING,
                    getSequence(), info.cost(), -1, true);
            effect.setLuckQuantity(cap.getLuckManager().getLuck());
            cap.getEffectsManager().addEffect(effect, cap, target);
        }
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_GAMBLING, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.WHEEL_GAMBLING, getSequence(), cap, target);
        }
    }
}
