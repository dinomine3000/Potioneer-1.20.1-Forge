package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.dinomine.potioneer.event.AbilityCastEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;

public abstract class Ability {
    private boolean state = true;
    private int cooldown = 0;
    private int maxCooldown = 1;
    protected int sequenceLevel;
    /**
     * used when revoking the ability. this stores the previous state to be recovered.
     */
    private boolean previousState = true;
    protected String abilityId;
    protected AbilityKey abilityKey = new AbilityKey();
    private Function<Integer, Integer> costFunction = null;
    private CompoundTag abilityData = new CompoundTag();
    protected boolean isActive = true;
    protected boolean isPassive = false;
    private int temporaryCooldown = -1;
    protected int defaultMaxCooldown = 20;
    private final Map<UUID, Integer> activeLevelModifiers = new HashMap<>();
    private UUID instanceId = UUID.randomUUID();
    public boolean isPassive(){return isPassive;}
    public UUID getInstanceId(){return instanceId;}

    public void receiveUpdateOnClient(AbilityInfo info, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(!target.level().isClientSide()) return;
        if(isEnabled() != info.isEnabled()){
            setEnabled(cap, target, info.isEnabled());
        }
        putOnCooldown(info.getCooldown(), target);
        setDataSilent(info.getData());
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

    public void setDataSilent(CompoundTag tag){
        this.abilityData = tag;
    }

    public AbilityInfo getAbilityInfo(){
        if(abilityKey == null){
            System.out.println("Warning: tried to get ability info with a null key");
            return Abilities.getInfo(abilityId, cooldown, maxCooldown, state,
                    getMainDescId(sequenceLevel), getAllDescId(sequenceLevel),
                    new AbilityKey(abilityId, sequenceLevel), sequenceLevel)
                    .withData(abilityData)
                    .withInstanceId(instanceId);
        }
        return Abilities.getInfo(abilityId, cooldown, maxCooldown, state,
                getMainDescId(sequenceLevel), getAllDescId(sequenceLevel),
                abilityKey, sequenceLevel)
                .withData(abilityData)
                .withInstanceId(instanceId);
    }

    protected abstract String getMainDescId(int sequenceLevel);

    protected LinkedHashSet<String> getAllDescId(int sequenceLevel){
        LinkedHashSet<String> res = new LinkedHashSet<>();
        for(int lvl = sequenceLevel + 1; lvl < 10; lvl++){
            if(getMainDescId(sequenceLevel).equalsIgnoreCase(getMainDescId(lvl))) continue;
            res.add(getMainDescId(lvl));
        }
        return res;
    };

    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     * @param sequenceLevel
     */
    public Ability(int sequenceLevel){
        this(sequenceLevel, 20);
    }
    public Ability(int sequenceLevel, int defaultMaxCooldown){
        this.sequenceLevel = sequenceLevel%10;
        this.defaultMaxCooldown = defaultMaxCooldown;
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
        if(abilityKey == null) return "";
        return this.abilityKey.getGroup();
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
        this.abilityKey = new AbilityKey(abilityList, abilityId, sequenceLevel);
        return this.abilityKey;
    }

    public AbilityKey setArtifactAbilityKey(UUID artifactId){
        this.abilityKey = new AbilityKey(PlayerAbilitiesManager.AbilityList.ARTIFACT.name(), abilityId, sequenceLevel, artifactId);
        return this.abilityKey;
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

    protected void setNextCooldownAs(int cooldownTicks){
        temporaryCooldown = cooldownTicks;
    }

    /**
     * puts the ability on cooldown.
     * only accepts positive or zero values
     * @param cooldownTicks
     */
    public boolean putOnCooldown(int cooldownTicks, LivingEntity target){
        if(cooldownTicks < 0) return false;
        maxCooldown = Math.max(cooldownTicks, 1);
        cooldown = cooldownTicks;
        if(target instanceof Player player) updateCooldownClient(player);
        return true;
    }

    private boolean putOnCooldown(LivingEntity target){
        putOnCooldown(temporaryCooldown >= 0 ? temporaryCooldown : defaultMaxCooldown, target);
        temporaryCooldown = -1;
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
        ent.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getAbilitiesManager().onAbilityUpdateData(this.getAbilityInfo(), cap, ent);
        });
    }

