package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

/**
 * Base or Standard ability class that implements the common ability type of doing a passive effect.
 * It always tries to give the associated effect for an unlimited duration, with the corresponding sequence level.
 * by default, cost is 0. you can use withCost() to set the cost, and itll be passed down to the effect instance. do with that what you will
 */
public class PassiveAbility extends Ability {
    private boolean canFlip = true;
    private boolean enabledOnAcquire = true;
    protected final BeyonderEffects.BeyonderEffectType effect;
    /**
     * percentage of maximum spirituality below which the ability stops working
     */
    protected float minimumSpiritualityThreshold = 0f;
    private Function<Integer, String> descId;

    protected PassiveAbility(int sequenceLevel, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId){
        super(sequenceLevel);
        this.effect = effect;
        this.descId = descId;

    }

    public static PassiveAbility createAbility(int level, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId){
        return new PassiveAbility(level, effect, descId);
    }

    public PassiveAbility withCost(Function<Integer, Integer> costFunction){
        setCost(costFunction);
        return this;
    }

    public PassiveAbility canFlip(){
        this.canFlip = true;
        return this;
    }

    public PassiveAbility enabledOnAcquire(){
        this.enabledOnAcquire = true;
        return this;
    }

    public PassiveAbility withThreshold(float thresh){
        this.minimumSpiritualityThreshold = thresh;
        return this;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return descId.apply(sequenceLevel);
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        setEnabled(cap, target, enabledOnAcquire);
    }

    @Override
    public boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        if(!isEnabled() || canFlip) flipEnable(cap, target);
        putOnCooldown(target);
        return true;
    }

    @Override
    public boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        return false;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(isEnabled()){
            cap.getEffectsManager().addOrReplaceEffect(effect.createInstance(sequenceLevel, cost(), -1, true), cap, target);
            if(cap.getSpirituality() <= cap.getMaxSpirituality()*minimumSpiritualityThreshold) flipEnable(cap, target);
        }
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().removeEffect(effect.getEffectId(), sequenceLevel);
    }
}
