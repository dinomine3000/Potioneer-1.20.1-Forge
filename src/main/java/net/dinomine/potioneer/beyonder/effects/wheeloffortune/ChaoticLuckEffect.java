package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class ChaoticLuckEffect extends BeyonderEffect {
    private final static UUID modifierId = UUID.fromString("d450da3e-2a0d-4367-b845-732075920e00");
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().chanceLuckEventChange(modifierId, 3);
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().removeLuckEventModifier(modifierId);
    }
}
