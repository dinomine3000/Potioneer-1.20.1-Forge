package net.dinomine.potioneer.util.misc;

import lombok.Getter;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ArtifactHolder {
    protected final HashMap<UUID, Ability> abilities = new HashMap<>();
    protected final HashMap<UUID, Downside> downsides = new HashMap<>();
    /**
     * this list contains all the abilities that the player wants to run from this artifact when interacting with the item.
     * say the artifact has door opening and extended reach. when interacting with the item, it would by default run both of these abilities.
     * instead, itll run only the abilities present in this array list.
     * now its a hashmap, where the corresponding boolean value tells the artifact what the default action is (between casting the primary or secondary ability)
     */
    private final HashMap<UUID, Boolean> abilitiesToActivateOnItemInteract = new HashMap<>();
    @Getter
    private final UUID artifactId;
    @Getter
    protected ItemStack item;
    //1 active cast = 60 passive seconds
    protected float chargeSeconds = -1;
    protected boolean needsCharge = false;

    public void charge(float chargeSeconds){
        this.chargeSeconds += chargeSeconds;
    }

    public List<UUID> getAbilityIds() {
        return new ArrayList<>(abilities.keySet().stream().toList());
    }

    public List<AbilityInfo> getAbilitiesInfo(boolean includeDownsides){
        ArrayList<Ability> result = new ArrayList<>(abilities.values());
        if(includeDownsides){
            result.addAll(downsides.values());
        }
        return result.stream().map(Ability::getAbilityInfo).toList();
    }

    /**
     * for reading an artifact from a tag (buffers, loading the world, etc...)
     * @param abilities
     * @param artifactId
     */
    public ArtifactHolder(List<Ability> abilities, UUID artifactId, ItemStack stack){
        setStack(stack);
        for(Ability abl: abilities){
            abl.setOnArtifact(this.item);
            if(abl.isDownside())
                this.downsides.put(abl.getInstanceId(), (Downside) abl);
            else {
                this.abilities.put(abl.getInstanceId(), abl);
                abilitiesToActivateOnItemInteract.put(abl.getInstanceId(), true);
            }
        }
        this.artifactId = artifactId;
    }

    public ArtifactHolder(List<Ability> abilities, UUID artifactId, ItemStack stack, float useInSeconds){
        this(abilities, artifactId, stack);
        this.chargeSeconds = useInSeconds;
        this.needsCharge = true;
    }

    public ArtifactHolder withStack(ItemStack stack) {
        if(stack == null) return this;
        setStack(stack);
        for(Ability abl: abilities.values()) abl.setOnArtifact(this.item);
        return this;
    }

    private void setStack(ItemStack stack){
        this.item = stack.copy();
        CompoundTag tag = this.item.getOrCreateTag();
        tag.remove(ModNbtUtils.ARTIFACT_TAG_ID);
        this.item.setTag(tag);
    }


    public boolean castAbility(UUID abilityId, boolean primary, BeyonderCapability cap, LivingEntity target, CompoundTag args){
        if(outOfCharge()) return false;
        Ability abl = abilities.get(abilityId);
        if(abl == null) return false;
        if(!abl.castAbility(cap, target, primary, args)) return false;
        for(Downside ds: downsides.values()){
            ds.castAbility(cap, target, true);
        }
        if(needsCharge) chargeSeconds -= 60;
        return true;
    }

    public void onAcquire(BeyonderCapability cap, LivingEntity target){
        abilities.values().stream().filter(abl -> !abl.isPassive() || abl.isEnabled()).forEach(abl -> abl.onAcquire(cap, target));
        downsides.values().forEach(downside -> downside.onAcquire(cap, target));
    }

    public void onRemove(BeyonderCapability cap, LivingEntity target){
        abilities.values().forEach(abl -> abl.onAbilityRemoved(cap, target));
        downsides.values().forEach(downside -> downside.onAbilityRemoved(cap, target));
    }

    public void castDefaultAbilities(BeyonderCapability cap, LivingEntity target){
        if(abilitiesToActivateOnItemInteract.isEmpty()) return;
        if(outOfCharge()) return;
        boolean flag = false;
        for(UUID id: abilitiesToActivateOnItemInteract.keySet()){
            if(abilities.get(id).castAbility(cap, target, abilitiesToActivateOnItemInteract.get(id))) flag = true;
        }
        if(!flag) return;
        for(Downside ds: downsides.values()){
            ds.castAbility(cap, target, true);
        }
        if(needsCharge) chargeSeconds -= 60;
    }

    public void passives(BeyonderCapability cap, LivingEntity target){
        if(outOfCharge()) return;
        abilities.values().forEach(abl -> abl.passive(cap, target));
        downsides.values().forEach(downside -> downside.passive(cap, target));
        abilities.values().forEach(abl -> abl.tickCooldown(target));
        downsides.values().forEach(downside -> downside.tickCooldown(target));
        if(abilities.values().stream().anyMatch(abl -> abl.isPassive() && abl.isEnabled()) && needsCharge){
            chargeSeconds -= 1/20f;
            if(chargeSeconds <= 0) updateItemTags();
        }
    }

    private boolean outOfCharge(){return needsCharge && chargeSeconds <= 0;}

    /**
     * saves the artifact to a compound tag
     * @param saveItem true if it should save the item. set this to false whenever able to not inflate the tag with recursion
     * @return artifactTag with the data written on it.
     */
    public CompoundTag saveToTag(boolean saveItem){
        return ModNbtUtils.ArtifactInfoTag.getTagFromArtifactHolder(this, saveItem);
    }

    public static ArtifactHolder loadFromTag(CompoundTag artifactTag, ItemStack stack){
        ArtifactHolder artifact = ModNbtUtils.ArtifactInfoTag.getArtifactHolderFromTag(artifactTag);
        if(artifact == null) return null;
        //if(artifact.needsCharge && artifact.chargeSeconds <= 0) return null;
        return artifact.withStack(stack);
    }

    public static ArtifactHolder loadFromTag(CompoundTag artifactTag){
        return loadFromTag(artifactTag, null);
    }

    public boolean isEmpty() {
        return abilities.isEmpty() && downsides.isEmpty();
    }

    public Ability getAbility(UUID id) {
        return abilities.get(id);
    }

    public Collection<Ability> getAbilities() {
        return abilities.values();
    }

    @Override
    public String toString() {
        return saveToTag(true).toString();
    }

    public ItemStack getStack() {
        return item.copy();
    }

    public ArtifactHolder updateItemTags() {
        ItemStack returnItem = item.copy();
        MysticalItemHelper.updateArtifactTagOnItem(this, returnItem);
        return MysticalItemHelper.getArtifactFromItem(returnItem);
    }

    public boolean hasAbility(UUID instanceId) {
        return abilities.containsKey(instanceId) || downsides.containsKey(instanceId);
    }
}
