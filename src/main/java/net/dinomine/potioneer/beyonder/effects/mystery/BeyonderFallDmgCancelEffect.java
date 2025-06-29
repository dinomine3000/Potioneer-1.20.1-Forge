package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AnvilMenu;

public class BeyonderFallDmgCancelEffect extends BeyonderEffect {
    public boolean flag = false;

    public BeyonderFallDmgCancelEffect(){
        this(0, 0f, 0, false, BeyonderEffects.EFFECT.MYSTERY_FALL_NEGATE);
    }

    public BeyonderFallDmgCancelEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
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
