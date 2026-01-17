package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.AbilitySyncMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.Function;

public abstract class Ability {
    private boolean state = true;
    private int cooldown = 0;
    private int maxCooldown = 1;
    protected int defaultMaxCooldown = 20;
    protected int sequenceLevel;
    /**
     * used when revoking the ability. this stores the previous state to be recovered.
     */
    private boolean previousState = true;
    protected String abilityId;
    protected AbilityKey key = new AbilityKey();
    private Function<Integer, Integer> costFunction;
    private CompoundTag abilityData = new CompoundTag();

    public void receiveUpdateOnClient(AbilityInfo info, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(!target.level().isClientSide()) return;
        if(isEnabled() != info.isEnabled()){
            setEnabled(cap, target, info.isEnabled());
        }
        putOnCooldown(info.getCooldown(), target);
        setData(info.getData(), target);
    }

    public boolean isDownside(){
        return false;
    }

    protected CompoundTag getData(){
        return abilityData;
    }

    public void setData(CompoundTag tag, LivingEntity target){
        this.abilityData = tag;
        if(target instanceof Player player && !player.level().isClientSide())  sendUpdateMessageToClient(target);
    }

    public AbilityInfo getAbilityInfo(){
        if(key == null){
            System.out.println("Warning: tried to get ability info with a null key");
            return Abilities.getInfo(abilityId, cooldown, maxCooldown, state, getDescId(sequenceLevel), new AbilityKey()).withData(abilityData);
        }
        return Abilities.getInfo(abilityId, cooldown, maxCooldown, state, getDescId(sequenceLevel), key).withData(abilityData);
    }

    protected abstract String getDescId(int sequenceLevel);

    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     * @param sequenceLevel
     */
    public Ability(int sequenceLevel){
        this.sequenceLevel = sequenceLevel%10;
    }

    protected void setCost(Function<Integer, Integer> costFunction){
       this.costFunction = costFunction;
    }

    protected int cost(){
        return costFunction == null ? 0 : costFunction.apply(sequenceLevel%10);
    }

    public int getSequenceLevel(){
        return sequenceLevel;
    }

    public String getType(){
        if(key == null) return "";
        return this.key.getGroup();
    }

    /**
     * returns the Ability Id, or the ID for this ability in general, also called "inner id"
     * @return an inner ID like "water_affinity"
     */
    public String getAbilityId(){
        return abilityId;
    }

    /**
     * returns the outer id, or an ID that can identify this specific ability, by mixing its normal/inner id with its sequence level
     * @return an outer id like "water_affinity:9"
     */
    public String getOuterId(){
        return getAbilityId().concat(":" + sequenceLevel);
    }

    /**
     * function to update the complete ability id.
     * in it, it contains the outer id, as well as an identifier for the source of the ability (was it recorded, intrinstic, replicated etc...)
     * @param abilityList an identifier for the group this ability belongs to (like Recorded, Replicated, Intrinsice, Grazed etc...)
     */
    public AbilityKey setAbilityKey(String abilityList) {
        this.key = new AbilityKey(abilityList, abilityId, sequenceLevel);
        return this.key;
    }

    public AbilityKey setArtifactAbilityKey(UUID artifactId){
        this.key = new AbilityKey(PlayerAbilitiesManager.AbilityList.ARTIFACT.name(), abilityId, sequenceLevel, artifactId);
        return this.key;
    }

    public boolean isEnabled(){
        return state;
    }

    /**
     * flips the enabled state
     * @return the new enabled state
     */
    public boolean flipEnable(LivingEntityBeyonderCapability cap, LivingEntity target){
        return setEnabled(cap, target, !state);
    }

    /**
     * returns the new enabled state
     * @param cap
     * @param target
     * @param enable
     * @return
     */
    public boolean setEnabled(LivingEntityBeyonderCapability cap, LivingEntity target, boolean enable){
        if(!state && enable){
            state = true;
            activate(cap, target);
            sendUpdateMessageToClient(target);
        } else if(state && !enable){
            state = false;
            deactivate(cap, target);
            sendUpdateMessageToClient(target);
        }
        return state;
    }


