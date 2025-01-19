package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public abstract class Ability {
    private boolean isActive;
    private boolean isPassive;
    public boolean enabled = true;
    protected int sequence;
    protected AbilityInfo info = new AbilityInfo(0, 0, "default");

    public AbilityInfo getInfo(){
        return info;
    }

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
        target.sendSystemMessage(Component.literal("Ability " + info.name() + " was turned " + (this.enabled ? "on" : "off") + "."));
    }

    /**
     * function that runs whenever the player casts the ability
     * @param cap
     * @param target
     */
    public abstract void active(EntityBeyonderManager cap, LivingEntity target);

    /**
     * function that runs every tick
     * @param cap
     * @param target
     */
    public abstract void passive(EntityBeyonderManager cap, LivingEntity target);

    /**
     * function that implements behaviour for every time the ability is activated (like changing stuff for a setup)
     * @param cap
     * @param target
     */
    public abstract void activate(EntityBeyonderManager cap, LivingEntity target);

    /**
     * function that implements behaviour for every time the ability is deactivated (like removing effects)
     * @param cap
     * @param target
     */
    public abstract void deactivate(EntityBeyonderManager cap, LivingEntity target);

}
