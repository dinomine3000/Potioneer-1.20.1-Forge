package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.AbilitySyncMessage;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerCastAbilityMessageCTS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlayerAbilitiesManager {
    private LinkedHashMap<AbilityKey, Ability> abilities = new LinkedHashMap<>();
    //private ArrayList<String> replicatedAbilities = new ArrayList<>();
    //private ArrayList<String> recordedAbilities = new ArrayList<>();
    //private ArrayList<ArtifactHolder> artifacts = new ArrayList<>();

    public ArrayList<AbilityKey> clientHotbar = new ArrayList<>();
    public AbilityKey quickAbility = new AbilityKey();

    public void copyFrom(PlayerAbilitiesManager mng){
        this.clientHotbar = mng.clientHotbar;
        this.quickAbility = mng.quickAbility;
        this.abilities = mng.abilities;
    }

    public Ability getAbility(AbilityKey key){
        return abilities.get(key);
    }

//    public void castArtifactAbilityAll(LivingEntityBeyonderCapability cap, Player player, String artifactId){
//        if(artifactIds == null || artifactIds.isEmpty()) return;
//        for(String artifactId: artifactIds){
//            String id = artifactId.substring(3);
//            if(artifacts.containsKey(id)
//                    || pathwayAbilities.containsKey(id)){
//                useAbility(cap, player, id, true, true);
//            }
//        }
//    }
//
//    /**
//     * this method is only used for adding artifacts.
//     * the logic of whether this specific ID should be added is not handled here
//     * @param cap
//     * @param player
//     * @param ablId
//     * @param sequence
//     */
//    public void updateAddArtifact(LivingEntityBeyonderCapability cap, Player player, String ablId, int sequence, boolean sync){
//        Ability abl = ArtifactHelper.getAbilityFromId(ablId, sequence);
//        if(abl == null) return;
//        if(!artifacts.containsKey(ablId)){
//            System.out.println("Adding new artifact: " + ablId + " sequence " + sequence);
//            enabledDisabled.putIfAbsent(ablId, true);
//            activeCooldowns.putIfAbsent(ablId, 0);
//        } else {
//            System.out.println("Changing existing artifact instance to " + ablId + sequence);
//            artifacts.get(ablId).deactivate(cap, player);
//        }
//        abl.onAcquire(cap, player);
//        artifacts.put(ablId, abl);
//
//        if(sync){
//            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
//                    new PlayerArtifactSyncSTC(ablId, sequence, true));
//        }
//    }
//
//    public void updateRemoveArtifact(LivingEntityBeyonderCapability cap, Player player, String ablId, boolean sync){
//        if(artifacts.containsKey(ablId)){
//            System.out.println("Removing artifact: " + ablId);
//            artifacts.get(ablId).deactivate(cap, player);
//            artifacts.remove(ablId);
//            if(sync)
//                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
//                        new PlayerArtifactSyncSTC(ablId, 0, false));
//        } else System.out.println("Tried to remove a non existent artifact ability");
//
//    }
//
//    /**
//     * this methods is the one that goes through the inventory and gets the list of artifacts to change.
//     * happens about once a second on server side only.
//     * @param cap
//     * @param player
//     */
//    public void updateArtifacts(LivingEntityBeyonderCapability cap, Player player) {
//        for (String ablId : new ArrayList<>(activeCooldowns.keySet())) {
//            if(activeCooldowns.get(ablId) == 0
//                    && !artifacts.containsKey(ablId)
//                    && !pathwayAbilities.containsKey(ablId)){
//                System.out.println("Removing cooldown and enabled info for ability: " + ablId);
//                activeCooldowns.remove(ablId);
//                enabledDisabled.remove(ablId);
//            }
//        }
//        //because artifacts depend on NBT data, it makes no sense to try to update them on client side.
//        //use messages to update the client.
//        if(player.level().isClientSide()) return;
//
//        //1 - create list of the best artifact abilities from inventory data
//        //returns a map, connecting ablIds to artifactIds
//        Map<String, String> inventoryArtifacts = getArtifactsFromInventory(player);
//        //2 - update artifacts list attribute if anything changed
//        //add new artifacts to list
//        for (String artifactId : inventoryArtifacts.values()) {
//            String ablId = artifactId.substring(3);
//            int newLevel = artifactId.charAt(1) - '0';
//
//            if (shouldAddArtifact(ablId, newLevel)) {
//                updateAddArtifact(cap, player, ablId, newLevel, true);
//            }
//        }
//        //remove artifacts from list
//        for (String ablId : new ArrayList<>(artifacts.keySet())) {
//            if (!inventoryArtifacts.containsKey(ablId)) {
//                updateRemoveArtifact(cap, player, ablId, true);
//            }
//            //when you advance, removing newly-turned-useless artifact abilities is dealt with in the set abilites method
//        }
//
//    }
//
//    /**
//     * This method should return a map, connectin AblID to ArtifactID.
//     * Returns the best abilities found in the inventory
//     */
//    private Map<String, String> getArtifactsFromInventory(Player player) {
//        HashMap<String, String> resMap = new HashMap<>();
//        ItemStack validAmuletEnabled = ItemStack.EMPTY;
//        boolean tooManyAmulets = false;
//        for(ItemStack itemStack: player.getInventory().items){
//            if(itemStack.is(ModItems.AMULET.get()) && ArtifactHelper.isValidArtifact(itemStack)){
//                if(validAmuletEnabled.isEmpty()){
//                    validAmuletEnabled = itemStack;
//                } else {
//                    tooManyAmulets = true;
//                    NecklaceItem.enableAmulet(validAmuletEnabled, false);
//                    NecklaceItem.enableAmulet(itemStack, false);
//                }
//            }
//            List<String> ablDown = ArtifactHelper.getArtifactIdsFromItem(itemStack);
//            for(String artifactId: ablDown){
//                addAbilityToMap(artifactId, resMap);
//            }
//        }
//        if(ModList.get().isLoaded("curios")){
//            if(CuriosApi.getCuriosInventory(player).resolve().isPresent()){
//                ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
//                Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();
//                for(ICurioStacksHandler handler: curios.values()){
//                    int slots = handler.getSlots();
//                    for(int i = 0; i < slots; i++){
//                        ItemStack itemStack = handler.getStacks().getStackInSlot(i);
//                        if(itemStack.is(ModItems.AMULET.get()) && ArtifactHelper.isValidArtifact(itemStack)){
//                            if(validAmuletEnabled.isEmpty()){
//                                validAmuletEnabled = itemStack;
//                            } else {
//                                tooManyAmulets = true;
//                                NecklaceItem.enableAmulet(validAmuletEnabled, false);
//                                NecklaceItem.enableAmulet(itemStack, false);
//                            }
//                        }
//                        List<String> ablDown = ArtifactHelper.getArtifactIdsFromItem(itemStack);
//                        for(String artifactId: ablDown){
//                            addAbilityToMap(artifactId, resMap);
//                        }
//                    }
//                }
//            }
//        }
//        if(!validAmuletEnabled.isEmpty() && !tooManyAmulets){
//            NecklaceItem.enableAmulet(validAmuletEnabled, true);
//        }
//        return resMap;
//    }

//    private void addAbilityToMap(String id, Map<String, String> map){
//        if(map.containsKey(id.substring(3))){
//            int ablLevel = id.charAt(1) - '0';
//            int prevLevel = map.get(id.substring(3)).charAt(1) - '0';
//            if(ablLevel < prevLevel){
//                map.put(id.substring(3), id);
//            }
//        } else {
//            map.put(id.substring(3), id);
//        }
//    }
//
//    private boolean shouldAddArtifact(String ablId, int sequenceLevel){
//        //this method assumes the passed ability is the best found in the players inventory
//        boolean isIntrinsic = pathwayAbilities.containsKey(ablId);
//        boolean isStrongerThanIntrinsic = !isIntrinsic
//                || pathwayAbilities.get(ablId).getSequence() > sequenceLevel;
//        boolean shouldReplaceInArtifacts = !artifacts.containsKey(ablId)
//                || artifacts.get(ablId).getSequence() != sequenceLevel;
//        return shouldReplaceInArtifacts && isStrongerThanIntrinsic;
//    }

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
//        if(!artifacts.isEmpty()){
//            artifacts.values().forEach(ability -> {
//                ability.passive(cap, target);
//            });
//        }

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

    public void clear(LivingEntityBeyonderCapability cap, LivingEntity target){
        abilities.values().forEach(ability -> ability.deactivate(cap, target));
        abilities = new LinkedHashMap<>();
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
        replaceCogitation(pathwaySequenceId, cap, target);

        //finally, update client info
        if(target instanceof Player player) updateClientAbilityInfo(player);
    }

    private void replaceCogitation(int pathwaySequenceId, LivingEntityBeyonderCapability cap, LivingEntity target) {
        for(AbilityKey key: abilities.keySet()){
            if(key.isSameAbility(Abilities.COGITATION.getAblId())){
                abilities.get(key).upgradeToLevel(pathwaySequenceId, cap, target);
                return;
            }
        }
        addAbility(AbilityList.INTRINSIC.name(), Abilities.COGITATION.create(pathwaySequenceId), cap, target, true, false);
    }

    private void upgradeAbilitiesToLevel(int sequenceLevel, LivingEntityBeyonderCapability cap, LivingEntity target){
        for(Ability abl: abilities.values()){
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
        clear(cap, target);
        addAbilitiesOnClient(abilities, cap, target, true);
    }


    public void addAbilitiesOnClient(List<AbilityInfo> abilities, @NotNull LivingEntityBeyonderCapability cap, LivingEntity target, boolean runOnAcquire) {
        if(!target.level().isClientSide()) return;
        for(AbilityInfo abl: abilities){
            AbilityKey key = abl.getKey();
            if(key == null){
                System.out.println("Warning: Read an ability with a null key: " + abl.descId());
                continue;
            }
            Ability ability = Abilities.getAbilityByKey(key);
            if(!addAbility(key, ability, cap, target, runOnAcquire, false)){
                System.out.println("Warning: Tried to add an ability that already exists on client: " + abl.getKey());
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

    public void updateAbilitiesOnClient(List<AbilityInfo> abilities2, @NotNull LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!target.level().isClientSide()) return;
        for(AbilityInfo info: abilities2){
            AbilityKey key = info.getKey();
            if(key == null){
                System.out.println("Warning: Read an ability with a null key: " + info.descId());
                continue;
            }
            if(!this.abilities.containsKey(key)){
                System.out.println("Warning: Tried to update an ability with a non existent key: " + key);
                continue;
            }

            Ability abl = this.abilities.get(key);
            if(abl.isEnabled() != info.isEnabled()){
                abl.setEnabled(cap, target, info.isEnabled());
            }
            abl.putOnCooldown(info.getCooldown(), target);
        }
    }

    public enum AbilityList{
        INTRINSIC,
        RECORDED,
        REPLICATED
    }

    public boolean addAbility(String abilityGroup, Ability ability, LivingEntityBeyonderCapability cap, LivingEntity target, boolean runOnAcquire, boolean sync){
        return addAbility(new AbilityKey(abilityGroup, ability.getAbilityId(), ability.getSequenceLevel()), ability, cap, target, runOnAcquire, sync);
    }

    public boolean addAbility(AbilityKey key, Ability ability, LivingEntityBeyonderCapability cap, LivingEntity target, boolean runOnAcquire, boolean sync){
        if(abilities.containsKey(key)) return false;
        ability.setAbilityKey(key.getGroup());
        abilities.put(key, ability);
        if (runOnAcquire) ability.onAcquire(cap, target);
        if(sync && target instanceof Player player) updateClientAbilityInfo(player);
        return true;
    }

    public boolean removeAbility(AbilityKey key, LivingEntityBeyonderCapability cap, LivingEntity target, boolean sync){
        if(key.getGroup().equals(AbilityList.INTRINSIC.name())) return false;
        if(!abilities.containsKey(key)) return false;
        abilities.get(key).deactivate(cap, target);
        abilities.remove(key);
        if(sync && target instanceof Player player) updateClientAbilityInfo(player);
        return true;
    }

    public void useAbility(LivingEntityBeyonderCapability cap, LivingEntity tar, AbilityKey key, boolean sync, boolean primary){
        Ability ability = abilities.get(key);
        System.out.println(key);
        System.out.println(abilities);
        System.out.println("On client side? " + tar.level().isClientSide());
        if(ability != null){
            System.out.println("Found ability.");
            ability.castAbility(cap, tar, primary);
            if(sync && tar.level().isClientSide()){
                PacketHandler.sendMessageCTS(new PlayerCastAbilityMessageCTS(key, primary));
            }
        }
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

    public void updateClientAbilityInfo(Player player){
        PacketHandler.sendMessageSTC(new AbilitySyncMessage(getAbilityInfos(), AbilitySyncMessage.SET), player);
    }

//    public void setPathwayAbilities(ArrayList<Ability> abilities){
////        System.out.println("Active abilities updates. Size is: " + abilities.size());
//        if(abilities == null) return;
////        this.abilities = new LinkedHashMap<>();
////        for (Ability ability : abilities) {
////            String ablId = ability.getInfo().ablId();
////            this.abilities.put(ablId, ability);
////        }
//
//    }

//
//    public void onAcquireAbilities(LivingEntityBeyonderCapability cap, LivingEntity target){
//        //TODO: only going through actives. if you want for passives, youll need to add it
//        // Check if its enabled for when the player loads into the world -> dont activate extended reach if the ability was previously disabled
//        //its loading the enabledDisabled list before calling this onAcquire function when loading data
//        for (Ability ability : abilities.values()) {
//            if (enabledDisabled.get(ability.getInfo().ablId())) ability.onAcquire(cap, target);
//        }
//    }

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
    }

    private List<Ability> bufferNewAbilities = new ArrayList<>();
    private List<String> bufferAbilityGroups = new ArrayList<>();

    public void bufferAddAbility(String group, Ability abl){
        bufferNewAbilities.add(abl);
        bufferAbilityGroups.add(group);
    }

    public void loadNBTData(CompoundTag nbt, LivingEntityBeyonderCapability cap, LivingEntity target){
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
        for(Ability abl: abilities.values()){
            abl.loadNbt(nbt);
        }

        for(int i = 0; i < bufferAbilityGroups.size(); i++){
            Ability abl = bufferNewAbilities.get(i);
            addAbility(bufferAbilityGroups.get(i), abl, cap, target, false, false);
            abl.loadNbt(nbt);
        }
        bufferNewAbilities.clear();
    }

}
