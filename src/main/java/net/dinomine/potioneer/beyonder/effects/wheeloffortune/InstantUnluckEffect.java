package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class InstantUnluckEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().consumeLuck(Mth.clamp(100*(5-getSequenceLevel()), 100, 2000));
        endEffectWhenPossible();
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
