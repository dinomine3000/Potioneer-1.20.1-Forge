package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BeyonderDrowningEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.level().playSound(null, target.getOnPos(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.NEUTRAL, 1, 1);
        if(target instanceof Player player)
            ParticleMaker.createWaterBlockEffectForPlayer(player, target.level());
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
