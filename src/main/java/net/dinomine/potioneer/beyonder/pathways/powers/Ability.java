package net.dinomine.potioneer.beyonder.pathways.powers;

import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public abstract class Ability {
    private boolean isActive;
    private boolean isPassive;
    public boolean enabled = true;
    protected int sequence;

    public void disable(EntityBeyonderManager cap, LivingEntity target){
        if(enabled){
            enabled = false;
            deactivate(cap, target);
        }
    }

    public void enable(EntityBeyonderManager cap, LivingEntity target){
        if(!enabled){
            enabled = true;
            activate(cap, target);
        }
    }

    public void flipEnable(EntityBeyonderManager cap, LivingEntity target){
        if (enabled) disable(cap, target);
        else enable(cap, target);
    }



    public abstract void active(EntityBeyonderManager cap, LivingEntity target);
    public abstract void passive(EntityBeyonderManager cap, LivingEntity target);
    public abstract void activate(EntityBeyonderManager cap, LivingEntity target);
    public abstract void deactivate(EntityBeyonderManager cap, LivingEntity target);

}
