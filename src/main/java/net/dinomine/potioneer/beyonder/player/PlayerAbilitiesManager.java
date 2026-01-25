package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.*;
import net.dinomine.potioneer.beyonder.abilities.misc.MysticalKnowledgeAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.AbilitySyncMessage;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerArtifactSyncSTC;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerCastAbilityMessageCTS;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.*;
import java.util.function.Consumer;

public class PlayerAbilitiesManager {
    private LinkedHashMap<AbilityKey, Ability> abilities = new LinkedHashMap<>();
    private LinkedHashMap<UUID, ArtifactHolder> artifacts = new LinkedHashMap<>();

    public ArrayList<AbilityKey> clientHotbar = new ArrayList<>();
    public AbilityKey quickAbility = new AbilityKey();

    @Override
    public String toString() {
        return "Abilities: " + abilities.keySet().stream().map(AbilityKey::toString).toList() +
                "\nArtifacts: " + artifacts.values().stream().map(ArtifactHolder::toString).toList();
    }

    public void copyFrom(PlayerAbilitiesManager mng){
        this.clientHotbar = mng.clientHotbar;
        this.quickAbility = mng.quickAbility;
        this.abilities = mng.abilities;
    }

    public void castArtifactAbility(UUID artifactKey, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(!target.level().isClientSide() && target instanceof Player player){
            PacketHandler.sendMessageSTC(new PlayerCastAbilityMessageCTS(artifactKey), player);
        }
        ArtifactHolder artifact = artifacts.get(artifactKey);
        artifact.castDefaultAbilities(cap, target);

    }

    public Ability getAbility(AbilityKey key){
        if(key.isArtifactKey()) return artifacts.get(key.getArtifactId()).getAbility(key);
        return abilities.get(key);
    }

    public List<Page> getPagesFromAbilities(){
        List<Page> result = new ArrayList<>();
        for(Ability abl: abilities.values()){
            if(abl instanceof MysticalKnowledgeAbility myst){
                result.addAll(myst.getPages());
            }
        }
        return result;
    }

    /**
     * this methods is the one that goes through the inventory and gets the list of artifacts to change.
     * happens about once a second on server side only.
     * @param cap
     * @param player
     */
    public void updateArtifacts(LivingEntityBeyonderCapability cap, Player player) {
        //because artifacts depend on NBT data, it makes no sense to try to update them on client side.
        //use messages to update the client.
        if(player.level().isClientSide()) return;

        //1 - create list of artifacts from inventory data
        //returns a map, connecting ablIds to artifactIds
        Map<UUID, ArtifactHolder> inventoryArtifacts = getArtifactsFromInventory(player);
        //2 - update artifacts list attribute if anything changed
        //add new artifacts to list
        for (Map.Entry<UUID, ArtifactHolder> entry: inventoryArtifacts.entrySet()) {
            UUID artifactKey = entry.getKey();
            ArtifactHolder artifact = entry.getValue();

            if (!artifacts.containsKey(artifactKey)) {
                addArtifact(artifact, cap, player, true, true);
            }
        }
        //remove artifacts from list
        for (UUID artifactId : new ArrayList<>(artifacts.keySet())) {
            if (!inventoryArtifacts.containsKey(artifactId)) {
                removeArtifact(artifactId, cap, player, true);
            }
        }

        //3 - update artifact data into the item tags.
        iterateThroughInventory(player, itemStack -> {
            UUID id = MysticalItemHelper.getArtifactIdFromItem(itemStack);
            if(id != null) MysticalItemHelper.updateArtifactTagOnItem(artifacts.get(id), itemStack);
        });
    }

