package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderMiningSpeedEffect extends BeyonderEffect {

    public BeyonderMiningSpeedEffect(){
        this(0, 0f, 0, false, BeyonderEffects.EFFECT.WHEEL_MINING);
    }

    public BeyonderMiningSpeedEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Wheel of Fortune Mining";
    }


    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
            cap.getEffectsManager().statsHolder.multMiningSpeed(1.2f + 1.7f*(9-sequenceLevel) + Math.max(5-sequenceLevel, 0)*2.5f);
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {

    }
}
