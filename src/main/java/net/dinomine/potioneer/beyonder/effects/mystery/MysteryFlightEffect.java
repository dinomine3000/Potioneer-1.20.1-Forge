package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class MysteryFlightEffect extends BeyonderEffect {
    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        cap.requestPassiveSpiritualityCost(cost);
        cap.getEffectsManager().statsHolder.enableFlight();
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }
}