    /**
     * This method should return a map, connectin artifact id to artifact instance.
     */
    private HashMap<UUID, ArtifactHolder> getArtifactsFromInventory(Player player) {
//        ItemStack validAmuletEnabled = ItemStack.EMPTY;
//        boolean tooManyAmulets = false;
        HashMap<UUID, ArtifactHolder> resMap = new HashMap<>();
        iterateThroughInventory(player, itemStack -> {
            ArtifactHolder artifact = MysticalItemHelper.getArtifactFromitem(itemStack);
            if(artifact != null)
                resMap.put(artifact.getArtifactId(), artifact);
        });
////        for(ItemStack itemStack: player.getInventory().items){
////            if(itemStack.is(ModItems.AMULET.get()) && MysticalItemHelper.isValidArtifact(itemStack)){
////                if(validAmuletEnabled.isEmpty()){
////                    validAmuletEnabled = itemStack;
////                } else {
////                    tooManyAmulets = true;
////                    NecklaceItem.enableAmulet(validAmuletEnabled, false);
////                    NecklaceItem.enableAmulet(itemStack, false);
////                }
////            }
////            ArtifactHolder artifact = MysticalItemHelper.getArtifactFromitem(itemStack);
////            resMap.put(artifact.getArtifactId(), artifact);
////           //addArtifact(artifact, cap, player, true);
////        }
////        if(ModList.get().isLoaded("curios")){
////            if(CuriosApi.getCuriosInventory(player).resolve().isPresent()){
////                ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
////                Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();
////                for(ICurioStacksHandler handler: curios.values()){
////                    int slots = handler.getSlots();
////                    for(int i = 0; i < slots; i++){
////                        ItemStack itemStack = handler.getStacks().getStackInSlot(i);
////                        if(itemStack.is(ModItems.AMULET.get()) && ArtifactHelper.isValidArtifact(itemStack)){
////                            if(validAmuletEnabled.isEmpty()){
////                                validAmuletEnabled = itemStack;
////                            } else {
////                                tooManyAmulets = true;
////                                NecklaceItem.enableAmulet(validAmuletEnabled, false);
////                                NecklaceItem.enableAmulet(itemStack, false);
////                            }
////                        }
////                        ArtifactHolder artifact = MysticalItemHelper.getArtifactFromitem(itemStack);
////                        resMap.put(artifact.getArtifactId(), artifact);
////                    }
////                }
////            }
////        }
////        if(!validAmuletEnabled.isEmpty() && !tooManyAmulets){
////            NecklaceItem.enableAmulet(validAmuletEnabled, true);
////        }
        return resMap;
    }

    private boolean addArtifact(ArtifactHolder artifact, LivingEntityBeyonderCapability cap, Player player, boolean runOnAcquire, boolean sync){
        if(artifact == null || artifact.isEmpty()) return false;
        if(artifacts.containsKey(artifact.getArtifactId())) return false;
        artifacts.put(artifact.getArtifactId(), artifact);
        if (runOnAcquire) artifact.onAcquire(cap, player);
        if(sync) updateClientArtifactInfo(player, List.of(artifact), PlayerArtifactSyncSTC.ADD);
        return true;
    }

    private boolean removeArtifact(UUID artifactId, LivingEntityBeyonderCapability cap, Player player, boolean sync){
        if(artifactId == null) return false;
        ArtifactHolder artifact = artifacts.remove(artifactId);
        artifact.onRemove(cap, player);
        if(sync) updateClientArtifactInfo(player, List.of(artifact), PlayerArtifactSyncSTC.REMOVE);
        return true;
    }

