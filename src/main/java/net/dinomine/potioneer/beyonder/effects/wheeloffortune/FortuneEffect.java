package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class FortuneEffect extends BeyonderEffect {
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost) {
            endEffectWhenPossible();
        } else cap.requestPassiveSpiritualityCost(cost);
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
    }
}
