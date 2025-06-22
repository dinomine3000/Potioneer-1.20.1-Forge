package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AnvilMenu;

public class BeyonderFallDmgCancelEffect extends BeyonderEffect {
    public boolean flag = false;

    public BeyonderFallDmgCancelEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        this.sequenceLevel = level;
        this.cost = cost;
        this.maxLife = time;
        this.ID = id;
        this.lifetime = 0;
        this.active = active;
        this.name = "Fall Damage Cancel";
    }


    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(!target.onGround() && !flag){
            maxLife = 5;
            lifetime = 0;
            flag = true;
        }
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
    }

}
