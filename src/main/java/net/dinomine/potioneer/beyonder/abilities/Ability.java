package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public abstract class Ability {
    private boolean isActive;
    private boolean isPassive;
//    public boolean enabled = true;
//    protected int sequence;
    protected AbilityInfo info = new AbilityInfo(0, 0, "default", 9, 0, 40);

    public AbilityInfo getInfo(){
        return info;
    }

    public int getCooldown(){
        return info.maxCooldown();
    }

    public int getSequence(){
        return info.sequence();
    }

    public boolean isEnabled(PlayerAbilitiesManager mng){
        return mng.isEnabled(this);
    }

    public void disable(EntityBeyonderManager cap, LivingEntity target){
        PlayerAbilitiesManager mng = cap.getAbilitiesManager();
        if(mng.isEnabled(this)){
            mng.setEnabled(this, false);
            deactivate(cap, target);
        }
    }

    public void enable(EntityBeyonderManager cap, LivingEntity target){
        PlayerAbilitiesManager mng = cap.getAbilitiesManager();
        if(!mng.isEnabled(this)){
            mng.setEnabled(this, true);
            activate(cap, target);
        }
    }

    public void flipEnable(EntityBeyonderManager cap, LivingEntity target){
        boolean en = cap.getAbilitiesManager().isEnabled(this);
        if (en) disable(cap, target);
        else enable(cap, target);
        target.sendSystemMessage(Component.literal("Ability " + info.name() + " was turned " + (!en ? "on" : "off") + "."));
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Ability abl)) return false;
        return this.info.name().equals(abl.info.name()) && this.info.sequence() == abl.info.sequence();
    }

    /**
     * function that runs whenever the player casts the ability
     * @param cap
     * @param target
     */
    public abstract boolean active(EntityBeyonderManager cap, LivingEntity target);

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
