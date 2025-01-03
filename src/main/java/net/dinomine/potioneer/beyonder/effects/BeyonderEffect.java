package net.dinomine.potioneer.beyonder.effects;

import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public abstract class BeyonderEffect {
    protected int sequenceLevel;
    protected float cost;
    protected int lifetime = 0;
    protected int maxLife;
    protected BeyonderEffects.EFFECT ID;

    protected boolean active;

    public BeyonderEffects.EFFECT getId(){
        return this.ID;
    }
    public boolean is(BeyonderEffects.EFFECT id){
        return this.ID == id;
    }

    public boolean is(BeyonderEffect effect){
        return this.ID == effect.getId();
    }

    public boolean is(BeyonderEffects.EFFECT id, int seq){
        return is(id) && this.sequenceLevel == seq;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BeyonderEffect effect && effect.is(this) && this.sequenceLevel == effect.sequenceLevel;
    }

    public boolean endsWithin(int time){
        return this.maxLife - this.lifetime < time;
    }

    public void refreshTime(){
        this.lifetime = 0;
    }

    public void setLifetime(int life){
        this.lifetime = life;
    }

    public void setDuration(int ticks){
        this.maxLife = ticks;
    }


    /**
     * used for replacement purposes. will return true if this theyre the same effect but the argument is of a higher sequence
     * aka, will return true if the argument should replace this
     * @param effect
     * @return
     */
    public boolean isBetter(BeyonderEffect effect){
        return this.is(effect) && this.sequenceLevel >= effect.sequenceLevel;
    }

    public void setActive(boolean active, EntityBeyonderManager cap, LivingEntity target){
        this.active = active;
        if(!active){
            stopEffects(cap, target);
        }
    }
    public boolean isActive(){
        return this.active;
    }

    public void effectTick(EntityBeyonderManager cap, LivingEntity target){
        if(active){
            if(this.maxLife < 1){
                doTick(cap, target);
                return;
            } else if(lifetime++ > maxLife){
                stopEffects(cap, target);
                return;
            }
            doTick(cap, target);
        }
        if(maxLife > 0){
            this.lifetime++;
        }
    }


    protected abstract void doTick(EntityBeyonderManager cap, LivingEntity target);
    public abstract void stopEffects(EntityBeyonderManager cap, LivingEntity target);

    public void toNbt(CompoundTag nbt){
        nbt.putInt("level", sequenceLevel);
        nbt.putFloat("cost", cost);
        nbt.putInt("lifetime", lifetime);
        nbt.putInt("maxLife", maxLife);
        nbt.putBoolean("active", active);
        nbt.putString("ID", ID.name());
    }


}
