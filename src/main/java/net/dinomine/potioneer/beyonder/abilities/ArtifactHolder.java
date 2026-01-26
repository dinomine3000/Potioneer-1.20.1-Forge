package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ArtifactHolder {
    private final HashMap<AbilityKey, Ability> abilities = new HashMap<>();
    private final HashMap<AbilityKey, Downside> downsides = new HashMap<>();
    /**
     * this list contains all the abilities that the player wants to run from this artifact when interacting with the item.
     * say the artifact has door opening and extended reach. when interacting with the item, it would by default run both of these abilities.
     * instead, itll run only the abilities present in this array list.
     * now its a hashmap, where the corresponding boolean value tells the artifact what the default action is (between casting the primary or secondary ability)
     */
    private final HashMap<AbilityKey, Boolean> abilitiesToActivateOnItemInteract = new HashMap<>();
    private final UUID artifactId;
    private ItemStack item;

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


    public boolean castAbility(AbilityKey key, boolean primary, LivingEntityBeyonderCapability cap, LivingEntity target){
        Ability abl = abilities.get(key);
        if(abl == null) return false;
        if(!abl.castAbility(cap, target, primary)) return false;
        for(Downside ds: downsides.values()){
            ds.castAbility(cap, target, true);
        }
        return true;
    }

    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target){
        abilities.values().forEach(abl -> abl.onAcquire(cap, target));
        downsides.values().forEach(downside -> downside.onAcquire(cap, target));
    }

    public void onRemove(LivingEntityBeyonderCapability cap, LivingEntity target){
        abilities.values().forEach(abl -> abl.deactivate(cap, target));
        downsides.values().forEach(downside -> downside.deactivate(cap, target));
    }

    public void revokeAbilities(List<AbilityKey> abilitiesToRevoke, LivingEntityBeyonderCapability cap, LivingEntity target){

    }

    public void castDefaultAbilities(LivingEntityBeyonderCapability cap, LivingEntity target){
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

    public void passives(LivingEntityBeyonderCapability cap, LivingEntity target){
        abilities.values().forEach(abl -> abl.passive(cap, target));
        downsides.values().forEach(downside -> downside.passive(cap, target));
        abilities.values().forEach(Ability::tickCooldown);
        downsides.values().forEach(Ability::tickCooldown);
    }

    /**
     * saves the artifact to a compound tag
     * @param artifactTag the tag created for this artifact to use as it pleases.
     * @return artifactTag with the data written on it.
     */
    public CompoundTag saveToTag(CompoundTag artifactTag){
        artifactTag.putUUID("artifactId", artifactId);
        for(Ability abl: abilities.values()){
            artifactTag.put(abl.getKey().toString(), abl.saveNbt());
        }
        for(Ability abl: downsides.values()){
            artifactTag.put(abl.getKey().toString(), abl.saveNbt());
        }
        artifactTag.put("itemStack", item.save(new CompoundTag()));
        return artifactTag;
    }

    public static ArtifactHolder loadFromTag(CompoundTag artifactTag){
        UUID artifactId = artifactTag.getUUID("artifactId");
        List<Ability> abilities = new ArrayList<>();
        for(String stringKey: artifactTag.getAllKeys()){
            if(stringKey.equals("artifactId")) continue;
            AbilityKey key = AbilityKey.fromString(stringKey);
            if(key.isEmpty()) continue;
            Ability ability = Abilities.getAbilityInstanceByKey(key);
            ability.loadNbt(artifactTag);
            abilities.add(ability);
        }
        ItemStack stack = ItemStack.of(artifactTag.getCompound("itemStack"));
        return new ArtifactHolder(abilities, artifactId, stack);
    }

    public boolean isEmpty() {
        return abilities.isEmpty() && downsides.isEmpty();
    }

    public Ability getAbility(AbilityKey key) {
        if(!key.isArtifactKey() || !key.getArtifactId().equals(artifactId)) return null;
        return abilities.get(key);
    }

    public void updateOnClient(ArtifactHolder artifact) {

    }

    @Override
    public String toString() {
        return saveToTag(new CompoundTag()).toString();
    }

    public ItemStack getStack() {
        return item;
    }
}
