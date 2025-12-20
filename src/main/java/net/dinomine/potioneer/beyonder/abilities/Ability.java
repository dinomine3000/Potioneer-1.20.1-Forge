package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public abstract class Ability {
    private int posY;
    private String ablId;
    private int costSpirituality;
    private int maxCooldown;
//    protected AbilityInfo info = new AbilityInfo(0, 0, "default", 9, 0, 20, "");

//    public AbilityInfo getInfo(){
//        return info;
//    }

    public Ability(int posY){
        this.posY = posY;
    }

    public void setAblId(String newAblId){
        this.ablId = newAblId;
    }

    public int getMaxCooldown(){
        return maxCooldown;
    }

    public boolean isEnabled(PlayerAbilitiesManager mng, String cAblId){
        return mng.isEnabled(cAblId);
    }

    public void disable(LivingEntityBeyonderCapability cap, LivingEntity target, String cAblId){
        PlayerAbilitiesManager mng = cap.getAbilitiesManager();
        if(isEnabled(mng, cAblId)){
            mng.setEnabled(cAblId, false, cap, target);
        }
    }

    public void enable(LivingEntityBeyonderCapability cap, LivingEntity target, String cAblId){
        PlayerAbilitiesManager mng = cap.getAbilitiesManager();
        if(isEnabled(mng, cAblId)){
            mng.setEnabled(cAblId, true, cap, target);
        }
    }

    /**
     *
     * @param cap
     * @param target
     * @return the updated status (enabled or disabled) after calling this method.
     */
    public boolean flipEnable(LivingEntityBeyonderCapability cap, LivingEntity target){
        boolean en = cap.getAbilitiesManager().isEnabled(this);
        if (en) disable(cap, target);
        else enable(cap, target);
        if(!target.level().isClientSide()) target.sendSystemMessage(Component.translatable("potioneer.ability." + (en ? "disabled" : "enabled"), Component.translatable("potioneer.ability_name." + info.descId())));
        return !en;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Ability abl)) return false;
        return this.info.name().equals(abl.info.name()) && this.getSequence() == abl.getSequence();
    }

    /**
     * function that runs when the player acquires the ability
     * @param cap
     * @param target
     */
    public abstract void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target, String cAblId);

    /**
     * function that runs whenever the player casts the ability
     * @param cap
     * @param target
     */
    public abstract boolean active(LivingEntityBeyonderCapability cap, LivingEntity target, String cAblId);

    /**
     * function that runs every tick
     * @param cap
     * @param target
     */
    public abstract void passive(LivingEntityBeyonderCapability cap, LivingEntity target, String cAblId);

    /**
     * function that implements behaviour for every time the ability is activated (like changing stuff for a setup)
     * @param cap
     * @param target
     */
    public abstract void activate(LivingEntityBeyonderCapability cap, LivingEntity target, String cAblId);

    /**
     * function that implements behaviour for every time the ability is deactivated (like removing effects)
     * @param cap
     * @param target
     */
    public abstract void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target, String cAblId);

    public abstract AbilityInfo getAbilityinfo(int sequenceLevel);
}
