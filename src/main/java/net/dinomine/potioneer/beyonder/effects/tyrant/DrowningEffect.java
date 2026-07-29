package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DrowningEffect extends BeyonderEffect {

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target, boolean fromLoading) {
        if(fromLoading) return;
        target.level().playSound(null, target.getOnPos(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.NEUTRAL, 1, 1);
        ParticleMaker.createWaterBlockEffectForPlayer(target, target.level(), maxLife);
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        target.level().playSound(null, target.getOnPos(), SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS, SoundSource.NEUTRAL, 1, 1);
        if(!target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) || target.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() < 2){
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Math.max(maxLife - lifetime, 1), 2, true, false));
        }
        target.clearFire();
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) target.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }
}
