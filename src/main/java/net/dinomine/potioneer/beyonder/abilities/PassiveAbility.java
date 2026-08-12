package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

/**
 * Base or Standard ability class that implements the common ability type of doing a passive effect.
 * It always tries to give the associated effect for an unlimited duration, with the corresponding sequence level.
 * by default, cost is 0. you can use withCost() to set the cost, and itll be passed down to the effect instance. do with that what you will
 */
public class PassiveAbility extends Ability {
    public enum CooldownTrigger {
        ON_APPLY,
        ON_REMOVE,
        BOTH
    }

    private boolean canFlip = false;
    private boolean enabledOnAcquire = false;
    protected final BeyonderEffects.BeyonderEffectType effect;
    /**
     * percentage of maximum spirituality below which the ability stops working
     */
    protected float minimumSpiritualityThreshold = 0f;
    protected int minSpiritualityAbsolute = 0;
    private final Function<Integer, String> descId;
    private final Function<Integer, LinkedHashSet<String>> otherDescIds;
    private int duration = -1;

    private int cooldownTicks = 0;
    private CooldownTrigger cooldownTrigger = CooldownTrigger.ON_REMOVE;

    protected PassiveAbility(int sequenceLevel, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId){
        this(sequenceLevel, effect, descId, null);
    }
    protected PassiveAbility(int sequenceLevel, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId, Function<Integer, LinkedHashSet<String>> otherDescs){
        super(sequenceLevel);
        this.effect = effect;
        this.descId = descId;
        this.isPassive = true;
        this.isActive = false;
        this.otherDescIds = otherDescs;
    }

    public PassiveAbility withDuration(int duration){
        this.duration = duration;
        return this;
    }

    public PassiveAbility withCooldown(int ticks) {
        return withCooldown(ticks, CooldownTrigger.ON_REMOVE);
    }

    public PassiveAbility withCooldown(int ticks, CooldownTrigger trigger) {
        this.cooldownTicks = ticks;
        this.cooldownTrigger = trigger;
        return this;
    }

    public static PassiveAbility createAbility(int level, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId){
        return new PassiveAbility(level, effect, descId);
    }

    public static PassiveAbility createAbility(int level, BeyonderEffects.BeyonderEffectType effect, Function<Integer, String> descId, Function<Integer, LinkedHashSet<String>> otherIds){
        return new PassiveAbility(level, effect, descId, otherIds);
    }

    /**
     * Wheter you can flip the state of this ability.
     * Of note, if the ability is ever disabled, you can always enable it. This option prevents players from disabling the ability.
     * @return
     */
    public PassiveAbility canFlip(){
        return canFlip(true);
    }

    public PassiveAbility canFlip(boolean canFlip){
        this.canFlip = canFlip;
        return this;
    }

    @Override
    public void onUpgrade(int oldLevel, int newLevel, BeyonderCapability cap, LivingEntity target) {
        if(oldLevel < newLevel)
            deactivate(cap, target);
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
    protected String getMainDescId(int sequenceLevel) {
        return descId.apply(sequenceLevel);
    }

    @Override
    protected LinkedHashSet<String> getAllDescId(int sequenceLevel) {
        return otherDescIds == null ? super.getAllDescId(sequenceLevel) : otherDescIds.apply(sequenceLevel);
    }

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        setEnabled(cap, target, enabledOnAcquire);
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        if(!isEnabled() || canFlip) flipEnable(cap, target);
        return true;
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        return false;
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(isEnabled() && !cap.getEffectsManager().hasEffectOrBetter(effect.createInstance(sequenceLevel, duration, true))){
            if (cooldownTicks > 0 && (cooldownTrigger == CooldownTrigger.ON_APPLY || cooldownTrigger == CooldownTrigger.BOTH)) {
                setNextCooldownAs(cooldownTicks);
            }
            cap.getEffectsManager().addOrRefreshEffect(createEffectInstance(cap, target), cap, target);
        }

        if(cap.getSpirituality() < cap.getMaxSpirituality()*minimumSpiritualityThreshold
                || cap.getSpirituality() < minSpiritualityAbsolute) {
            if (isEnabled()) {
                deactivate(cap, target);
                setEnabled(cap, target, false);
            }
        }
    }

    protected BeyonderEffect createEffectInstance(BeyonderCapability cap, LivingEntity target){
        return effect.createInstance(sequenceLevel, cost(), -1, true);
    }

    @Override
    public void activate(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public final void deactivate(BeyonderCapability cap, LivingEntity target) {
        if (cap.getEffectsManager().hasEffect(effect.getEffectId(), sequenceLevel)) {
            cap.getEffectsManager().removeEffect(effect.getEffectId(), sequenceLevel);
            if (cooldownTicks > 0 && (cooldownTrigger == CooldownTrigger.ON_REMOVE || cooldownTrigger == CooldownTrigger.BOTH)) {
                setNextCooldownAs(cooldownTicks);
            }
        }
    }
}