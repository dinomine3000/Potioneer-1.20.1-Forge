package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class DurabilityRegenAbility extends PassiveAbility {
    private boolean levelUp;
    private Supplier<Integer> getEffectDuration = () -> levelUp ? -1 : 60*((9-getSequenceLevel())*6 + 3);
    private Supplier<Integer> getEffectCost = () -> levelUp ? cost() / 10 : cost() / 5;
    public DurabilityRegenAbility(int sequence){
//        this.info = new AbilityInfo(109, 56, "Durability Regen", 40 + sequence, 30*(10-sequence), levelUp ? this.getMaxCooldown() : 20*5, "durability_regen_" + (levelUp ? "2" : ""));
        super(BeyonderEffects.PARAGON_REGEN, level -> "durability_regen_" + (level <= 7 ? "2" : ""));
    }

    @Override
    public void init() {
        withCost(30 * (10 - getSequenceLevel()));
        levelUp = getSequenceLevel()%10 <= 7;
    }

    @Override
    public void onUpgrade(int oldLevel, int newLevel, BeyonderCapability cap, LivingEntity target) {
        levelUp = newLevel <= 7;
    }

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        setEnabled(cap, target, levelUp);
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
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
    public void activate(BeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrRefreshEffect(effect.createInstance(getSequenceLevel(), getEffectCost.get(), getEffectDuration.get(), true), cap, target);
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(isEnabled()){
            if(cap.getSpirituality() < cap.getMaxSpirituality()*minimumSpiritualityThreshold
                    || cap.getSpirituality() < minSpiritualityAbsolute) setEnabled(cap, target, false);
        }
    }
}
