package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderRegenEffect extends BeyonderEffect {
    public int combo = 0;

    public BeyonderRegenEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        this.sequenceLevel = level;
        this.cost = cost;
        this.maxLife = time;
        this.ID = id;
        this.lifetime = 0;
        this.active = active;
        this.name = "Mystery Regen";
    }


    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
    }

}