    public final void upgradeToLevel(int level, LivingEntityBeyonderCapability cap, LivingEntity target) {
        int clampedBaseLevel = Math.max(0, Math.min(9, level));

        // Update the base ability key
        if (this.abilityKey != null) {
            this.abilityKey = new AbilityKey(abilityKey.getGroup(), abilityKey.getAbilityId(), clampedBaseLevel);
        }

        // Recompute effective level using the new base level and existing temporary modifiers
        recalculateEffectiveLevel(cap, target);
    }

    /**
     * Applies or updates a temporary modifier from a specific source.
     *
     * @param sourceId         The UUID applying the modifier (e.g., an Effect, Item, or Ability).
     *                         This prevents identical magnitude effects from stacking unless they come from different sources/types.
     * @param levelDifference  The amount to shift level. Level 9 -> 8 is a buff of 1 level.
     *                         Pass negative values for buffs (lower sequence number), positive for debuffs.
     */
    public void applyTemporaryModifier(UUID sourceId, int levelDifference, LivingEntityBeyonderCapability cap, LivingEntity target) {
        if (levelDifference == 0) return;
        activeLevelModifiers.put(sourceId, levelDifference);
        recalculateEffectiveLevel(cap, target);
    }

    public void removeTemporaryModifier(UUID sourceId, LivingEntityBeyonderCapability cap, LivingEntity target) {
        if (activeLevelModifiers.remove(sourceId) != null) {
            recalculateEffectiveLevel(cap, target);
        }
    }

    private void recalculateEffectiveLevel(LivingEntityBeyonderCapability cap, LivingEntity target) {
        int baseLevel = abilityKey.getSequenceLevel();

        // Group active modifiers by their direction/type to prevent stacking identical magnitudes.
        // E.g., two +1 buffs from different sources shouldn't stack, but a +2 and a +1 buff should max out at +2.
        int maxBuff = 0;   // Buffs lower the sequence number (e.g., 9 -> 8 is -1)
        int maxDebuff = 0; // Debuffs raise the sequence number (e.g., 8 -> 9 is +1)

        for (int mod : activeLevelModifiers.values()) {
            if (mod < 0) {
                // Negative difference = Buff (e.g., Sequence 9 -> 8)
                maxBuff = Math.min(maxBuff, mod); // Find strongest buff (largest negative value)
            } else if (mod > 0) {
                // Positive difference = Debuff (e.g., Sequence 8 -> 9)
                maxDebuff = Math.max(maxDebuff, mod); // Find strongest debuff
            }
        }

        // Combined net difference (e.g., -2 buff + 1 debuff = -1 net shift)
        int netDifference = maxBuff + maxDebuff;
        int uncappedLevel = baseLevel + netDifference;
        int targetLevel = Math.max(0, Math.min(9, uncappedLevel));

        // Apply the actual transition if level changed
        if (this.sequenceLevel != targetLevel) {
            onUpgrade(this.sequenceLevel, targetLevel, cap, target);
            this.sequenceLevel = targetLevel;
            sendUpdateMessageToClient(target);
        }
    }
    /**
     * runs the abilities active methods (that is, primary() or secondary()) if the ability is off cooldown.
     * if an ability wants to run while on cooldown (that is, allow the player to cast the ability primary() or secondary() while on cooldown) it should override this.
     * @param cap
     * @param target
     * @param primary
     */
    public boolean castAbility(LivingEntityBeyonderCapability cap, LivingEntity target, boolean primary){
        return castAbility(cap, target, primary, new CompoundTag());
    }

