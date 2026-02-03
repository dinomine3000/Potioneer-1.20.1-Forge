package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class BeyonderGamblingEffect extends BeyonderEffect {
    private static final List<MobEffect> POSITIVE_EFFECTS = List.of(MobEffects.DAMAGE_BOOST, MobEffects.DAMAGE_RESISTANCE, MobEffects.ABSORPTION, MobEffects.REGENERATION, MobEffects.CONDUIT_POWER);
    private static final List<MobEffect> NEGATIVE_EFFECTS = List.of(MobEffects.DIG_SLOWDOWN, MobEffects.WEAKNESS, MobEffects.POISON, MobEffects.DARKNESS);

    private boolean isQuick = false;

    public void setQuick(boolean quick){
        this.isQuick = quick;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        int minDuration = 60*20;
        int maxDuration = 4*60*20;
        int maxLevel = getSequenceLevel() < 6 ? 3 : 1;
        PlayerLuckManager luck = cap.getLuckManager();
        RandomSource random = target.getRandom();
        if(isQuick){
            minDuration = 5*20;
            maxDuration = 20*20;
            maxLevel = getSequenceLevel() < 6 ? 5 : 4;
        }
        if(luck.passesLuckCheck(0.5f, 50, 50, random)){
            applyPositiveEffect(cap, target, luck, sequenceLevel, minDuration, maxDuration, maxLevel, random);
            cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.GAMBLER_ACTING_SUC, 7);
            endEffectWhenPossible();
            return;
        }
        cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.GAMBLER_ACTING_FAIL, 7);
        applyNegativeEffect(cap, target, luck, sequenceLevel, minDuration, maxDuration, maxLevel, random);
        endEffectWhenPossible();
    }

    public static void applyPositiveEffect(LivingEntityBeyonderCapability cap, LivingEntity target, PlayerLuckManager luck, int sequenceLevel, int minDuration, int maxDuration, int maxLevel, RandomSource random){
        if(maxLevel > 1){
            for(MobEffectInstance effectInstance: target.getActiveEffects()){
                if(effectInstance.getEffect().isBeneficial()) continue;
                target.removeEffect(effectInstance.getEffect());
            }
        }
        for(MobEffect effect: POSITIVE_EFFECTS){
            target.addEffect(new MobEffectInstance(effect, luck.getRandomNumber(minDuration, maxDuration, true, random),
                    luck.getRandomNumber(0, maxLevel, true, random), false, true, true));
        }
        cap.getEffectsManager().addEffectNoRefresh(
                BeyonderEffects.WHEEL_LUCK_EFFECT.createInstance(sequenceLevel, 0, luck.getRandomNumber(minDuration, maxDuration, true, random),
                        true), cap, target);
        if(sequenceLevel < 6 && maxLevel > 1){
            if(maxLevel > 2)
                cap.setSpirituality(-1);
            cap.setUnlimitedSpirituality(luck.getRandomNumber(minDuration, maxDuration, true, random));
        }
        if(sequenceLevel < 6 && maxLevel > 1)
            ParticleMaker.createSlotMachineForEntity(target.level(), target, true);
    }

    public static void applyNegativeEffect(LivingEntityBeyonderCapability cap, LivingEntity target, PlayerLuckManager luck, int sequenceLevel, int minDuration, int maxDuration, int maxLevel, RandomSource random) {
        for(MobEffect effect: NEGATIVE_EFFECTS){
            target.addEffect(new MobEffectInstance(effect, luck.getRandomNumber(minDuration, maxDuration/2, false, random),
                    luck.getRandomNumber(0, maxLevel, false, random), false, true, true));
        }
        if(sequenceLevel < 6){
            cap.setSpirituality(luck.getRandomNumber(0, 20, true, random));
        }
        ParticleMaker.createSlotMachineForEntity(target.level(), target, false);
    }

        @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        endEffectWhenPossible();
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
