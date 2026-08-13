package net.dinomine.potioneer.beyonder.downsides;

import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class DummyDownside extends Downside{

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "d_dummy";
    }

    public DummyDownside(int sequenceLevel){
        super(sequenceLevel);
        defaultMaxCooldown = 0;
    }

    @Override
    public boolean primary(BeyonderCapability cap, LivingEntity target) {
        target.hurt(target.damageSources().generic(), 2);
        return true;
    }
}
