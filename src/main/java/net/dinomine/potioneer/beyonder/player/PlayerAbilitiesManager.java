package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.item.custom.NecklaceItem;
import net.dinomine.potioneer.util.misc.ArtifactHelper;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAbilityCooldownSTC;
import net.dinomine.potioneer.network.messages.PlayerArtifactSyncSTC;
import net.dinomine.potioneer.network.messages.PlayerCastAbilityMessageCTS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.*;

public class PlayerAbilitiesManager {
    private Map<String, Ability> pathwayActives = new HashMap<>();
    public Map<String, Boolean> enabledDisabled = new HashMap<>();
    private Map<String, Integer> activeCooldowns = new HashMap<>();
    private Map<String, Ability> artifactActives = new HashMap<>();

    public ArrayList<String> clientHotbar = new ArrayList<>();
    public String quickAbility = "";

    public void copyFrom(PlayerAbilitiesManager mng){
        this.enabledDisabled = new HashMap<>(mng.enabledDisabled);
        this.clientHotbar = mng.clientHotbar;
        this.quickAbility = mng.quickAbility;
    }

    public void useArtifactAbililty(LivingEntityBeyonderCapability cap, Player player, List<String> artifactIds){
        if(artifactIds == null || artifactIds.isEmpty()) return;
        for(String artifactId: artifactIds){
            String id = artifactId.substring(3);
            if(artifactActives.containsKey(id)
                    || pathwayActives.containsKey(id)){
                useAbility(cap, player, id, true, true);
            }
        }
    }

