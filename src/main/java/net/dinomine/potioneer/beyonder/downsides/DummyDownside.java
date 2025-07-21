package net.dinomine.potioneer.beyonder.downsides;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class DummyDownside extends Downside{

    public DummyDownside(int sequenceLevel){
        super("Dummy", 0, true, "dummy", sequenceLevel);
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.hurt(target.damageSources().generic(), 2);
        return true;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
