package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderRegenEffect extends BeyonderEffect {
    public int combo = 0;

    public BeyonderRegenEffect(){
        this(0, 0f, 0, false, BeyonderEffects.EFFECT.MYSTERY_REGEN);
    }

    public BeyonderRegenEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
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