    /**
     * runs the abilities active methods (that is, primary() or secondary()) if the ability is off cooldown.
     * if an ability wants to run while on cooldown (that is, allow the player to cast the ability primary() or secondary() while on cooldown) it should override this.
     * @param cap
     * @param target
     * @param primary
     * @param args CompoundTag arguments for the cast
     */
    public boolean castAbility(LivingEntityBeyonderCapability cap, LivingEntity target, boolean primary, CompoundTag args){
        if(cooldown != 0) return false;

        boolean cancelledCheck = MinecraftForge.EVENT_BUS.post(new AbilityCastEvent.Pre(this, target, primary, args));
        if(cancelledCheck) return false;
        if(primary){
            if(primary(cap, target, args)){
                MinecraftForge.EVENT_BUS.post(new AbilityCastEvent.Post(this, target, true, args));
                putOnCooldown(target);
                return true;
            }
        } else {
            if(secondary(cap, target, args)){
                MinecraftForge.EVENT_BUS.post(new AbilityCastEvent.Post(this, target, false, args));
                putOnCooldown(target);
                return true;
            }
        }
        return false;
    }
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target, CompoundTag args){return primary(cap, target);}
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target, CompoundTag args){return secondary(cap, target);}

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
     * if it returns true, it means itll be posted on the CastAbilityEvent and will be put on a default cooldown.
     * false means it wont.
     * @param cap
     * @param target
     * @return true if successfuly cast, false otherwise
     */
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target){return false;}

    /**
     * function that runs whenever the player casts the secondary ability
     * if it returns true, it means itll be posted on the CastAbilityEvent and will be put on a default cooldown.
     * false means it wont.
     * @param cap
     * @param target
     * @return true if successfuly cast, false otherwise
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

    public CompoundTag saveNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("cooldown", cooldown);
        tag.putBoolean("enabled", state);
        tag.putBoolean("prevState", previousState);
        tag.put("data", abilityData);
        tag.putUUID("instanceId", instanceId);
        return tag;
    }

    /**
     * function to load relevant NBT data.
     * generally not to be overwritten, except for abilities that add abilities like Recording or Replicating.
     * To do that, overwrite loadExtraNbtInfo(), and add abilities to a buffer list in AbilityManager
     * @param parentTag - the complete nbt tag for the abilities manager. Check if your own ability key is in here, and if so you can load it.
     */
    public void loadNbt(CompoundTag parentTag){
        if(parentTag.contains(abilityKey.toString())){
            CompoundTag abilityTag = parentTag.getCompound(abilityKey.toString());
            cooldown = abilityTag.getInt("cooldown");
            maxCooldown = Math.max(abilityTag.getInt("cooldown"), 1);
            state = abilityTag.getBoolean("enabled");
            previousState = abilityTag.getBoolean("prevState");
            if(abilityTag.contains("instanceId"))
                instanceId = abilityTag.getUUID("instanceId");
            Tag dataTag = abilityTag.get("data");
            if(dataTag != null)
                abilityData = (CompoundTag) dataTag;
            loadExtraNbtInfo(abilityTag);
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

    public Ability withCost(Function<Integer, Integer> costFunction){
        this.costFunction = costFunction;
        return this;
    }

    public Ability withCost(int constantCost){
        this.costFunction = ignored -> constantCost;
        return this;
    }

    public Ability withActives(boolean isActive, boolean isPassive){
        this.isActive = isActive;
        this.isPassive = isPassive;
        return this;
    }

    @Override
    public String toString() {
        if(abilityKey == null || abilityKey.isEmpty()) return getOuterId();
        return abilityKey.toString();
    }

    public AbilityKey getAbilityKey() {
        return this.abilityKey;
    }

    public boolean is(String ablId) {
        return this.abilityId.equalsIgnoreCase(ablId);
    }
    public boolean is(Ability abl) {
        return this.is(abl.abilityId);
    }
    public boolean is(UUID testId) {
        return this.instanceId.equals(testId);
    }

    public Ability withInstanceId(UUID instanceId){
        this.instanceId = instanceId;
        return this;
    }

    public List<Page> getPages(){return List.of();}

    public static Component getNameComponent(String abilityDescId){
        return Component.translatableWithFallback("ability_name.potioneer." + abilityDescId, StringUtils.capitalize(abilityDescId.replace("_", " ")));
    }
    public static Component getNameComponent(AbilityKey ablKey){
        String descId = Abilities.createAbilityInstance(ablKey).getAbilityInfo().descId();
        return getNameComponent(descId);
    }
}
