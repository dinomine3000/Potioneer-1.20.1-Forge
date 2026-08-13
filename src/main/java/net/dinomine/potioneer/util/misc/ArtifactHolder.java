package net.dinomine.potioneer.util.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ArtifactHolder {
    protected final HashMap<AbilityKey, Ability> abilities = new HashMap<>();
    protected final HashMap<AbilityKey, Downside> downsides = new HashMap<>();
    /**
     * this list contains all the abilities that the player wants to run from this artifact when interacting with the item.
     * say the artifact has door opening and extended reach. when interacting with the item, it would by default run both of these abilities.
     * instead, itll run only the abilities present in this array list.
     * now its a hashmap, where the corresponding boolean value tells the artifact what the default action is (between casting the primary or secondary ability)
     */
    private final HashMap<AbilityKey, Boolean> abilitiesToActivateOnItemInteract = new HashMap<>();
    private final UUID artifactId;
    protected ItemStack item;

    public UUID getArtifactId(){
        return artifactId;
    }

    public List<AbilityKey> getAbilityKeys() {
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
        for(Ability abl: abilities){
            AbilityKey key = abl.setArtifactAbilityKey(artifactId);
            if(abl.isDownside())
                this.downsides.put(key, (Downside) abl);
            else {
                this.abilities.put(key, abl);
                abilitiesToActivateOnItemInteract.put(key, true);
            }
        }
        this.item = stack;
        this.artifactId = artifactId;
    }

    public ArtifactHolder withStack(ItemStack stack) {
        if(stack == null) return this;
        this.item = stack;
        return this;
    }

    /**
     * for creating a new artifact
     * @param abilities
     * @param downsides
     */
    public ArtifactHolder(List<Ability> abilities, List<Downside> downsides){
        this(abilities, UUID.randomUUID(), ItemStack.EMPTY);
    }


    public boolean castAbility(AbilityKey key, boolean primary, BeyonderCapability cap, LivingEntity target, CompoundTag args){
        Ability abl = abilities.get(key);
        if(abl == null) return false;
        if(!abl.castAbility(cap, target, primary, args)) return false;
        for(Downside ds: downsides.values()){
            ds.castAbility(cap, target, true);
        }
        return true;
    }

    public void onAcquire(BeyonderCapability cap, LivingEntity target){
        abilities.values().stream().filter(abl -> !abl.isPassive() || abl.isEnabled()).forEach(abl -> abl.onAcquire(cap, target));
        downsides.values().forEach(downside -> downside.onAcquire(cap, target));
    }

    public void onRemove(BeyonderCapability cap, LivingEntity target){
        abilities.values().forEach(abl -> abl.deactivate(cap, target));
        downsides.values().forEach(downside -> downside.deactivate(cap, target));
    }

    public void revokeAbilities(List<AbilityKey> abilitiesToRevoke, BeyonderCapability cap, LivingEntity target){

    }

    public void castDefaultAbilities(BeyonderCapability cap, LivingEntity target){
        if(abilitiesToActivateOnItemInteract.isEmpty()) return;
        boolean flag = false;
        for(AbilityKey key: abilitiesToActivateOnItemInteract.keySet()){
            if(abilities.get(key).castAbility(cap, target, abilitiesToActivateOnItemInteract.get(key))) flag = true;
        }
        if(!flag) return;
        for(Downside ds: downsides.values()){
            ds.castAbility(cap, target, true);
        }
    }

    public void passives(BeyonderCapability cap, LivingEntity target){
        abilities.values().forEach(abl -> abl.passive(cap, target));
        downsides.values().forEach(downside -> downside.passive(cap, target));
        abilities.values().forEach(abl -> abl.tickCooldown(target));
        downsides.values().forEach(downside -> downside.tickCooldown(target));
    }

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
        return artifact.withStack(stack);
    }

    public static ArtifactHolder loadFromTag(CompoundTag artifactTag){
        return loadFromTag(artifactTag, null);
    }

    public boolean isEmpty() {
        return abilities.isEmpty() && downsides.isEmpty();
    }

    public Ability getAbility(AbilityKey key) {
        if(!key.isArtifactKey() || !key.getArtifactId().equals(artifactId)) return null;
        return abilities.get(key);
    }

    public Collection<Ability> getAbilities() {
        return abilities.values();
    }

    public void updateOnClient(ArtifactHolder artifact) {

    }

    @Override
    public String toString() {
        return saveToTag(true).toString();
    }

    public ItemStack getStack() {
        return item;
    }

    public ArtifactHolder updateItemTags() {
        ItemStack returnItem = item.copy();
        MysticalItemHelper.updateArtifactTagOnItem(this, returnItem);
        return MysticalItemHelper.getArtifactFromItem(returnItem);
    }
}
