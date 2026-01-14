package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

/**
 * same as passive ability, but used when effects have a limited duration.
 * in this case, when the effect ends, it should disable the ability.
 * the effect duration is passed as argument in the constructor.
 */
public class TimedPassiveAbility extends PassiveAbility{
    private Function<Integer, Integer> durationFunction;
    protected TimedPassiveAbility(int sequenceLevel, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId, Function<Integer, Integer> durationFunction) {
        super(sequenceLevel, effect, descId);
        this.durationFunction = durationFunction;
    }

    public static TimedPassiveAbility createTimed(int level, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId, Function<Integer, Integer> durationFunction){
        return new TimedPassiveAbility(level, effect, descId, durationFunction);
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(isEnabled()){
            cap.getEffectsManager().addOrReplaceEffect(effect.createInstance(sequenceLevel, cost(), durationFunction.apply(getSequenceLevel()), true), cap, target);
            if(cap.getSpirituality() <= cap.getMaxSpirituality()*minimumSpiritualityThreshold) flipEnable(cap, target);
        }
    }
}