    /**
     * this method is only used for adding artifacts.
     * the logic of whether this specific ID should be added is not handled here
     * @param cap
     * @param player
     * @param ablId
     * @param sequence
     */
    public void updateAddArtifact(LivingEntityBeyonderCapability cap, Player player, String ablId, int sequence, boolean sync){
        Ability abl = ArtifactHelper.getAbilityFromId(ablId, sequence);
        if(abl == null) return;
        if(!artifactActives.containsKey(ablId)){
            System.out.println("Adding new artifact: " + ablId + " sequence " + sequence);
            enabledDisabled.putIfAbsent(ablId, true);
            activeCooldowns.putIfAbsent(ablId, 0);
        } else {
            System.out.println("Changing existing artifact instance to " + ablId + sequence);
            artifactActives.get(ablId).deactivate(cap, player);
        }
        abl.onAcquire(cap, player);
        artifactActives.put(ablId, abl);

        if(sync){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerArtifactSyncSTC(ablId, sequence, true));
        }
    }

    public void updateRemoveArtifact(LivingEntityBeyonderCapability cap, Player player, String ablId, boolean sync){
        if(artifactActives.containsKey(ablId)){
            System.out.println("Removing artifact: " + ablId);
            artifactActives.get(ablId).deactivate(cap, player);
            artifactActives.remove(ablId);
            if(sync)
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PlayerArtifactSyncSTC(ablId, 0, false));
        } else System.out.println("Tried to remove a non existent artifact ability");

    }

    /**
     * this methods is the one that goes through the inventory and gets the list of artifacts to change.
     * happens about once a second on server side only.
     * @param cap
     * @param player
     */
    public void updateArtifacts(LivingEntityBeyonderCapability cap, Player player) {
        for (String ablId : new ArrayList<>(activeCooldowns.keySet())) {
            if(activeCooldowns.get(ablId) == 0
                    && !artifactActives.containsKey(ablId)
                    && !pathwayActives.containsKey(ablId)){
                System.out.println("Removing cooldown and enabled info for ability: " + ablId);
                activeCooldowns.remove(ablId);
                enabledDisabled.remove(ablId);
            }
        }
        //because artifacts depend on NBT data, it makes no sense to try to update them on client side.
        //use messages to update the client.
        if(player.level().isClientSide()) return;

        //1 - create list of the best artifact abilities from inventory data
        //returns a map, connecting ablIds to artifactIds
        Map<String, String> inventoryArtifacts = getArtifactsFromInventory(player);
        //2 - update artifacts list attribute if anything changed
        //add new artifacts to list
        for (String artifactId : inventoryArtifacts.values()) {
            String ablId = artifactId.substring(3);
            int newLevel = artifactId.charAt(1) - '0';

            if (shouldAddArtifact(ablId, newLevel)) {
                updateAddArtifact(cap, player, ablId, newLevel, true);
            }
        }
        //remove artifacts from list
        for (String ablId : new ArrayList<>(artifactActives.keySet())) {
            if (!inventoryArtifacts.containsKey(ablId)) {
                updateRemoveArtifact(cap, player, ablId, true);
            }
            //when you advance, removing newly-turned-useless artifact abilities is dealt with in the set abilites method
        }

    }

    /**
     * This method should return a map, connectin AblID to ArtifactID.
     * Returns the best abilities found in the inventory
     */
    private Map<String, String> getArtifactsFromInventory(Player player) {
        HashMap<String, String> resMap = new HashMap<>();
        ItemStack validAmuletEnabled = ItemStack.EMPTY;
        boolean tooManyAmulets = false;
        for(ItemStack itemStack: player.getInventory().items){
            if(itemStack.is(ModItems.AMULET.get()) && ArtifactHelper.isValidArtifact(itemStack)){
                if(validAmuletEnabled.isEmpty()){
                    validAmuletEnabled = itemStack;
                } else {
                    tooManyAmulets = true;
                    NecklaceItem.enableAmulet(validAmuletEnabled, false);
                    NecklaceItem.enableAmulet(itemStack, false);
                }
            }
            List<String> ablDown = ArtifactHelper.getArtifactIdsFromItem(itemStack);
            for(String artifactId: ablDown){
                addAbilityToMap(artifactId, resMap);
            }
//            if(itemStack.hasTag()){
//            }
        }
        if(ModList.get().isLoaded("curios")){
            if(CuriosApi.getCuriosInventory(player).resolve().isPresent()){
                ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
                Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();
                for(ICurioStacksHandler handler: curios.values()){
                    int slots = handler.getSlots();
                    for(int i = 0; i < slots; i++){
                        ItemStack itemStack = handler.getStacks().getStackInSlot(i);
                        if(itemStack.is(ModItems.AMULET.get()) && ArtifactHelper.isValidArtifact(itemStack)){
                            if(validAmuletEnabled.isEmpty()){
                                validAmuletEnabled = itemStack;
                            } else {
                                tooManyAmulets = true;
                                NecklaceItem.enableAmulet(validAmuletEnabled, false);
                                NecklaceItem.enableAmulet(itemStack, false);
                            }
                        }
                        List<String> ablDown = ArtifactHelper.getArtifactIdsFromItem(itemStack);
                        for(String artifactId: ablDown){
                            addAbilityToMap(artifactId, resMap);
                        }
                    }
                }
            }
        }
        if(!validAmuletEnabled.isEmpty() && !tooManyAmulets){
            NecklaceItem.enableAmulet(validAmuletEnabled, true);
        }
        return resMap;
    }

    private void addAbilityToMap(String id, Map<String, String> map){
        if(map.containsKey(id.substring(3))){
            int ablLevel = id.charAt(1) - '0';
            int prevLevel = map.get(id.substring(3)).charAt(1) - '0';
            if(ablLevel < prevLevel){
                map.put(id.substring(3), id);
            }
        } else {
            map.put(id.substring(3), id);
        }
    }

    private boolean shouldAddArtifact(String ablId, int sequenceLevel){
        //this method assumes the passed ability is the best found in the players inventory
        boolean isIntrinsic = pathwayActives.containsKey(ablId);
        boolean isStrongerThanIntrinsic = !isIntrinsic
                || pathwayActives.get(ablId).getSequence() > sequenceLevel;
        boolean shouldReplaceInArtifacts = !artifactActives.containsKey(ablId)
                || artifactActives.get(ablId).getSequence() != sequenceLevel;
        return shouldReplaceInArtifacts && isStrongerThanIntrinsic;
    }

    public void onTick(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(!pathwayActives.isEmpty()){
            pathwayActives.values().forEach(ability -> {
                ability.passive(cap, target);
            });
        }
        if(!artifactActives.isEmpty()){
            artifactActives.values().forEach(ability -> {
                ability.passive(cap, target);
            });
        }
//        if(!otherPassives.isEmpty()){
//            otherPassives.forEach(ability -> {
//                ability.passive(cap, target);
//            });
//        }

        for (String key : activeCooldowns.keySet()) {
            if (activeCooldowns.get(key) > 0) activeCooldowns.put(key, activeCooldowns.get(key) - 1);
        }
    }

