package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BeyonderZeroDamageEffect extends BeyonderEffect {
    private long timestamp = 0L;
    private static final int COOLDOWN = 20*3;
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    public void playSound(LivingEntity target){
        if(target.level().getGameTime() - timestamp > COOLDOWN){
            target.level().playSound(null, target.getOnPos().above(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.3f, (float) target.getRandom().triangle(1d, 0.2d));
            timestamp = target.level().getGameTime();
        }
    }
}
