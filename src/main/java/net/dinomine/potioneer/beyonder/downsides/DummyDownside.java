package net.dinomine.potioneer.beyonder.downsides;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class DummyDownside extends Downside{

    @Override
    protected String getDescId(int sequenceLevel) {
        return "dummy";
    }

    public DummyDownside(int sequenceLevel){
        super(sequenceLevel);
    }

    @Override
    public boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.hurt(target.damageSources().generic(), 2);
        return true;
    }
}