//    public void addPassiveAbility(Ability ability, boolean pathway){
//        if(pathway) pathwayPassives.add(ability);
//        else otherPassives.add(ability);
//    }

    public void clear(boolean pathway, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(pathway) {
            //condition on the passives list since its generally smaller than actives list
            //this way it only calls the deactivate once
            //as of right now, every passive ability is in actives, so you could ditch this first forEach
            //however, if in the future an ability is added that is completely passive, this will be necessary
            //pathwayPassives.values().forEach(ability -> {if(!pathwayActives.containsKey(ability.getInfo().normalizedId())) ability.deactivate(cap, target);});
            //pathwayPassives = new HashMap<>();
            pathwayActives.values().forEach(ability -> ability.deactivate(cap, target));
            pathwayActives = new HashMap<>();
//            activeCooldowns.keySet().removeIf(key -> !artifactActives.containsKey(key));
//            enabledDisabled.keySet().removeIf(key -> !artifactActives.containsKey(key));
        }
    }

    public void useAbility(LivingEntityBeyonderCapability cap, LivingEntity tar, String ablId, boolean sync, boolean artifactFirst){
        if(tar instanceof Player player){
            boolean inArtifactsList = artifactActives.containsKey(ablId);
            boolean inPathwayList = pathwayActives.containsKey(ablId);
            if(player.level().isClientSide()){
                if(sync) PacketHandler.INSTANCE.sendToServer(new PlayerCastAbilityMessageCTS(ablId));
                //System.out.println("Activating ability on client side: " + ablId);
                if(artifactFirst && inArtifactsList) artifactActives.get(ablId).active(cap, tar);
                else if(inPathwayList) pathwayActives.get(ablId).active(cap, tar);
                else if(inArtifactsList) artifactActives.get(ablId).active(cap, tar);
                else System.out.println("Ability ID specified was not found: " + ablId);
            } else {
                if(activeCooldowns.get(ablId) != null && activeCooldowns.get(ablId) != 0){
                    System.out.println("Tried to activate ability on cooldown");
                    return;
                }
                boolean flag = false;
                if((artifactFirst && inArtifactsList)
                        || (!artifactFirst && !inPathwayList && inArtifactsList)){
                    if(artifactActives.get(ablId).active(cap, tar)){
                        System.out.println("Activating ability: " + ablId);
                        Ability abl = artifactActives.get(ablId);
                        flag = true;
                        putOnCooldown(player, ablId, abl.getCooldown(), abl.getInfo().maxCooldown());
                    }
                } else if(inPathwayList){
                    if(pathwayActives.get(ablId).active(cap, tar)){
                        System.out.println("Activating ability: " + ablId);
                        flag = true;
                        putOnCooldown(player, ablId);
                    }
                } else System.out.println("Tried to cast ability that doesnt exist: " + ablId);

                if(sync && flag)
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new PlayerCastAbilityMessageCTS(ablId));
            }

        }
    }

    /**
     * Method that updates the data in newMap into original. it overrides in original the values it can get from newMap.
     * Any extras in original remain the same, and any extra in newMap are ignored.
     *
     * Also guarantees that any active-type ability (defined manually per ability) is enabled.
     * @param original
     * @param newMap
     * @param <T>
     */
    public <T> void setMap(Map<String, T> original, Map<String, T> newMap){

        for (Map.Entry<String, T> entry : newMap.entrySet()) {
            if (original.containsKey(entry.getKey())) {
                original.put(entry.getKey(), entry.getValue());
            }
        }

        if(newMap.size() < pathwayActives.size()){
            System.out.println("WARNING: received list is SMALLER than the pathwayActives list.\nProceed with caution!!!");
//            System.out.println("List size: " + list.size());
//            System.out.println("EnabledDisabled list size: " + enabledDisabled.size());
//            System.out.println("pathwayActives list size: " + pathwayActives.size());
        }
        for (Ability abilityInfo: pathwayActives.values()) {
            if(abilityInfo.isActive) enabledDisabled.put(abilityInfo.getInfo().normalizedId(), true);
        }
    }

    public void setEnabled(Ability abl, boolean bol, LivingEntityBeyonderCapability cap, LivingEntity target){
        String ablId = abl.getInfo().normalizedId();
        if(enabledDisabled.containsKey(ablId)){
            boolean prevStatus = enabledDisabled.get(ablId);
            if(prevStatus && !bol){
                abl.deactivate(cap, target);
            } else if(!prevStatus && bol){
                abl.activate(cap, target);
            }
            enabledDisabled.put(ablId, bol);
        }
    }

    public boolean isEnabled(Ability abl){
        String ablId = abl.getInfo().normalizedId();
        if(!enabledDisabled.containsKey(ablId)) return false;
        return enabledDisabled.get(ablId);
    }

    public List<String> disabledALlAbilities(Player player, String abilityToIgnore){
        ArrayList<String> res = new ArrayList<>();
        for(String ablId: pathwayActives.keySet()){
            if(abilityToIgnore.equals(ablId)) continue;

            putOnCooldown(player, ablId, -activeCooldowns.get(ablId)/2 - 1);
            res.add(ablId);
        }
        return res;
    }

    public void reactivateAbilities(Player player, List<String> deactivatedAbilities) {
        for(String ablID: deactivatedAbilities){
            putOnCooldown(player, ablID, -activeCooldowns.get(ablID)/2 - 1);
        }
    }

    public void putOnCooldown(Player player, String ablId){
        putOnCooldown(player, ablId, pathwayActives.get(ablId).getCooldown());
    }

    public void putOnCooldown(Player player, String ablId, int cd){
        putOnCooldown(player, ablId, cd, pathwayActives.get(ablId).getInfo().maxCooldown());
    }

    public void putOnCooldown(Player player, String ablId, int cd, int maxCd){
        activeCooldowns.put(ablId, cd*2);
        //tick methods ticks twice a fast as on client -> 40 ticks per second
        if(pathwayActives.containsKey(ablId)){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAbilityCooldownSTC(ablId, cd, maxCd));
        }
    }

    public void updateClientCooldownInfo(ServerPlayer player){
        for (Ability pathwayActive : pathwayActives.values()) {
            String desc = pathwayActive.getInfo().normalizedId();
            if (activeCooldowns.get(desc) != 0) {
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                        new PlayerAbilityCooldownSTC(desc, activeCooldowns.get(desc), pathwayActive.getInfo().maxCooldown()));
            }
        }
    }

    public void setPathwayActives(ArrayList<Ability> abilities){
//        System.out.println("Active abilities updates. Size is: " + abilities.size());
        if(abilities == null) return;
        pathwayActives = new LinkedHashMap<>();
        for (Ability ability : abilities) {
            String ablId = ability.getInfo().normalizedId();
            pathwayActives.put(ablId, ability);
            activeCooldowns.put(ablId, 0);
            enabledDisabled.put(ablId, true);
            if(artifactActives.containsKey(ablId) && artifactActives.get(ablId).getSequence() >= ability.getSequence()){
                artifactActives.remove(ablId);
            }
        }
    }

    public void onAcquireAbilities(LivingEntityBeyonderCapability cap, LivingEntity target){
        //TODO: only going through actives. if you want for passives, youll need to add it
        // Check if its enabled for when the player loads into the world -> dont activate extended reach if the ability was previously disabled
        //its loading the enabledDisabled list before calling this onAcquire function when loading data
        for (Ability ability : pathwayActives.values()) {
            if (enabledDisabled.get(ability.getInfo().normalizedId())) ability.onAcquire(cap, target);
        }
    }

    public ArrayList<AbilityInfo> getActivesIds() {
        return new ArrayList<>(pathwayActives.values().stream().map(Ability::getInfo).toList());
    }

