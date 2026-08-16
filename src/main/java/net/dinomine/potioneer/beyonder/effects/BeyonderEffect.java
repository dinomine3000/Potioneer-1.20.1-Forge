package net.dinomine.potioneer.beyonder.effects;

import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.BeyonderEffectSyncMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public abstract class BeyonderEffect {
    protected int sequenceLevel;
    protected int lifetime = 0;
    protected int maxLife;
    public String name;
    protected String effectId;
    protected int cost = 0;
    protected boolean visible;
    protected Priority priority = Priority.MEDIUM;

    public final int getPriority(){return priority.value;}

    public void onUpdateReceivedOnClient(BeyonderCapability cap, LivingEntity target) {}

    public boolean onDie(LivingDeathEvent event, BeyonderCapability cap, LivingEntity target) {
        return false;
    }

    public enum Priority {
        VERY_HIGH(5),
        HIGH(4),
        MEDIUM(3),
        LOW(2),
        VERY_LOW(1);
        private final int value;

        Priority(int value) {
            this.value = value;
        }
    }
    public boolean canAdd(BeyonderCapability cap, LivingEntity target){return true;}

    public int getMaxLife(){
        return maxLife;
    }

    public boolean canBeCleansed(){return true;}

    public BeyonderEffect() {
        setPriority(9);
    }

    /**
     * Override this function to define the priority of this effect.
     * uses the default value 9.
     * @param sequenceLevel
     */
    protected void setPriority(int sequenceLevel){
        this.priority = Priority.MEDIUM;
    }

    public BeyonderEffect withParams(int sequence, int time, boolean visible) {
        return withParams(sequence, time, visible, 0);
    }

    public BeyonderEffect withParams(int sequence, int time, boolean visible, int cost) {
        this.sequenceLevel = sequence%10;
        this.lifetime = time == -1 ? -2 : 0;
        this.maxLife = time;
        this.visible = visible;
        this.cost = cost;
        setPriority(sequenceLevel%10);
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
    public boolean isOrBetter(int seq){
        return this.sequenceLevel <= seq;
    }

    public boolean isOrWorse(String id, int seq){
        return is(id) && this.sequenceLevel >= seq;
    }
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BeyonderEffect effect && effect.is(this) && this.sequenceLevel == effect.sequenceLevel;
    }

    public boolean endsWithin(int time){
        if(maxLife < 1) return false;
        return this.maxLife - this.lifetime < time;
    }

    public void refreshTime(BeyonderCapability cap, LivingEntity target, BeyonderEffect effect){
        if(maxLife < 0) return;
        this.maxLife = Math.max(maxLife, effect.maxLife);
        this.lifetime = 0;
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
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability>  attackerCap, boolean calledOnVictim){return false;}

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
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability>  attackerCap, boolean calledOnVictim){return false;}

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
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability>  attackerCap, boolean calledOnVictim) {return false;}
    /**
     * used for replacement purposes. will return true if theyre the same effect but the argument is of a higher sequence
     * aka, will return true if the argument should replace this
     * @param effect
     * @return
     */
    public boolean isSameOrBetter(BeyonderEffect effect){
        return this.is(effect) && this.sequenceLevel <= effect.sequenceLevel;
    }

    public boolean isBetter(BeyonderEffect effect){
        return this.is(effect) && this.sequenceLevel < effect.sequenceLevel;
    }

    public void setActive(boolean active, BeyonderCapability cap, LivingEntity target){
        this.visible = active;
        if(!active){
            stopEffects(cap, target);
        }
    }

    public int getCost(){
        return this.cost;
    }

    public void effectTick(BeyonderCapability cap, LivingEntity target){
        doTick(cap, target);
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
    public void onAcquire(BeyonderCapability cap, LivingEntity target){};

    /**
     * same as onAcquire, but you also get information of whether its from loading into the world or not
     * @param cap
     * @param target
     * @param fromLoading
     */
    public void onAcquire(BeyonderCapability cap, LivingEntity target, boolean fromLoading){onAcquire(cap, target);};
    protected abstract void doTick(BeyonderCapability cap, LivingEntity target);
    public abstract void stopEffects(BeyonderCapability cap, LivingEntity target);

    public void toNbt(CompoundTag nbt){
        nbt.putInt("level", sequenceLevel);
        nbt.putInt("cost", cost);
        nbt.putInt("maxLife", maxLife);
        nbt.putBoolean("active", visible);
        nbt.putInt("lifetime", lifetime);
        nbt.putString("ID", effectId);
    }

    /**
     * function that effects can utilize to load their custom effect information
     * @param nbt
     */
    public void loadNBTData(CompoundTag nbt){
    }

    public static BeyonderEffect readEffectFromNBTTag(CompoundTag tag){
        if(tag == null) return null;
        BeyonderEffects.BeyonderEffectType type = BeyonderEffects.getEffect(tag.getString("ID"));

        if (type == null) {
            System.out.println("Warning: read NBT data of a null effect: " + tag);
            return null;
        }

        BeyonderEffect effect = type.createInstance(
                tag.getInt("level"),
                tag.getInt("cost"),
                tag.getInt("maxLife"),
                tag.getBoolean("active"));
        effect.setLifetime(tag.getInt("lifetime"));
        effect.loadNBTData(tag);
        return effect;
    }
    public void writeToBuffer(FriendlyByteBuf buffer){
        CompoundTag tag = new CompoundTag();
        toNbt(tag);
        buffer.writeNbt(tag);
    }

    /**
     * should only run on client side. adds the effect with unlimited duration, 0 cost and active
     * @param buffer
     * @return
     */
    public static BeyonderEffect readFromBuffer(FriendlyByteBuf buffer){
        return readEffectFromNBTTag(buffer.readNbt());
    }


    public void sendDataToClient(Player player){
        if(!(player instanceof ServerPlayer serverPlayer)) return;
        PacketHandler.sendToPlayer(new BeyonderEffectSyncMessage(List.of(this), BeyonderEffectSyncMessage.Operation.UPDATE), serverPlayer);
    }

    public boolean shouldPersistInDeath() {
        return false;
    }

    public BeyonderEffect setId(String effectId) {
        this.effectId = effectId;
        return this;
    }
}

