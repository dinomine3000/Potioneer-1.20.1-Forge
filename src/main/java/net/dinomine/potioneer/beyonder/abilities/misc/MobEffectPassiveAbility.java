package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class MobEffectPassiveAbility extends Ability {
    private Function<Integer, String> descId;
    private MobEffect effect;
    private int amplifier = 255;
    private int duration = -1;
    private int effectCooldown = -1;
    private int passiveCost = 0;
    private int spiritualityThreshold = 0;
    private boolean enabledOnAcquire = false;

    protected MobEffectPassiveAbility(int sequenceLevel, MobEffect effect, Function<Integer, String> descId){
        super(sequenceLevel);
        this.effect = effect;
        this.descId = descId;
        this.isPassive = true;
        this.isActive = false;
    }

    public static MobEffectPassiveAbility createAbility(int level, MobEffect effect, Function<Integer, String> descId){
        return new MobEffectPassiveAbility(level, effect, descId);
    }

    public MobEffectPassiveAbility withAmplifier(int newAmplifier){
        this.amplifier = newAmplifier;
        return this;
    }

    public MobEffectPassiveAbility withDuration(int durationTicks, int cooldown){
        this.duration = durationTicks;
        this.effectCooldown = Math.max(cooldown, 1);
        return this;
    }

    public MobEffectPassiveAbility withPassiveCost(int passiveCost){
        this.passiveCost = passiveCost;
        return this;
    }

    public MobEffectPassiveAbility withThreshold(int spiritualityThreshold){
        this.spiritualityThreshold = spiritualityThreshold;
        return this;
    }

    public MobEffectPassiveAbility enabledOnAcquire(){
        return enabledOnAcquire(true);
    }

    public MobEffectPassiveAbility enabledOnAcquire(boolean enabled){
        this.enabledOnAcquire = enabled;
        return this;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        if(!isEnabled() && cap.getSpirituality() < spiritualityThreshold) return false;
        boolean newState = flipEnable(cap, target);
        if(newState) {
            return true;
        }
        putOnCooldown(effectCooldown, target);
        return false;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        return false;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        setEnabled(cap, target, enabledOnAcquire);
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(isEnabled()){
            if(cap.getSpirituality() < spiritualityThreshold){
                flipEnable(cap, target);
                return;
            }
            cap.requestPassiveSpiritualityCost(passiveCost);
            if(duration < 0)
                target.addEffect(new MobEffectInstance(effect, -1, amplifier, false, false, false));
        }
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(duration > -1){
            target.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, false));
        }
    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.removeEffect(effect);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return descId.apply(sequenceLevel);
    }
}