//    public void setPathwayPassives(ArrayList<Ability> abilities){
//        //System.out.println("Passive abilities updates. Size is: " + abilities.size());
//        if(abilities == null) return;
//        pathwayPassives = new HashMap<>();
//        for (Ability ability : abilities) {
//            pathwayPassives.put(ability.getInfo().normalizedId(), ability);
//        }
//    }

    public void saveNBTData(CompoundTag nbt){
        CompoundTag enabled = new CompoundTag();
        enabled.putInt("size", enabledDisabled.size());
        int i = 0;
        for (Map.Entry<String, Boolean> entry : enabledDisabled.entrySet()) {
            enabled.putString("id_" + i, entry.getKey());
            enabled.putBoolean("bool_" + i, entry.getValue());
            i++;
        }
        nbt.put("enabled_abilities", enabled);

        CompoundTag cooldowns = new CompoundTag();
        cooldowns.putInt("size", activeCooldowns.size());
        i = 0;
        for (Map.Entry<String, Integer> entry : activeCooldowns.entrySet()) {
            cooldowns.putString("id_" + i, entry.getKey());
            cooldowns.putInt("cooldown_" + i, entry.getValue());
            i++;
        }
        nbt.put("cooldowns", cooldowns);

        CompoundTag hotbar = new CompoundTag();
        hotbar.putInt("size", clientHotbar.size());
        for(int j = 0; j < clientHotbar.size(); j++){
            hotbar.putString(String.valueOf(j), clientHotbar.get(j));
        }
        hotbar.putString("quick", quickAbility);
        nbt.put("hotbar", hotbar);
    }

    public void loadEnabledListFromTag(CompoundTag nbt){
        CompoundTag enabledAbilitiesTag = nbt.getCompound("enabled_abilities");
        int size = enabledAbilitiesTag.getInt("size");
        HashMap<String, Boolean> enabled = new HashMap<>();
        if(size != 0){
            for(int i = 0; i < size; i++){
                String key = enabledAbilitiesTag.getString("id_" + i);
                boolean val = enabledAbilitiesTag.getBoolean("bool_" + i);
                if(!key.isEmpty()) enabled.put(key, val);
            }
        }
        setMap(this.enabledDisabled, enabled);
    }

    public void loadNBTData(CompoundTag nbt){
        /*CompoundTag enabledAbilities = nbt.getCompound("enabled_abilities");
        int size = enabledAbilities.getInt("size");
        ArrayList<Boolean> enabled = new ArrayList<>();
        if(size != 0){
            for(int i = 0; i < size; i++){
                enabled.add(true);
            }
            //syncing with the abilities you had from pathway
            //this is important if pathway abilities change between world loads,
            //so itll at least try to keep the info on what abilities were on or off
//            System.out.println(enabled.size());
            setEnabledList(enabled);
        }*/

        CompoundTag cds = nbt.getCompound("cooldowns");
        int sizeCd = cds.getInt("size");
        HashMap<String, Integer> cooldowns = new HashMap<>();
        if(sizeCd != 0){
            for (int i = 0; i < sizeCd; i++) {
                String key = cds.getString("id_" + i);
                int val = cds.getInt("cooldown_" + i);
                cooldowns.put(key, val);
            }
        }
        setMap(activeCooldowns, cooldowns);


        CompoundTag hot = nbt.getCompound("hotbar");
        int sizeHot = hot.getInt("size");
        if(sizeHot != 0){
            for(int i = 0; i < sizeHot; i++){
                clientHotbar.add(hot.getString(String.valueOf(i)));
            }
        }
        quickAbility = hot.getString("quick");

        loadEnabledListFromTag(nbt);
    }
}