    /**
     * Function that will put the ability on a special cooldown
     * here, the time left will not be shown to the player, instead itll show the disabled/block symbol
     * -1 will disable it indefinitely
     * any value below -2 will function like a cooldown, and it will automatically re-enable after the time runs out (once it reaches -2)
     * @param time - time in ticks until its to be re-enabled. setting this to 0 means removing the ability from cooldown.
     */
    public void revoke(int time, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(time == 0) return;
        if(cooldown >= 0){
            onRevoke(cap, target);
            maxCooldown = Math.max(cooldown, 20);
        }
        if(time > 0) time = -time;
        cooldown = time;
        if (target instanceof Player player) updateCooldownClient(player);
    }

    /**
     * revokes the ability indefinitely
     */
    public void revoke(LivingEntityBeyonderCapability cap, LivingEntity target){
        revoke(-1, cap, target);
    }

    /**
     * automatically re-enables the ability if its been revoked. if its on cooldown, it does nothing
     */
    public void undoRevoke(LivingEntityBeyonderCapability cap, LivingEntity target){
        putOnCooldown(maxCooldown, target);
        onUndoRevoke(cap, target);
    }

    public boolean isRevoked(){
        return cooldown < 0;
    }

    public void tickCooldown(){
        /*
         * values for cooldown:
         * >0 -> just tick down
         * = 0 -> its off cooldown
         * = -1 -> indefinitely disabled
         * = -2 -> enabled, jump to 0
         * < -2 -> disabled for a time, just tick up until it reaches -2. wont show to the player how long its left
         */
        if(cooldown == -2) cooldown = 0;
        if(cooldown > 0) cooldown--;
        if(cooldown < -2) cooldown++;
    }

    /**
     * puts the ability on cooldown.
     * only accepts positive or zero values
     * @param cooldownTicks
     */
    public boolean putOnCooldown(int cooldownTicks, LivingEntity target){
        if(cooldownTicks < 0) return false;
        if(maxCooldown < cooldownTicks) maxCooldown = cooldownTicks;
        cooldown = cooldownTicks;
        if(target instanceof Player player) updateCooldownClient(player);
        return true;
    }

    public boolean putOnCooldown(LivingEntity target){
        putOnCooldown(defaultMaxCooldown, target);
        return true;
    }

    public int getCooldown(){
        return cooldown;
    }

    public int getMaxCooldown(){
        return maxCooldown;
    }

    public void updateCooldownClient(Player player) {
        if(player.level().isClientSide()) return;
        sendUpdateMessageToClient(player);
    }

    private void sendUpdateMessageToClient(LivingEntity ent){
        if(ent instanceof Player player && !player.level().isClientSide()) PacketHandler.sendMessageSTC(new AbilitySyncMessage(getAbilityInfo(), AbilitySyncMessage.UPDATE), player);
    }

    public void setSequenceLevelSilent(int level){
        sequenceLevel = level;
        if(key != null)
            this.key = new AbilityKey(key.getGroup(), key.getAbilityId(), level);
    }

    public void upgradeToLevel(int level, LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(sequenceLevel == level) return;
        onUpgrade(sequenceLevel, level, cap, target);
        sequenceLevel = level;
        if(this.key != null)
            this.key = new AbilityKey(key.getGroup(), key.getAbilityId(), level);
    }

    /**
     * runs the abilities active methods (that is, primary() or secondary()) if the ability is off cooldown.
     * if an ability wants to run while on cooldown (that is, allow the player to cast the ability primary() or secondary() while on cooldown) it should override this.
     * @param cap
     * @param target
     * @param primary
     */
    public void castAbility(LivingEntityBeyonderCapability cap, LivingEntity target, boolean primary){
        if(cooldown != 0) return;
        if(primary){
            if(primary(cap, target)){
                putOnCooldown(target);
            }
        } else {
            if(secondary(cap, target)){
                putOnCooldown(target);
            }
        }
    }

