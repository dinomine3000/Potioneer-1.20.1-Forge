package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class DurabilityRegenAbility extends PassiveAbility {
    private boolean levelUp;
    private Supplier<Integer> getEffectDuration = () -> levelUp ? -1 : 60*((9-getSequenceLevel())*6 + 3);
    private Supplier<Integer> getEffectCost = () -> levelUp ? cost() / 10 : cost() / 5;
    public DurabilityRegenAbility(int sequence){
//        this.info = new AbilityInfo(109, 56, "Durability Regen", 40 + sequence, 30*(10-sequence), levelUp ? this.getMaxCooldown() : 20*5, "durability_regen_" + (levelUp ? "2" : ""));
        super(sequence, BeyonderEffects.PARAGON_REGEN, level -> "durability_regen_" + (level <= 7 ? "2" : ""));
        setCost(level -> 30 * (10 - level));
        levelUp = sequence%10 <= 7;
    }

    @Override
    public void upgradeToLevel(int level, LivingEntityBeyonderCapability cap, LivingEntity target) {
        levelUp = level%10 <= 7;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        setEnabled(cap, target, levelUp);
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);

        if (!isEnabled() && !levelUp){
            putOnCooldown(20*5, target);
            //return false to have custom cooldown
            return false;
        }
        return true;
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrReplaceEffect(effect.createInstance(sequenceLevel, getEffectCost.get(), getEffectDuration.get(), true), cap, target);
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(isEnabled()){
            if(cap.getSpirituality() < cap.getMaxSpirituality()*minimumSpiritualityThreshold
                    || cap.getSpirituality() < minSpiritualityAbsolute) setEnabled(cap, target, false);
        }
    }
}
