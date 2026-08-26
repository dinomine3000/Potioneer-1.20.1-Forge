package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.event.AbilityCastEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class Ability {

    /**
     * used when revoking the ability. this stores the previous state to be recovered.
     */
    private boolean previousState = true;
    private int temporaryCooldown = -1;
    protected int defaultMaxCooldown = 20;
    private final Map<UUID, Integer> activeLevelModifiers = new HashMap<>();
    protected boolean isPassive = false;
    private AbilityInfo abilityInfo;
    private UUID anchorId = null;

    public void preInit(ResourceLocation abilityLocation, int trueLevel, AbilityInfo.Group group) {
        this.abilityInfo = new AbilityInfo(abilityLocation);
        this.abilityInfo.setTrueSequenceLevel(trueLevel);
        this.abilityInfo.setSequenceLevel(trueLevel);
        this.abilityInfo.setGroup(group);
    }

    public void init() {}

    public final boolean isPassive() {
        return isPassive;
    }

    public UUID getInstanceId() {
        return abilityInfo.getInstanceId();
    }

    public final void receiveUpdateOnClient(AbilityInfo info, BeyonderCapability cap, LivingEntity target) {
        if (!target.level().isClientSide()) return;
        if (isEnabled() != info.isEnabled()) {
            setEnabled(cap, target, info.isEnabled());
        }
        putOnCooldown(info.getCooldown(), target);
        this.abilityInfo.copyFrom(info);
        onClientUpdate(cap, target);
    }

    public boolean isDownside() {
        return false;
    }

    protected CompoundTag getData() {
        return abilityInfo.getAbilityData().copy();
    }

    public void setData(CompoundTag tag, LivingEntity target) {
        if (!abilityInfo.setAbilityData(tag)) return;
        if (target instanceof Player player && !player.level().isClientSide()) sendUpdateMessageToClient(target);
    }

    public void setDataSilent(CompoundTag tag) {
        abilityInfo.setAbilityData(tag);
    }

    public AbilityInfo getAbilityInfo() {
        abilityInfo.setDescId(getMainDescId(abilityInfo.getSequenceLevel()));
        abilityInfo.setAllDescIds(getAllDescId(abilityInfo.getSequenceLevel()));
        abilityInfo.setHasSecondary(hasSecondary(getSequenceLevel()));
        return abilityInfo;
    }

    protected boolean hasSecondary(int level){return false;}

    protected abstract String getMainDescId(int sequenceLevel);
    public String getMainDescId(){return getMainDescId(getSequenceLevel());}

    /**
     * the first you give, is the first you see when cycling back.
     * @param sequenceLevel
     * @return
     */
    protected LinkedHashSet<String> getAllDescId(int sequenceLevel) {
        LinkedHashSet<String> res = new LinkedHashSet<>();
        for (int lvl = sequenceLevel + 1; lvl < 10; lvl++) {
            if (getMainDescId(sequenceLevel).equalsIgnoreCase(getMainDescId(lvl))) continue;
            res.add(getMainDescId(lvl));
        }
        return res;
    }

    /**
     * returns the Ability Id, or the ID for this ability in general, also called "inner id"
     * @return an inner ID like "water_affinity"
     */
    public ResourceLocation getAbilityId() {
        return abilityInfo.getAbilityId();
    }

    public void setOnArtifact(ItemStack artifactStack) {
        abilityInfo.setArtifactStack(artifactStack);
    }

    public boolean isEnabled() {
        return abilityInfo.isEnabled();
    }

    public int getSequenceLevel() {
        return abilityInfo.getSequenceLevel();
    }

    public int getTrueSequenceLevel() {
        return abilityInfo.getTrueSequenceLevel();
    }

    /**
     * flips the enabled state
     * @return the new enabled state
     */
    public boolean flipEnable(BeyonderCapability cap, LivingEntity target) {
        return setEnabled(cap, target, !isEnabled());
    }

    /**
     * returns the new enabled state
     * @param cap
     * @param target
     * @param enable
     * @return
     */
    public boolean setEnabled(BeyonderCapability cap, LivingEntity target, boolean enable) {
        if (!isEnabled() && enable) {
            abilityInfo.setEnabled(true);
            activate(cap, target);
            sendUpdateMessageToClient(target);
        } else if (isEnabled() && !enable) {
            abilityInfo.setEnabled(false);
            deactivate(cap, target);
            sendUpdateMessageToClient(target);
        }
        return isEnabled();
    }

    /**
     * Revokes (disables) the ability.
     */
    protected void revoke(BeyonderCapability cap, LivingEntity target) {
        if (abilityInfo.isRevoked()) return;
        previousState = isEnabled();
        abilityInfo.setRevoked(true);
        abilityInfo.setMaxCd(getCooldown());
        abilityInfo.setCooldown(-1);
        onRevoke(cap, target);
        if (target instanceof Player player) updateCooldownClient(player);
    }

    /**
     * Automatically re-enables the ability if it has been revoked.
     */
    protected void undoRevoke(BeyonderCapability cap, LivingEntity target) {
        if (!isRevoked()) return;
        abilityInfo.setRevoked(false);
        abilityInfo.setCooldown(getMaxCooldown());
        abilityInfo.setMaxCd(getCooldown());
        onUndoRevoke(cap, target);
        if (target instanceof Player player) updateCooldownClient(player);
    }

    public boolean isRevoked() {
        return abilityInfo.isRevoked();
    }

    public void tickCooldown(LivingEntity target) {
        if (isRevoked() || getCooldown() <= 0) return;
        abilityInfo.setCooldown(getCooldown() - 1);
        if (getCooldown() == 0 && target instanceof Player player) updateCooldownClient(player);
    }

    public void setNextCooldownAs(int cooldownTicks) {
        temporaryCooldown = cooldownTicks;
    }

    /**
     * puts the ability on cooldown.
     * only accepts positive or zero values
     * @param cooldownTicks
     */
    public boolean putOnCooldown(int cooldownTicks, LivingEntity target) {
        if (cooldownTicks < 0) return false;
        if (isRevoked()) return false;
        abilityInfo.setMaxCd(cooldownTicks);
        abilityInfo.setCooldown(cooldownTicks);
        if (target instanceof Player player) updateCooldownClient(player);
        return true;
    }

    private boolean putOnCooldown(LivingEntity target) {
        putOnCooldown(temporaryCooldown >= 0 ? temporaryCooldown : defaultMaxCooldown, target);
        temporaryCooldown = -1;
        return true;
    }

    public int getCooldown() {
        return abilityInfo.getCooldown();
    }

    public int getMaxCooldown() {
        return abilityInfo.getMaxCd();
    }

    public final void updateCooldownClient(Player player) {
        if (player.level().isClientSide()) return;
        sendUpdateMessageToClient(player);
    }

    private void sendUpdateMessageToClient(LivingEntity ent) {
        ent.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getAbilitiesManager().onAbilityUpdateData(this, cap, ent);
        });
    }

    public final void permanentlyUpgradeToLevel(int level, BeyonderCapability cap, LivingEntity target) {
        int clampedBaseLevel = Math.max(0, Math.min(9, level));
        abilityInfo.setTrueSequenceLevel(clampedBaseLevel);
        recalculateEffectiveLevel(cap, target);
    }

    /**
     * Applies or updates a temporary modifier from a specific source.
     *
     * @param sourceId        The UUID applying the modifier (e.g., an Effect, Item, or Ability).
     *                        This prevents identical magnitude effects from stacking unless they come from different sources/types.
     * @param levelDifference The amount to shift level. Level 9 -> 8 is a buff of 1 level.
     *                        Pass negative values for buffs (lower sequence number), positive for debuffs.
     */
    public void temporarilyUpgradeToLevel(UUID sourceId, int levelDifference, BeyonderCapability cap, LivingEntity target) {
        if (levelDifference == 0) return;
        activeLevelModifiers.put(sourceId, levelDifference);
        recalculateEffectiveLevel(cap, target);
    }

    public void removeTemporaryUpgrade(UUID sourceId, BeyonderCapability cap, LivingEntity target) {
        if (activeLevelModifiers.remove(sourceId) != null) {
            recalculateEffectiveLevel(cap, target);
        }
    }

    private void recalculateEffectiveLevel(BeyonderCapability cap, LivingEntity target) {
        int baseLevel = getTrueSequenceLevel();

        int maxBuff = 0;
        int maxDebuff = 0;

        for (int mod : activeLevelModifiers.values()) {
            if (mod < 0) {
                maxBuff = Math.min(maxBuff, mod);
            } else if (mod > 0) {
                maxDebuff = Math.max(maxDebuff, mod);
            }
        }

        int netDifference = maxBuff + maxDebuff;
        int uncappedLevel = baseLevel + netDifference;
        int targetLevel = Math.max(0, Math.min(9, uncappedLevel));

        if (getSequenceLevel() != targetLevel) {
            onUpgrade(getSequenceLevel(), targetLevel, cap, target);
            abilityInfo.setSequenceLevel(targetLevel);
            sendUpdateMessageToClient(target);
            cap.getAbilitiesManager().getDisabledAbilitiesManager().abilityChangedLevel(this, cap, target);
        }
    }

    /**
     * runs the abilities active methods (that is, primary() or secondary()) if the ability is off cooldown.
     * if an ability wants to run while on cooldown (that is, allow the player to cast the ability primary() or secondary() while on cooldown) it should override this.
     * @param cap
     * @param target
     * @param primary
     */
    public boolean castAbility(BeyonderCapability cap, LivingEntity target, boolean primary) {
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
    public boolean castAbility(BeyonderCapability cap, LivingEntity target, boolean primary, CompoundTag args) {
        if (getCooldown() != 0 || isRevoked()) return false;

        boolean cancelledCheck = MinecraftForge.EVENT_BUS.post(new AbilityCastEvent.Pre(this, target, primary, args));
        if (cancelledCheck) return false;
        if (isRevoked()) return false;
        if (primary) {
            if (primary(cap, target, args)) {
                MinecraftForge.EVENT_BUS.post(new AbilityCastEvent.Post(this, target, true, args));
                if (!target.level().isClientSide()) putOnCooldown(target);
                return true;
            }
        } else {
            if (secondary(cap, target, args)) {
                MinecraftForge.EVENT_BUS.post(new AbilityCastEvent.Post(this, target, false, args));
                if (!target.level().isClientSide()) putOnCooldown(target);
                return true;
            }
        }
        return false;
    }

    protected boolean primary(BeyonderCapability cap, LivingEntity target, CompoundTag args) {
        return primary(cap, target);
    }

    protected boolean secondary(BeyonderCapability cap, LivingEntity target, CompoundTag args) {
        return secondary(cap, target);
    }

    public void onUpgrade(int oldLevel, int newLevel, BeyonderCapability cap, LivingEntity target) {}

    public void onAcquire(BeyonderCapability cap, LivingEntity target) {}

    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        return false;
    }

    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        return false;
    }

    public void passive(BeyonderCapability cap, LivingEntity target) {}

    public void activate(BeyonderCapability cap, LivingEntity target) {}

    public void deactivate(BeyonderCapability cap, LivingEntity target) {}

    public void onAbilityRemoved(BeyonderCapability cap, LivingEntity target) {deactivate(cap, target);}

    public void onRevoke(BeyonderCapability cap, LivingEntity target) {
        setEnabled(cap, target, false);
    }

    public void onUndoRevoke(BeyonderCapability cap, LivingEntity target) {
        setEnabled(cap, target, previousState);
    }

    public static Ability constructAbility(AbilityInfo info){
        Optional<AbilityFactory> obj = Abilities.getFactory(info.getAbilityId());
        if(obj.isEmpty()){
            System.out.println("Couldnt find ability id for ability: " + info.toString());
            return null;
        }
        Ability abl = obj.get().construct(info.getTrueSequenceLevel(), info.getGroup());
        abl.abilityInfo.copyFrom(info);
        return abl;
    }
    public static Ability loadAndInitAbility(CompoundTag abilityTag){
        AbilityInfo info = AbilityInfo.deserializeNBT(abilityTag);
        Optional<AbilityFactory> obj = Abilities.getFactory(info.getAbilityId());
        if(obj.isEmpty()){
            System.out.println("couldnt find ability id for ability: " + info.toString() + " from tag " + abilityTag);
            return null;
        }
        Ability abl = Abilities.getFactory(info.getAbilityId()).get().construct(info.getTrueSequenceLevel(), info.getGroup());
        abl.loadTag(abilityTag);
        abl.init();
        return abl;
    }

    public void loadTag(CompoundTag abilityTag) {
        if (abilityTag == null) return;
        this.abilityInfo = AbilityInfo.deserializeNBT(abilityTag);
        if (abilityTag.contains("previousState")) {
            this.previousState = abilityTag.getBoolean("previousState");
        }
        activeLevelModifiers.clear();
        if (abilityTag.contains("activeLevelModifiers", Tag.TAG_LIST)) {
            ListTag modifiersTag = abilityTag.getList("activeLevelModifiers", Tag.TAG_COMPOUND);
            for (int i = 0; i < modifiersTag.size(); i++) {
                CompoundTag entryTag = modifiersTag.getCompound(i);
                activeLevelModifiers.put(entryTag.getUUID("source"), entryTag.getInt("mod"));
            }
        }
        if(abilityTag.hasUUID("anchor")) this.anchorId = abilityTag.getUUID("anchor");
    }

    public CompoundTag saveAbility() {
        CompoundTag tag = this.abilityInfo != null ? this.abilityInfo.serializeNBT() : new CompoundTag();
        tag.putBoolean("previousState", previousState);
        ListTag modifiersTag = new ListTag();
        for (Map.Entry<UUID, Integer> entry : activeLevelModifiers.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("source", entry.getKey());
            entryTag.putInt("mod", entry.getValue());
            modifiersTag.add(entryTag);
        }
        tag.put("activeLevelModifiers", modifiersTag);
        tag.putBoolean("downside", isDownside());
        if(anchorId != null) tag.putUUID("anchor", anchorId);
        return tag;
    }


    protected void onClientUpdate(BeyonderCapability cap, LivingEntity target) {}

    @Override
    public String toString() {
        return "Ability{" +
                "id=" + (abilityInfo != null ? abilityInfo.getAbilityId() : "null") +
                ", instanceId=" + getInstanceId() +
                ", sequenceLevel=" + getSequenceLevel() +
                ", enabled=" + isEnabled() +
                ", cooldown=" + getCooldown() +
                '}';
    }

    public boolean is(ResourceLocation ablId, int sequenceLevel) {
        return is(ablId) && getSequenceLevel() == sequenceLevel;
    }

    public boolean is(RegistryObject<AbilityFactory> fac) {
        return is(fac.get().getAblId());
    }
    public boolean is(ResourceLocation ablId) {
        return abilityInfo != null && abilityInfo.getAbilityId().equals(ablId);
    }

    public boolean is(UUID testId) {
        return getInstanceId().equals(testId);
    }

    public List<Page> getPages() {
        return List.of();
    }

    public static MutableComponent getNameComponent(String abilityDescId) {
        return Component.translatableWithFallback("ability_name.potioneer." + abilityDescId, StringUtils.capitalize(abilityDescId.replace("_", " ")));
    }
    public static MutableComponent getNameComponent(ResourceLocation ablId, int sequenceLevel) {
        Ability abl = Abilities.getFactoryAndConstruct(ablId, sequenceLevel, AbilityInfo.Group.INTRINSIC);
        if(abl == null) return Component.literal("Null Ability");
        return getNameComponent(abl.getMainDescId());
    }

    public boolean isOfGroup(AbilityInfo.Group group) {
        return abilityInfo != null && abilityInfo.getGroup() == group;
    }

    public void setInstanceId(UUID instanceId) {
        this.abilityInfo.setInstanceId(instanceId);
    }

    public boolean isAnchored(){return anchorId != null;}

    public UUID getAnchorId(){return anchorId;}

    public void anchor(UUID anchoId){this.anchorId = anchorId;}
}