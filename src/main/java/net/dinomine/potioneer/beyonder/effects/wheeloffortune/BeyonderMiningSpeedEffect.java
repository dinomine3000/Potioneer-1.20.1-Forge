package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderMiningSpeedEffect extends BeyonderEffect {


    public BeyonderMiningSpeedEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        this.sequenceLevel = level;
        this.cost = cost;
        this.maxLife = time;
        this.ID = id;
        this.lifetime = 0;
        this.active = active;
    }


    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        cap.getEffectsManager().statsHolder.multMiningSpeed(10f + (9-sequenceLevel));
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {

    }
}
