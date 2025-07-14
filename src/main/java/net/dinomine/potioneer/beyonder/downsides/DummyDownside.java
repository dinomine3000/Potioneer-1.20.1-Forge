package net.dinomine.potioneer.beyonder.downsides;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class DummyDownside extends Downside{

    public DummyDownside(int sequenceLevel){
        super("Dummy", 0, true, "dummy", sequenceLevel);
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        target.hurt(target.damageSources().generic(), 2);
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {

    }
}
