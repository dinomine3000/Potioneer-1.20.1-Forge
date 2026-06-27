package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DrowningEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {}

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target, boolean fromLoading) {
        if(fromLoading) return;
        target.level().playSound(null, target.getOnPos(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.NEUTRAL, 1, 1);
        ParticleMaker.createWaterBlockEffectForPlayer(target, target.level(), maxLife);
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.level().playSound(null, target.getOnPos(), SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS, SoundSource.NEUTRAL, 1, 1);
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