    /**
     * code that will run whenever the level of this ability is changed.
     * mainly called when ascending the sequence. Generally speaking, this method is useless, but if an ability changes with sequence and needs to know
     * when that level changes, this is the function to listen to.
     * @param oldLevel
     * @param newLevel
     * @param cap
     * @param target
     */
    public void onUpgrade(int oldLevel, int newLevel, LivingEntityBeyonderCapability cap, LivingEntity target){}

    /**
     * function that runs when the player acquires the ability
     * @param cap
     * @param target
     */
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target){}

    /**
     * function that runs whenever the player casts the main ability
     * @param cap
     * @param target
     * @return true to be put on default cooldown, false to not be put on default cooldown
     */
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target){return false;}

    /**
     * function that runs whenever the player casts the secondary ability
     * @param cap
     * @param target
     * @return true to be put on default cooldown, false to not be put on default cooldown
     */
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target){return false;}

    /**
     * function that runs every tick
     * @param cap
     * @param target
     */
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target){}

    /**
     * function that implements behaviour for every time the ability is activated (like changing stuff for a setup)
     * @param cap
     * @param target
     */
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target){}

    /**
     * function that implements behaviour for every time the ability is deactivated (like removing effects)
     * @param cap
     * @param target
     */
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target){}

    /**
     * function that implements behaviour for every time the ability is revoked.
     * By default, disables itself, which triggers deactivate(). Any abilities that dont want to be disabled on revoke should override this function
     * @param cap
     * @param target
     */
    public void onRevoke(LivingEntityBeyonderCapability cap, LivingEntity target){
        previousState = state;
        setEnabled(cap, target, false);
    }

    /**
     * function that implements behaviour for every time the ability is un-revoked.
     * By default, enables the ability, which triggers activate(). Any abilities that dont want to be enabled upon being un-revoked should override this function
     * @param cap
     * @param target
     */
    public void onUndoRevoke(LivingEntityBeyonderCapability cap, LivingEntity target){
        setEnabled(cap, target, previousState);
    }

    public Tag saveNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("cooldown", cooldown);
        tag.putBoolean("enabled", state);
        tag.putBoolean("prevState", previousState);
        tag.put("data", abilityData);
        return tag;
    }

    /**
     * function to load relevant NBT data.
     * generally not to be overwritten, except for abilities that add abilities like Recording or Replicating.
     * To do that, overwrite loadExtraNbtInfo(), and add abilities to a buffer list in AbilityManager
     * @param tag - the complete nbt tag for the abilities manager. Check if your own ability key is in here, and if so you can load it.
     */
    public void loadNbt(CompoundTag tag){
        if(tag.contains(key.toString())){
            CompoundTag tag2 = tag.getCompound(key.toString());
            cooldown = tag2.getInt("cooldown");
            maxCooldown = Math.max(tag2.getInt("cooldown"), 1);
            state = tag2.getBoolean("enabled");
            previousState = tag2.getBoolean("prevState");
            Tag dataTag = tag2.get("data");
            if(dataTag != null)
                abilityData = (CompoundTag) dataTag;
            loadExtraNbtInfo(tag2);
        }
    }

    /**
     * overwrite this for any ability that adds abilities, and add them to the buffer list.
     * this info should be stored in a shared memory for all abilities
     * @param tag
     */
    protected void loadExtraNbtInfo(CompoundTag tag){

    }

    public Ability withAbilityId(String ablId) {
        this.abilityId = ablId;
        return this;
    }

    @Override
    public String toString() {
        if(key == null || key.isEmpty()) return getOuterId();
        return key.toString();
    }

    public AbilityKey getKey() {
        return this.key;
    }
}
