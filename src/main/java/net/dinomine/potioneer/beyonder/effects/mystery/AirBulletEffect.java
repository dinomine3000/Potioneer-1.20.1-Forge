package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class AirBulletEffect extends BeyonderEffect {
    public static final int DAMAGE = 6;
    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().statsHolder.addHealth(-DAMAGE);
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }
}
