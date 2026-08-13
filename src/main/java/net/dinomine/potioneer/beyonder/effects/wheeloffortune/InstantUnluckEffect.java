package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class InstantUnluckEffect extends BeyonderEffect {
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().consumeLuck(target, Mth.clamp(100*(5-getSequenceLevel()), 100, 2000), false);
        endEffectWhenPossible();
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }
}