    /**
     * method that saves the artifact ability data to its respective item
     * @param artifactId
     * @param player
     */
    public void updateDataOnArtifact(UUID artifactId, Player player){
        if(artifactId == null || player.level().isClientSide()) return;
        for(ItemStack itemStack: player.getInventory().items){
            if(Objects.equals(MysticalItemHelper.getArtifactIdFromItem(itemStack), artifactId)){
                MysticalItemHelper.updateArtifactTagOnItem(artifacts.get(artifactId), itemStack);
                return;
            }
        }
        if(ModList.get().isLoaded("curios")){
            if(CuriosApi.getCuriosInventory(player).resolve().isPresent()){
                ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
                Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();
                for(ICurioStacksHandler handler: curios.values()){
                    int slots = handler.getSlots();
                    for(int i = 0; i < slots; i++){
                        ItemStack itemStack = handler.getStacks().getStackInSlot(i);
                        if(Objects.equals(MysticalItemHelper.getArtifactIdFromItem(itemStack), artifactId)){
                            MysticalItemHelper.updateArtifactTagOnItem(artifacts.get(artifactId), itemStack);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void iterateThroughInventory(Player player, Consumer<ItemStack> consumer){
        for(ItemStack itemStack: player.getInventory().items){
            consumer.accept(itemStack);
        }
        if(ModList.get().isLoaded("curios")){
            if(CuriosApi.getCuriosInventory(player).resolve().isPresent()){
                ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
                Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();
                for(ICurioStacksHandler handler: curios.values()){
                    int slots = handler.getSlots();
                    for(int i = 0; i < slots; i++){
                        ItemStack itemStack = handler.getStacks().getStackInSlot(i);
                        consumer.accept(itemStack);
                    }
                }
            }
        }
    }

    public void onTick(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(!abilities.isEmpty()){
            abilities.values().forEach(ability -> {
                ability.passive(cap, target);
                if(cap.getEffectsManager().hasEffect(BeyonderEffects.COGITATION.getEffectId())
                        && !ability.getAbilityId().equals(Abilities.COGITATION.getAblId())
                        && !ability.isRevoked()){
                    ability.revoke(cap, target);
                }
                ability.tickCooldown();
            });
        }
        artifacts.values().forEach(ability -> {
            ability.passives(cap, target);
        });

    }

    public void unrevokeAll(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(!abilities.isEmpty()){
            abilities.values().forEach(ability -> {
                if(ability.isRevoked()){
                    ability.undoRevoke(cap, target);
                }
            });
        }
    }

    public void clearAbilities(LivingEntityBeyonderCapability cap, LivingEntity target){
        abilities.values().forEach(ability -> ability.deactivate(cap, target));
        abilities = new LinkedHashMap<>();
    }

    public void clearArtifacts(LivingEntityBeyonderCapability cap, LivingEntity target){
        artifacts.values().forEach(ability -> ability.onRemove(cap, target));
        artifacts = new LinkedHashMap<>();
    }

    public void grantAbilities(List<Ability> newAbilities, int pathwaySequenceId, LivingEntityBeyonderCapability cap, LivingEntity target) {
        //first, upgrade all existing intrinsic abilities to max sequence level, regardless of their original pathway.
        upgradeAbilitiesToLevel(pathwaySequenceId%10, cap, target);
        //then grant new abilities
        for(Ability abl: newAbilities){
            //already checks if it exists
            addAbility(AbilityList.INTRINSIC.name(), abl, cap, target, true, false);
        }
        //then replace any cogitation abilities with the
        replaceCogitation(pathwaySequenceId, cap, target, true);

        //finally, update client info
        if(target instanceof Player player) updateClientAbilityInfo(player, AbilitySyncMessage.SET);
    }

    public void replaceCogitation(int pathwaySequenceId, LivingEntityBeyonderCapability cap, LivingEntity target, boolean runOnAcquire) {
        for(AbilityKey key: new ArrayList<>(abilities.keySet())){
            if(key.isSameAbility(Abilities.COGITATION.getAblId())){
                abilities.remove(key);
                break;
            }
        }
        addAbility(AbilityList.INTRINSIC.name(), Abilities.COGITATION.create(pathwaySequenceId), cap, target, runOnAcquire, false);
    }

    private void upgradeAbilitiesToLevel(int sequenceLevel, LivingEntityBeyonderCapability cap, LivingEntity target){
        for(Ability abl: new ArrayList<>(abilities.values())){
            if(abl.getSequenceLevel() > sequenceLevel && abl.getType().equals(AbilityList.INTRINSIC.name())){
                abilities.remove(abl.getKey());
                abl.upgradeToLevel(sequenceLevel, cap, target);
                AbilityKey newKey = abl.setAbilityKey(AbilityList.INTRINSIC.name());
                abilities.put(newKey, abl);
            }
        }
    }

    /**
     * sets the enabled state of the target ability to the given state.
     * this will apply to every ability at this sequence level or lower (that is, between that level and level 9)
     * @param abilityId
     * @param sequenceLevel
     * @param state
     * @param cap
     * @param target
     */
    public void setAbilityEnabled(String abilityId, int sequenceLevel, boolean state, LivingEntityBeyonderCapability cap, LivingEntity target) {
        for(Map.Entry<AbilityKey, Ability> abilityEntry: abilities.entrySet()){
            AbilityKey iKey = abilityEntry.getKey();
            if(iKey.isSameAbility(abilityId) && iKey.getSequenceLevel() >= sequenceLevel){
                abilityEntry.getValue().setEnabled(cap, target, state);
            }
        }
    }

    /**
     * function to put all abilities of this level or lower on cooldown.
     * it doesnt disable them (see: setAbilityEnabled) just puts them on cooldown
     * @param abilityId
     * @param sequenceLevel
     * @param cooldownTicks
     * @param target
     */
    public void putAbilityOnCooldown(String abilityId, int sequenceLevel, int cooldownTicks, LivingEntity target){
        for(Map.Entry<AbilityKey, Ability> abilityEntry: abilities.entrySet()){
            AbilityKey iKey = abilityEntry.getKey();
            if(iKey.isSameAbility(abilityId) && iKey.getSequenceLevel() >= sequenceLevel){
                abilityEntry.getValue().putOnCooldown(cooldownTicks, target);
            }
        }
    }

    public boolean isEnabledExactLevel(String abilityId, int sequenceLevel){
        for(Map.Entry<AbilityKey, Ability> abilityEntry: abilities.entrySet()){
            AbilityKey iKey = abilityEntry.getKey();
            if(iKey.isSameAbility(abilityId) && iKey.getSequenceLevel() == sequenceLevel){
                return abilityEntry.getValue().isEnabled();
            }
        }
        return false;
    }

    public boolean isEnabledAtLevelOrLower(String abilityId, int sequenceLevel) {
        for(Map.Entry<AbilityKey, Ability> abilityEntry: abilities.entrySet()){
            AbilityKey iKey = abilityEntry.getKey();
            if(iKey.isSameAbility(abilityId) && iKey.getSequenceLevel() >= sequenceLevel){
                return abilityEntry.getValue().isEnabled();
            }
        }
        return false;
    }

    /**
     * method to set the abilities on the client-side manager to match with server-side, based on the corresponding ability infos
     * @param abilities
     */
    public void setAbilitiesOnClient(List<AbilityInfo> abilities, LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!target.level().isClientSide()) return;
        clearAbilities(cap, target);
        addAbilitiesOnClient(abilities, cap, target, true);
        updateAbilitiesOnClient(abilities, cap, target);
    }

    public void setArtifactsOnClient(List<ArtifactHolder> artifacts, LivingEntityBeyonderCapability cap, Player player) {
        if(!player.level().isClientSide()) return;
        clearArtifacts(cap, player);
        addArtifactsOnClient(artifacts, cap, player, false);
    }

    public void addAbilitiesOnClient(List<AbilityInfo> abilities, @NotNull LivingEntityBeyonderCapability cap, LivingEntity target, boolean runOnAcquire) {
        if(!target.level().isClientSide()) return;
        for(AbilityInfo abl: abilities){
            AbilityKey key = abl.getKey();
            if(key == null){
                System.out.println("Warning: Read an ability with a null key: " + abl.descId());
                continue;
            }
            Ability ability = Abilities.getAbilityInstanceByKey(key);
            if(!addAbility(key, ability, cap, target, runOnAcquire, false)){
                System.out.println("Warning: Tried to add an ability that already exists on client: " + abl.getKey());
            }
        }
    }

    public void addArtifactsOnClient(List<ArtifactHolder> artifacts, @NotNull LivingEntityBeyonderCapability cap, Player player, boolean runOnAcquire) {
        if(!player.level().isClientSide()) return;
        for(ArtifactHolder artifact: artifacts){
            UUID artifactId = artifact.getArtifactId();
            if(artifactId == null){
                System.out.println("Warning: Read an artifact with a null key: " + artifact.toString());
                continue;
            }
            if(!addArtifact(artifact, cap, player, runOnAcquire, false)){
                System.out.println("Warning: Tried to add an artifact that already exists on client: " + artifact.getArtifactId());
            }
        }
    }

    public void removeAbilitiesOnClient(List<AbilityInfo> abilities, @NotNull LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!target.level().isClientSide()) return;
        for(AbilityInfo abl: abilities){
            AbilityKey key = abl.getKey();
            if(key == null){
                System.out.println("Warning: Read an ability with a null key: " + abl.descId());
                continue;
            }
            if(!removeAbility(key, cap, target, false)){
                System.out.println("Warning: Tried to remove an ability that doesnt exist on client: " + key);
            }
        }
    }

    public void removeArtifactsOnClient(List<ArtifactHolder> artifacts, @NotNull LivingEntityBeyonderCapability cap, Player player) {
        if(!player.level().isClientSide()) return;
        for(ArtifactHolder artifact: artifacts){
            UUID uuid = artifact.getArtifactId();
            if(uuid == null){
                System.out.println("Warning: Read an artifact with a null key: " + artifact);
                continue;
            }
            if(!removeArtifact(uuid, cap, player, false)){
                System.out.println("Warning: Tried to remove an artifact that doesnt exist on client: " + artifact.getArtifactId());
            }
        }
    }

    public void updateAbilitiesOnClient(List<AbilityInfo> abilities2, @NotNull LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!target.level().isClientSide()) return;
        for(AbilityInfo info: abilities2){
            AbilityKey key = info.getKey();
            if(key == null){
                System.out.println("Warning: Read an ability with a null key: " + info.descId());
                continue;
            }
            if(key.isArtifactKey() && artifacts.containsKey(key.getArtifactId())){
                artifacts.get(key.getArtifactId()).getAbility(key).receiveUpdateOnClient(info, cap, target);
                continue;
            }
            if(!this.abilities.containsKey(key)){
                //System.out.println("Warning: Tried to update an ability with a non existent key: " + key);
                continue;
            }

            Ability abl = this.abilities.get(key);
            abl.receiveUpdateOnClient(info, cap, target);
        }
    }

//    public void updateArtifactsOnClient(List<ArtifactHolder> artifacts,  @NotNull LivingEntityBeyonderCapability cap, Player player) {
//        if(!player.level().isClientSide()) return;
//        for(ArtifactHolder artifact: artifacts){
//            UUID uuid = artifact.getArtifactId();
//            if(uuid == null){
//                System.out.println("Warning: Read an artifact with a null key: " + artifact);
//                continue;
//            }
//            if(!this.artifacts.containsKey(uuid)){
//                //System.out.println("Warning: Tried to update an ability with a non existent key: " + key);
//                continue;
//            }
//            this.artifacts.get(uuid).updateOnClient(artifact);
//        }
//    }


    public enum AbilityList{
        INTRINSIC,
        RECORDED,
        REPLICATED,
        ARTIFACT,
    }

    public boolean addAbility(String abilityGroup, Ability ability, LivingEntityBeyonderCapability cap, LivingEntity target, boolean runOnAcquire, boolean sync){
        return addAbility(new AbilityKey(abilityGroup, ability.getAbilityId(), ability.getSequenceLevel()), ability, cap, target, runOnAcquire, sync);
    }

    public boolean addAbility(AbilityKey key, Ability ability, LivingEntityBeyonderCapability cap, LivingEntity target, boolean runOnAcquire, boolean sync){
        if(abilities.containsKey(key)) return false;
        ability.setAbilityKey(key.getGroup());
        abilities.put(key, ability);
        if (runOnAcquire) ability.onAcquire(cap, target);
        if(sync && target instanceof Player player) updateClientAbilityInfo(player, List.of(ability.getAbilityInfo()), AbilitySyncMessage.ADD);
        return true;
    }

    public boolean removeAbility(AbilityKey key, LivingEntityBeyonderCapability cap, LivingEntity target, boolean sync){
        if(key.getGroup().equals(AbilityList.INTRINSIC.name())) return false;
        if(!abilities.containsKey(key)) return false;
        Ability abl = abilities.get(key);
        abl.deactivate(cap, target);
        abilities.remove(key);
        if(sync && target instanceof Player player) updateClientAbilityInfo(player, List.of(abl.getAbilityInfo()), AbilitySyncMessage.REMOVE);
        return true;
    }

    public void useAbility(LivingEntityBeyonderCapability cap, LivingEntity tar, AbilityKey key, boolean sync, boolean primary){
        if(key.isArtifactKey()){
            ArtifactHolder artifact = artifacts.get(key.getArtifactId());
            if(artifact != null){
                artifact.castAbility(key, primary, cap, tar);
            }
            if(sync && tar.level().isClientSide()){
                PacketHandler.sendMessageCTS(new PlayerCastAbilityMessageCTS(key, primary));
            }
        } else {
            Ability ability = abilities.get(key);
            if(ability != null){
                ability.castAbility(cap, tar, primary);
                if(sync && tar.level().isClientSide()){
                    PacketHandler.sendMessageCTS(new PlayerCastAbilityMessageCTS(key, primary));
                }
            }
        }
//        if(ability != null && cap.getSpirituality() >= Abilities.getAbilityById(key.getAbilityId()).getCostSpirituality()){
    }

    public void setEnabledAtLevel(String ablId, int sequenceLevel, boolean enabling, LivingEntityBeyonderCapability cap, LivingEntity target){
        for(Map.Entry<AbilityKey, Ability> entry: abilities.entrySet()){
            AbilityKey iKey = entry.getKey();
            if(iKey.isSameAbility(ablId) && iKey.getSequenceLevel() == sequenceLevel){
                entry.getValue().setEnabled(cap, target, enabling);
            }
        }
    }

    public void setEnabledAtLevelOrLower(String ablId, int sequenceLevel, boolean enabling, LivingEntityBeyonderCapability cap, LivingEntity target){
        for(Map.Entry<AbilityKey, Ability> entry: abilities.entrySet()){
            AbilityKey iKey = entry.getKey();
            if(iKey.isSameAbility(ablId) && iKey.getSequenceLevel() >= sequenceLevel){
                entry.getValue().setEnabled(cap, target, enabling);
            }
        }
    }

    /**
     *
     * @param key
     * @param enabling
     * @param cap
     * @param target
     * @return the new enabled state for that ability
     */
    public boolean setEnabled(AbilityKey key, boolean enabling, LivingEntityBeyonderCapability cap, LivingEntity target){
        //cAblId example: Artifact:23:water_affinity:9
        // Intrinsic:water_affinity:8
        // artifact abilities point to the specific artifact, intrinsic and other abilities just point to that ability.
        //if the argument cAblId is incomplete ("water_affinity:8" or just "water_affinity") we must apply this enableDisable to every such ability
        //      of that sequence level or lower.
        return abilities.get(key).setEnabled(cap, target, enabling);
    }

    public void updateClientAbilityInfo(Player player, List<AbilityInfo> abilities, int operation){
        if(player.level().isClientSide()) return;
        PacketHandler.sendMessageSTC(new AbilitySyncMessage(abilities, operation), player);
    }

    public void updateClientAbilityInfo(Player player, int operation){
        if(player.level().isClientSide()) return;
        PacketHandler.sendMessageSTC(new AbilitySyncMessage(getAbilityInfos(), operation), player);
    }

    public void updateClientArtifactInfo(Player player, List<ArtifactHolder> artifacts, int operation) {
        if(player.level().isClientSide()) return;
        PacketHandler.sendMessageSTC(new PlayerArtifactSyncSTC(artifacts, operation), player);
    }

    public void updateClientArtifactInfo(Player player, int operation) {
        if(player.level().isClientSide()) return;
        PacketHandler.sendMessageSTC(new PlayerArtifactSyncSTC(artifacts.values().stream().toList(), operation), player);
    }

    private List<AbilityInfo> getAbilityInfos() {
        List<AbilityInfo> res = new ArrayList<>();
        for(AbilityKey key: abilities.keySet()){
            res.add(abilities.get(key).getAbilityInfo());
        }
        return res;
    }

    public void saveNBTData(CompoundTag nbt){
        CompoundTag hotbar = new CompoundTag();
        hotbar.putInt("size", clientHotbar.size());
        for(int j = 0; j < clientHotbar.size(); j++){
            hotbar.putString(String.valueOf(j), clientHotbar.get(j).toString());
        }
        hotbar.putString("quick", quickAbility.toString());
        nbt.put("hotbar", hotbar);
        for(Ability abl: abilities.values()){
            nbt.put(abl.getKey().toString(), abl.saveNbt());
        }
        ListTag artifactsTag = new ListTag();
        for(ArtifactHolder artifact: artifacts.values()){
            artifactsTag.add(artifact.saveToTag(new CompoundTag()));
        }
        nbt.put("artifacts", artifactsTag);
    }

    private List<Ability> bufferNewAbilities = new ArrayList<>();
    private List<String> bufferAbilityGroups = new ArrayList<>();

    public void bufferAddAbility(String group, Ability abl){
        bufferNewAbilities.add(abl);
        bufferAbilityGroups.add(group);
    }

    public void loadNBTData(CompoundTag nbt, LivingEntityBeyonderCapability cap, LivingEntity target){
        for(Ability abl: abilities.values()){
            abl.loadNbt(nbt);
        }

        if(!(target instanceof Player player)) return;

        CompoundTag hot = nbt.getCompound("hotbar");
        int sizeHot = hot.getInt("size");
        if(sizeHot != 0){
            for(int i = 0; i < sizeHot; i++){
                AbilityKey key = AbilityKey.fromString(hot.getString(String.valueOf(i)));
                if(key.isEmpty()){
                    System.out.println("Read invalid ability key from NBT data: " + hot.getString(String.valueOf(i)));
                    continue;
                }
                clientHotbar.add(key);
            }
        }
        quickAbility = AbilityKey.fromString(hot.getString("quick"));
        for(int i = 0; i < bufferAbilityGroups.size(); i++){
            Ability abl = bufferNewAbilities.get(i);
            addAbility(bufferAbilityGroups.get(i), abl, cap, target, false, false);
            abl.loadNbt(nbt);
        }
        bufferNewAbilities.clear();

        ListTag artifactTag = nbt.getList("artifacts", ListTag.TAG_COMPOUND);
        for(Tag tag: artifactTag){
            if(tag instanceof CompoundTag artTag){
                addArtifact(ArtifactHolder.loadFromTag(artTag), cap, player, false, false);
            }
        }
    }

}
