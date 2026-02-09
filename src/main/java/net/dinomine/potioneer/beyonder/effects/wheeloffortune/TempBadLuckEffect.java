package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class TempBadLuckEffect extends BeyonderEffect {
    private final static UUID modifierId = UUID.fromString("8f5f7ed4-0338-40de-9950-d072a86c1697");
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().changeLuckRange(modifierId, 0, 0, -30 - 20*(9-sequenceLevel));
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().removeModifier(modifierId);
    }
}
