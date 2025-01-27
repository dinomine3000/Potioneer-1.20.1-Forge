package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BeyonderInvisibilityEffect extends BeyonderEffect {
    public int combo = 0;

    public BeyonderInvisibilityEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        this.sequenceLevel = level;
        this.cost = cost;
        this.maxLife = time;
        this.ID = id;
        this.lifetime = 0;
        this.active = active;
        this.name = "Mystery Invis";
    }


    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, this.maxLife, 1, false, false));
    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(!target.hasEffect(MobEffects.INVISIBILITY)){
            target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, (this.maxLife-lifetime), 1, false, false));
        }
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
    }

}
