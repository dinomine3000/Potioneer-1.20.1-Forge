package net.dinomine.potioneer.beyonder.effects;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public abstract class BeyonderEffect {
    protected int sequenceLevel;
    protected int lifetime = 0;
    protected int maxLife;
    public String name;
    protected String effectId;
    protected int cost = 0;

    protected boolean active;

    public BeyonderEffect() {
    }

    public BeyonderEffect withParams(int sequence, int time, boolean active) {
        this.sequenceLevel = sequence;
        this.lifetime = time == -1 ? -2 : 0;
        this.maxLife = time;
        this.active = active;
        return this;
    }

    public BeyonderEffect withParams(int sequence, int time, boolean active, int cost) {
        this.sequenceLevel = sequence%10;
        this.lifetime = time == -1 ? -2 : 0;
        this.maxLife = time;
        this.active = active;
        this.cost = cost;
        return this;
    }

    public int getSequenceLevel() {
        return sequenceLevel;
    }

    public String getId(){
        return effectId;
    }
    public boolean is(String id){
        return effectId.equals(id);
    }

    public boolean is(BeyonderEffect effect){
        return effectId.equals(effect.getId());
    }

    public boolean is(String id, int seq){
        return is(id) && this.sequenceLevel == seq;
    }

    public boolean isOrBetter(String id, int seq){
        return is(id) && this.sequenceLevel <= seq;
    }
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BeyonderEffect effect && effect.is(this) && this.sequenceLevel == effect.sequenceLevel;
    }

    public boolean endsWithin(int time){
        if(maxLife < 1) return false;
        return this.maxLife - this.lifetime < time;
    }

    public void refreshTime(){
        this.lifetime = 0;
    }

    public void refreshTime(BeyonderEffect effect){
        this.lifetime = 0;
        this.maxLife = effect.maxLife;
    }

    public void setLifetime(int life){
        this.lifetime = life;
    }

    public void setDuration(int ticks){
        this.maxLife = ticks;
    }

    public void endEffectWhenPossible(){
        maxLife = 1;
        lifetime = 2;
    }


    /**
     * used for replacement purposes. will return true if theyre the same effect but the argument is of a higher sequence
     * aka, will return true if the argument should replace this
     * @param effect
     * @return
     */
    public boolean isBetter(BeyonderEffect effect){
        return this.is(effect) && this.sequenceLevel >= effect.sequenceLevel;
    }

    public void setActive(boolean active, LivingEntityBeyonderCapability cap, LivingEntity target){
        this.active = active;
        if(!active){
            stopEffects(cap, target);
        }
    }

    public int getCost(){
        return this.cost;
    }

    public boolean isActive(){
        return this.active;
    }

    public void effectTick(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(active){
            if(this.maxLife < 1){
                doTick(cap, target);
                return;
            } else if(lifetime > maxLife){
                stopEffects(cap, target);
                return;
            }
            doTick(cap, target);
        }
        if(maxLife > 0){
            this.lifetime++;
        }
    }


    /**
     * called anytime the effect is added to a player (including when he loads into the world)
     * as such, be wary of doing things that require a connection (like adding an effect or sending system messages)
     * mob effects should be added on the doTick function, not on the onAcquire
     * @param cap
     * @param target
     */
    public abstract void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target);
    protected abstract void doTick(LivingEntityBeyonderCapability cap, LivingEntity target);
    public abstract void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target);

    public void toNbt(CompoundTag nbt){
        nbt.putInt("level", sequenceLevel);
        nbt.putInt("lifetime", lifetime);
        nbt.putInt("maxLife", maxLife);
        nbt.putBoolean("active", active);
        nbt.putString("ID", effectId);
    }

    public void loadNBTData(CompoundTag nbt){
    }


    public boolean shouldPersistInDeath() {
        return false;
    }

    public BeyonderEffect setId(String effectId) {
        this.effectId = effectId;
        return this;
    }
}
