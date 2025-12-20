package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class LuckTrendAbility extends Ability {

    public LuckTrendAbility(int sequence){
        this.info = new AbilityInfo(5, 32, "Luck Trend", sequence, 0, this.getMaxCooldown(), "lucky_trend");
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        return false;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_LUCK_TREND,
                getSequence(), 0, -1, true), cap, target);
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_LUCK_TREND, getSequence())){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.WHEEL_LUCK_TREND, getSequence(), cap, target);
        }
    }
}
