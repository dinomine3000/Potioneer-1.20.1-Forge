package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class TempBadLuckEffect extends BeyonderEffect {
    private final static UUID modifierId = UUID.fromString("8f5f7ed4-0338-40de-9950-d072a86c1697");
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().changeLuckRange(modifierId, 0, 0, -30 - 20*(9-sequenceLevel));
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().removeModifier(modifierId);
    }
}
