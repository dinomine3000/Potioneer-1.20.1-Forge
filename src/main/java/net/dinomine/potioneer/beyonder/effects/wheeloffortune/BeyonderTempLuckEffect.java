package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class BeyonderTempLuckEffect extends BeyonderEffect {
    private static final UUID modifierId = UUID.fromString("6616419c-777b-438e-96e7-ffaf33555acb");
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().changeLuckRange(modifierId, -15, -15, 30 + 10*(9-sequenceLevel));
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().removeModifier(modifierId);
    }
}
