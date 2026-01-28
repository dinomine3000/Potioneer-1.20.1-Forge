package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
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
    private boolean canFlip = false;
    private boolean enabledOnAcquire = false;
    protected final BeyonderEffects.BeyonderEffectType effect;
    /**
     * percentage of maximum spirituality below which the ability stops working
     */
    protected float minimumSpiritualityThreshold = 0f;
    protected int minSpiritualityAbsolute = 0;
    private final Function<Integer, String> descId;
    private int duration = -1;

    protected PassiveAbility(int sequenceLevel, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId){
        super(sequenceLevel);
        this.effect = effect;
        this.descId = descId;
        this.isPassive = true;
        this.isActive = false;
    }

    public PassiveAbility withDuration(int duration){
        this.duration = duration;
        return this;
    }

    public static PassiveAbility createAbility(int level, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId){
        return new PassiveAbility(level, effect, descId);
    }

    /**
     * Wheter you can flip the state of this ability.
     * Of note, if the ability is ever disabled, you can always enable it. This option prevents players from disabling the ability.
     * @return
     */
    public PassiveAbility canFlip(){
        this.canFlip = true;
        return this;
    }

    public PassiveAbility enabledOnAcquire(){
        this.enabledOnAcquire = true;
        return this;
    }

    /**
     * A spirituality threshold as a float. If spirituality ever dips below this percentage, it will disable itself
     * @param thresh
     * @return
     */
    public PassiveAbility withThreshold(float thresh){
        this.minimumSpiritualityThreshold = thresh;
        return this;
    }

    public PassiveAbility withThreshold(int thresh){
        this.minSpiritualityAbsolute = thresh;
        return this;
    }

    @Override
    public Ability withActives(boolean isActive, boolean isPassive) {
        this.isActive = isActive;
        this.isPassive = true;
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
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        if(!isEnabled() || canFlip) flipEnable(cap, target);
        return true;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        return false;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(isEnabled() && !cap.getEffectsManager().hasEffectOrBetter(effect.createInstance(sequenceLevel, duration, true))){
            cap.getEffectsManager().addOrReplaceEffect(createEffectInstance(cap, target), cap, target);
            if(cap.getSpirituality() < cap.getMaxSpirituality()*minimumSpiritualityThreshold
                || cap.getSpirituality() < minSpiritualityAbsolute) setEnabled(cap, target, false);
        }
    }

    protected BeyonderEffect createEffectInstance(LivingEntityBeyonderCapability cap, LivingEntity target){
        return effect.createInstance(sequenceLevel, cost(), -1, true);
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().removeEffect(effect.getEffectId(), sequenceLevel);
    }
}
