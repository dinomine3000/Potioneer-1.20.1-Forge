package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class TempLuckEffect extends BeyonderEffect {
    private static final UUID modifierId = UUID.fromString("6616419c-777b-438e-96e7-ffaf33555acb");
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().changeLuckRange(modifierId, 0, 0, 30 + 20*(9-sequenceLevel));
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().removeModifier(modifierId);
    }
}
