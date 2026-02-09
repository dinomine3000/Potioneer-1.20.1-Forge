package net.dinomine.potioneer.beyonder.effects;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Optional;

public abstract class BeyonderEffect {
    protected int sequenceLevel;
    protected int lifetime = 0;
    protected int maxLife;
    public String name;
    protected String effectId;
    protected int cost = 0;

    public boolean canAdd(LivingEntityBeyonderCapability cap, LivingEntity target){return true;}

    public int getMaxLife(){
        return maxLife;
    }

    protected boolean active;

    public BeyonderEffect() {
    }

    public BeyonderEffect withParams(int sequence, int time, boolean active) {
        return withParams(sequence, time, active, 0);
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

    public void refreshTime(LivingEntityBeyonderCapability cap, LivingEntity target, BeyonderEffect effect){
        if(maxLife < 0 && effect.maxLife >= 0){
            maxLife = effect.maxLife;
            this.lifetime = 0;
        } else if(maxLife >= 0) {
            this.maxLife += effect.maxLife;
        }
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
     * runs when the victim takes damage from the attacker
     * @param event
     * @param victim
     * @param attacker
     * @return whether it should cancel the event or not
     */
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, Optional<LivingEntityBeyonderCapability> optAttackerCap, boolean calledOnVictim){return false;}

    /**
     * runs when the damage has been approved and the amount is being calculated (reduced, increased)
     * @param event
     * @param victim
     * @param attacker
     * @param victimCap
     * @param attackerCap
     * @param calledOnVictim
     * @return
     */
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, LivingEntityBeyonderCapability attackerCap, boolean calledOnVictim){return false;}

    /**
     * runs when verifying a damage proposal. here is where you cancel it.
     * @param event
     * @param victim
     * @param attacker
     * @param victimCap
     * @param attackerCap
     * @param calledOnVictim
     * @return
     */
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, LivingEntityBeyonderCapability attackerCap, boolean calledOnVictim) {return false;}
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
        nbt.putInt("cost", cost);
        nbt.putBoolean("active", active);
        nbt.putString("ID", effectId);
    }

    //nbt data is loaded in the effects manager

    /**
     * function that effects can utilize to load their custom effect information
     * @param nbt
     */
    public void loadNBTData(CompoundTag nbt){
    }

    public void writeToBuffer(FriendlyByteBuf buffer){
        CompoundTag tag = new CompoundTag();
        BufferUtils.writeStringToBuffer(getId(), buffer);
        buffer.writeInt(sequenceLevel);
        buffer.writeNbt(tag);
    }

    /**
     * should only run on client side. adds the effect with unlimited duration, 0 cost and active
     * @param buffer
     * @return
     */
    public static BeyonderEffect readFromBuffer(FriendlyByteBuf buffer){
        String id = BufferUtils.readString(buffer);
        int level = buffer.readInt();
        CompoundTag tag = buffer.readNbt();
        BeyonderEffect eff = BeyonderEffects.byId(id, level, 0, -1, true);
        eff.loadNBTData(tag);
        return eff;
    }


    public boolean shouldPersistInDeath() {
        return false;
    }

    public BeyonderEffect setId(String effectId) {
        this.effectId = effectId;
        return this;
    }
}

