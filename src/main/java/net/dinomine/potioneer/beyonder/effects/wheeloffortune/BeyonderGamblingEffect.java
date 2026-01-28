package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class BeyonderGamblingEffect extends BeyonderEffect {
    private static final List<MobEffect> POSITIVE_EFFECTS = List.of(MobEffects.DAMAGE_BOOST, MobEffects.DAMAGE_RESISTANCE, MobEffects.ABSORPTION, MobEffects.REGENERATION, MobEffects.CONDUIT_POWER);
    private static final List<MobEffect> NEGATIVE_EFFECTS = List.of(MobEffects.DIG_SLOWDOWN, MobEffects.WEAKNESS, MobEffects.POISON, MobEffects.DARKNESS);
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        int minDuration = 60*20;
        int maxDuration = 4*60*20;
        PlayerLuckManager luck = cap.getLuckManager();
        RandomSource random = target.getRandom();
        if(luck.passesLuckCheck(0.5f, 50, 50, random)){
            for(MobEffect effect: POSITIVE_EFFECTS){
                target.addEffect(new MobEffectInstance(effect, luck.getRandomNumber(minDuration, maxDuration, true, random),
                                            luck.getRandomNumber(0, 5, true, random), false, true, true));
            }
            cap.getEffectsManager().addEffectNoRefresh(
                    BeyonderEffects.WHEEL_LUCK_DODGE.createInstance(sequenceLevel, 0, luck.getRandomNumber(minDuration, maxDuration, true, random),
                            true), cap, target);
            return;
        }
        for(MobEffect effect: NEGATIVE_EFFECTS){
            target.addEffect(new MobEffectInstance(effect, luck.getRandomNumber(minDuration, maxDuration/2, false, random),
                    luck.getRandomNumber(0, 3, false, random), false, true, true));
        }
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        endEffectWhenPossible();
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
