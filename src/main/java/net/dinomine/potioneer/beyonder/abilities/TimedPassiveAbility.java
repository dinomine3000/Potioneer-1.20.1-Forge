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
    private int passiveCost = 0;
    private int castCost = 0;
    protected TimedPassiveAbility(BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId, Function<Integer, Integer> durationFunction) {
        super(effect, descId);
        this.durationFunction = durationFunction;
    }

    public static TimedPassiveAbility createTimed(BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId, Function<Integer, Integer> durationFunction){
        return new TimedPassiveAbility(effect, descId, durationFunction);
    }


    @Override
    protected BeyonderEffect createEffectInstance(BeyonderCapability cap, LivingEntity target) {
        return effect.createInstance(getSequenceLevel(), passiveCost, durationFunction.apply(getSequenceLevel()), true);
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(!hasEnoughSpirituality(cap)) return false;
        super.passive(cap, target);
        setNextCooldownAs(cooldownTicks);
        cap.requestActiveSpiritualityCost(castCost);
        return true;
    }

    public TimedPassiveAbility withCost(int castCost, int passiveCost){
        this.castCost = castCost;
        this.passiveCost = passiveCost;
        return this;
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        setEnabled(cap, target, true);
    }

    @Override
    public void deactivate(BeyonderCapability cap, LivingEntity target) {}
}
