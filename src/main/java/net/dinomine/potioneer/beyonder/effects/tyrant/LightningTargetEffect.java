package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;

public class LightningTargetEffect extends BeyonderEffect {
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(!cap.getLuckManager().passesLuckCheck(0.9f + sequenceLevel/100f, 1, 0, target.getRandom())){
            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, target.level());
            lightning.setDamage(Math.max((10-sequenceLevel)*5 - 20, 5));
            lightning.setPos(target.getOnPos().offset(target.getRandom().nextInt(3), 0, target.getRandom().nextInt(3)).getCenter());
            target.level().addFreshEntity(lightning);
        }
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
    }
}
