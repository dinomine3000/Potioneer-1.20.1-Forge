package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerAbilityCooldownSTC;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerCastAbilityMessageCTS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerAbilitiesManager {
    private LinkedHashMap<String, Ability> abilities = new LinkedHashMap<>();
    //private ArrayList<String> replicatedAbilities = new ArrayList<>();
    //private ArrayList<String> recordedAbilities = new ArrayList<>();
    //private ArrayList<ArtifactHolder> artifacts = new ArrayList<>();

    public ArrayList<String> clientHotbar = new ArrayList<>();
    public String quickAbility = "";

    public void copyFrom(PlayerAbilitiesManager mng){
        this.clientHotbar = mng.clientHotbar;
        this.quickAbility = mng.quickAbility;
        this.abilities = mng.abilities;
    }

    public Ability getAbility(String cAblId){
        return abilities.get(cAblId);
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
                ability.tickCooldown();
            });
        }
//        if(!artifacts.isEmpty()){
//            artifacts.values().forEach(ability -> {
//                ability.passive(cap, target);
//            });
//        }

    }

    public void clear(LivingEntityBeyonderCapability cap, LivingEntity target){
        abilities.values().forEach(ability -> ability.deactivate(cap, target));
        abilities = new LinkedHashMap<>();
    }

    /**
     * function that returns client relevant information of every ability they possess.
     * used to sync their ability info.
     * @return
     */
    public Map<String, AbilityInfo> getAbilityInfo() {
        return null;
    }

    public void grantAbilities(List<Ability> newAbilities, int pathwaySequenceId, LivingEntityBeyonderCapability cap, LivingEntity target) {
        //first, upgrade all existing intrinsic abilities to max sequence level, regardless of their original pathway.
        upgradeAbilitiesToLevel(pathwaySequenceId%10, cap, target);
        //then grant new abilities
        for(Ability abl: newAbilities){
            //already checks if it exists
            addAbility(AbilityList.INTRINSIC, abl, cap, target, true);
        }
        //then replace any cogitation abilities with the
        replaceCogitation(pathwaySequenceId, cap, target);
    }

    private void replaceCogitation(int pathwaySequenceId, LivingEntityBeyonderCapability cap, LivingEntity target) {
        for(String key: abilities.keySet()){
            if(key.contains(Abilities.COGITATION.getAblId())){
                abilities.get(key).upgradeToLevel(pathwaySequenceId, cap, target);
                return;
            }
        }
    }

    private void upgradeAbilitiesToLevel(int sequenceLevel, LivingEntityBeyonderCapability cap, LivingEntity target){
        for(Ability abl: abilities.values()){
            if(abl.getSequenceLevel() > sequenceLevel && abl.getType().equals(AbilityList.INTRINSIC.name())){
                abilities.remove(abl.cAbilityId);
                abl.upgradeToLevel(sequenceLevel, cap, target);
                String cAblId = AbilityList.INTRINSIC.name().concat(":" + abl.getOuterId());
                abilities.put(cAblId, abl);
                abl.cAbilityId = cAblId;
            }
        }
    }

    public void setAbilityEnabled(String innerId, int sequenceLevel, boolean state, LivingEntityBeyonderCapability cap, LivingEntity target) {
        for(Map.Entry<String, Ability> abilityEntry: abilities.entrySet()){
            if(!abilityEntry.getKey().contains(innerId)) continue;
            int i = sequenceLevel;
            while(i <= 9){
                if(abilityEntry.getKey().contains(innerId.concat(":" + i))){
                    abilityEntry.getValue().setEnabled(cap, target, state);
                    break;
                }
                i++;
            }
        }
    }

    /**
     * function to put all abilities of this level or lower on cooldown.
     * it doesnt disable them (see: setAbilityEnabled) just puts them on cooldown
     * @param innerId
     * @param sequenceLevel
     * @param cooldownTicks
     * @param target
     */
    public void putAbilityOnCooldown(String innerId, int sequenceLevel, int cooldownTicks, LivingEntity target){
        for(Map.Entry<String, Ability> abilityEntry: abilities.entrySet()){
            if(!abilityEntry.getKey().contains(innerId)) continue;
            int i = sequenceLevel;
            while(i <= 9){
                if(abilityEntry.getKey().contains(innerId.concat(":" + i))){
                    abilityEntry.getValue().putOnCooldown(cooldownTicks, target);
                    break;
                }
                i++;
            }
        }
    }

    public boolean isEnabled(String innerId, int sequenceLevel) {
        for(Map.Entry<String, Ability> abilityEntry: abilities.entrySet()){
            if(!abilityEntry.getKey().contains(innerId)) continue;
            int i = sequenceLevel;
            while(i <= 9){
                if(abilityEntry.getKey().contains(innerId.concat(":" + i))){
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    public enum AbilityList{
        INTRINSIC,
        ARTIFACT_ABILITY,
        RECORDED,
        REPLICATED
    }
    public boolean addAbility(AbilityList abilityType, Ability ability, LivingEntityBeyonderCapability cap, LivingEntity target, boolean runOnAcquire){
        String cAbilityId = abilityType.name()
                .concat(":" + ability.getOuterId());
        if(abilities.containsKey(cAbilityId)) return false;
        ability.setCompleteId(cAbilityId);
        abilities.put(cAbilityId, ability);
        if (runOnAcquire) ability.onAcquire(cap, target);
        return true;
    }

    public boolean removeAbility(AbilityList abilityType, Ability ability, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(abilityType == AbilityList.INTRINSIC || abilityType == AbilityList.ARTIFACT_ABILITY) return false;
        String cAbilityId = abilityType.name()
                .concat(":" + ability.getOuterId());
        if(!abilities.containsKey(cAbilityId)) return false;
        abilities.get(cAbilityId).deactivate(cap, target);
        abilities.remove(cAbilityId);
        return true;
    }

    public void useAbility(LivingEntityBeyonderCapability cap, LivingEntity tar, String cAblId, boolean sync, boolean primary){
        Ability ability = abilities.get(cAblId);
        if(ability != null){
            ability.castAbility(cap, tar, primary);
            if(sync){
                if(tar.level().isClientSide()){
                    PacketHandler.sendMessageCTS(new PlayerCastAbilityMessageCTS(cAblId));
                }
            }
        }
    }

    public boolean setEnabled(String cAblId, boolean enabling, LivingEntityBeyonderCapability cap, LivingEntity target){
        //cAblId example: Artifact:23:water_affinity:9
        // Intrinsic:water_affinity:8
        // artifact abilities point to the specific artifact, intrinsic and other abilities just point to that ability.
        //if the argument cAblId is incomplete ("water_affinity:8" or just "water_affinity") we must apply this enableDisable to every such ability
        //      of that sequence level or lower.
        String[] identifiers = cAblId.split(":");
        switch(identifiers.length){
            case 3:
                return abilities.get(cAblId).setEnabled(cap, target, enabling);
            case 4:
                break;
            default:
                System.out.println("Warning: Not implemented enable/disable abilities without a cAblId.");
        }
        return false;
    }

    public List<String> disabledALlAbilities(Player player, String abilityToIgnore){
        //TODO fix what happens when abilityies are unrevoked during cogitation
        //ie when a tribunal revokes abilities, then the targets use cogitation and the original ability is unrevoked during that effect
        ArrayList<String> res = new ArrayList<>();
        for(String ablId: abilities.keySet()){
            if(ablId.contains(abilityToIgnore)) continue;

            Ability ability = abilities.get(ablId);
            ability.revoke();
            res.add(ablId);
        }
        return res;
    }

    public void reactivateAbilities(Player player, List<String> deactivatedAbilities) {
        for(String ablID: deactivatedAbilities){
            abilities.get(ablID).undoRevoke(player);
        }
    }

    public void updateClientCooldownInfo(ServerPlayer player){
        for (Ability abl : abilities.values()) {
            abl.updateCooldownClient(player);
        }
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

    public LinkedHashMap<String, AbilityInfo> getAbilityInfos() {
        LinkedHashMap<String, AbilityInfo> res = new LinkedHashMap<>();
        for(String ablId: abilities.keySet()){
            res.put(ablId, abilities.get(ablId).getAbilityInfo());
        }
        return res;
    }

    public void saveNBTData(CompoundTag nbt){
        CompoundTag hotbar = new CompoundTag();
        hotbar.putInt("size", clientHotbar.size());
        for(int j = 0; j < clientHotbar.size(); j++){
            hotbar.putString(String.valueOf(j), clientHotbar.get(j));
        }
        hotbar.putString("quick", quickAbility);
        nbt.put("hotbar", hotbar);
        for(Ability abl: abilities.values()){
            nbt.put(abl.cAbilityId, abl.saveNbt());
        }
    }

    private List<Ability> bufferNewAbilities = new ArrayList<>();
    private List<AbilityList> bufferAbilityTypes = new ArrayList<>();

    public void bufferAddAbility(AbilityList type, Ability abl){
        bufferNewAbilities.add(abl);
        bufferAbilityTypes.add(type);
    }

    public void loadNBTData(CompoundTag nbt, LivingEntityBeyonderCapability cap, LivingEntity target){
        CompoundTag hot = nbt.getCompound("hotbar");
        int sizeHot = hot.getInt("size");
        if(sizeHot != 0){
            for(int i = 0; i < sizeHot; i++){
                clientHotbar.add(hot.getString(String.valueOf(i)));
            }
        }
        quickAbility = hot.getString("quick");
        for(Ability abl: abilities.values()){
            abl.loadNbt(nbt);
        }
        for(int i = 0; i < bufferAbilityTypes.size(); i++){
            Ability abl = bufferNewAbilities.get(i);
            addAbility(bufferAbilityTypes.get(i), abl, cap, target, false);
            abl.loadNbt(nbt);
        }
        bufferNewAbilities.clear();
    }

}
