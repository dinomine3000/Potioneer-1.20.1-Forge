package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderFortuneEffect extends BeyonderEffect {

    public BeyonderFortuneEffect(){
        this(0, 0f, 0, false, BeyonderEffects.EFFECT.WHEEL_FORTUNE);
    }

    public BeyonderFortuneEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Wheel of Fortune Fortune";
    }


    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getSpirituality() < cost) {
            maxLife = 1;
            lifetime = 0;
        } else cap.requestPassiveSpiritualityCost(cost);
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
    }
}
