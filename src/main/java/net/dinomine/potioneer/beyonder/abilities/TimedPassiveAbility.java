package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

/**
 * same as passive ability, but used when effects have a limited duration.
 * in this case, when the effect ends, it should disable the ability.
 * the effect duration is passed as argument in the constructor.
 */
public class TimedPassiveAbility extends PassiveAbility {
    private final Function<Integer, Integer> durationFunction;
    protected TimedPassiveAbility(int sequenceLevel, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId, Function<Integer, Integer> durationFunction) {
        super(sequenceLevel, effect, descId);
        this.durationFunction = durationFunction;
    }

    public static TimedPassiveAbility createTimed(int level, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId, Function<Integer, Integer> durationFunction){
        return new TimedPassiveAbility(level, effect, descId, durationFunction);
    }


    @Override
    protected BeyonderEffect createEffectInstance(BeyonderCapability cap, LivingEntity target) {
        return effect.createInstance(sequenceLevel, minSpiritualityAbsolute, durationFunction.apply(sequenceLevel), true);
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(!hasEnoughSpirituality(cap)) return false;
        super.passive(cap, target);
        setNextCooldownAs(cooldownTicks);
        cap.requestActiveSpiritualityCost(cost());
        return true;
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        setEnabled(cap, target, true);
    }

    @Override
    public void deactivate(BeyonderCapability cap, LivingEntity target) {}
}
