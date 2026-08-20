package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.*;
import net.dinomine.potioneer.beyonder.pages.Page;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.event.AbilityPossessionEvent;
import net.dinomine.potioneer.event.ArtifactPossessionEvent;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.AbilitySyncMessage;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerArtifactSyncSTC;
import net.dinomine.potioneer.network.messages.abilityRelevant.PlayerCastAbilityMessageCTS;
import net.dinomine.potioneer.util.misc.ArtifactHolder;
import net.dinomine.potioneer.util.misc.ModNbtUtils;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.*;
import java.util.function.Consumer;

public class PlayerAbilitiesManager {
    private LinkedHashMap<UUID, Ability> abilities = new LinkedHashMap<>();
    private LinkedHashMap<UUID, ArtifactHolder> artifacts = new LinkedHashMap<>();
    private DisabledAbilitiesManager disabledManager = new DisabledAbilitiesManager();
    private final List<Ability> intrisicAbilitiesBuffer = new ArrayList<>();

    public ArrayList<UUID> clientHotbar = new ArrayList<>();
    public UUID quickAbility = null;

    public @Nullable Ability getQuickAbility(){
        return quickAbility == null ? null : getAbilityInstance(quickAbility);
    }

    public @Nullable Ability getAbilityById(UUID instanceId){
        if(instanceId == null) return null;
        if(abilities.containsKey(instanceId)) return abilities.get(instanceId);
        for(ArtifactHolder artifactHolder: artifacts.values()){
            for(Ability abl: artifactHolder.getAbilities()){
                if(abl.getInstanceId().equals(instanceId)) return abl;
            }
        }
        return null;
    }

    public List<Ability> getAllAbilities(AbilityInfo.Group group){
        return new ArrayList<>(getAllAbilities().stream().filter(abl -> abl.isOfGroup(group)).toList());
    }

    @Override
    public String toString() {
        return "Abilities: " + abilities.values().stream().map(Ability::toString).toList() +
                "\nArtifacts: " + artifacts.values().stream().map(ArtifactHolder::toString).toList();
    }

    public void copyFrom(PlayerAbilitiesManager mng){
        this.clientHotbar = mng.clientHotbar;
        this.quickAbility = mng.quickAbility;
        this.abilities = mng.abilities;
        this.disabledManager = mng.disabledManager;
    }

    public DisabledAbilitiesManager getDisabledAbilitiesManager(){return disabledManager;}

    public void castArtifactAbility(UUID artifactKey, BeyonderCapability cap, LivingEntity target){
        if(!target.level().isClientSide() && target instanceof Player player){
            PacketHandler.sendMessageSTC(new PlayerCastAbilityMessageCTS(artifactKey), player);
        }
        ArtifactHolder artifact = artifacts.get(artifactKey);
        artifact.castDefaultAbilities(cap, target);
    }
    public @Nullable Ability getFirstAbilityOutOfCooldown(ResourceLocation ability){
        List<Ability> matches = getAllAbilities(ability);
        for(Ability abl: matches){
            if(abl.getCooldown() == 0) return abl;
        }
        return null;
    }

    public Ability getAbilityInstance(UUID instanceId){
        for(ArtifactHolder artifact: artifacts.values()){
            for(Ability abl: artifact.getAbilities()){
                if(abl.is(instanceId)) return abl;
            }
        }
        for(Ability abl: abilities.values()){
            if(abl.is(instanceId)) return abl;
        }
        return null;
    }

    /*public Ability getAbility(String abilityId, String abilityGroup){
        for(Ability abl: abilities.values()){
            if(abl.is(abilityId) && abl.getType().equals(abilityGroup)) return abl;
        }
        return null;
    }*/

    public List<Page> getPagesFromAbilities(){
        List<Page> result = new ArrayList<>();
        for(Ability abl: abilities.values()){
            result.addAll(abl.getPages());
        }
        return result;
    }

    /**
     * this methods is the one that goes through the inventory and gets the list of artifacts to change.
     * happens about once a second on server side only.
     * @param cap
     * @param player
     */
    public void updateArtifacts(BeyonderCapability cap, Player player) {
        //because artifacts depend on NBT data, it makes no sense to try to update them on client side.
        //use messages to update the client.
        if(player.level().isClientSide()) return;

        //1 - create list of working artifacts from inventory data
        //returns a map, connecting ablIds to artifactIds
        Map<UUID, ArtifactHolder> inventoryArtifacts = getArtifactsFromInventory(player);
        //2 - update artifacts list attribute if anything changed
        //add new artifacts to list
        for (Map.Entry<UUID, ArtifactHolder> entry: inventoryArtifacts.entrySet()) {
            UUID artifactKey = entry.getKey();
            ArtifactHolder artifact = entry.getValue();

            if (!artifacts.containsKey(artifactKey)) {
                addArtifact(artifact, cap, player, true, true);
            } else {
                artifacts.get(artifactKey).updateItem(artifact.getItem());
            }
        }
        //remove artifacts from list
        for (UUID artifactId : new ArrayList<>(artifacts.keySet())) {
            if (!inventoryArtifacts.containsKey(artifactId)) {
                removeArtifact(artifactId, cap, player, true);
            }
        }
    }

    /**
     * This method should return a map, connectin artifact id to artifact instance.
     */
    private HashMap<UUID, ArtifactHolder> getArtifactsFromInventory(Player player) {
//        ItemStack validAmuletEnabled = ItemStack.EMPTY;
//        boolean tooManyAmulets = false;
        HashMap<UUID, ArtifactHolder> resMap = new HashMap<>();
        iterateThroughInventory(player, itemStack -> {
            ArtifactHolder artifact = MysticalItemHelper.getWorkingArtifactFromItem(itemStack);
            if(artifact != null)
                resMap.putIfAbsent(artifact.getArtifactId(), artifact);
        });
        return resMap;
    }

    private boolean addArtifact(ArtifactHolder artifact, BeyonderCapability cap, LivingEntity target, boolean runOnAcquire, boolean sync){
        if(artifact == null || artifact.isEmpty()) return false;
        if(artifacts.containsKey(artifact.getArtifactId())) return false;
        artifacts.put(artifact.getArtifactId(), artifact);
        MinecraftForge.EVENT_BUS.post(new ArtifactPossessionEvent.Gained(artifact, target));
        if (runOnAcquire) artifact.onAcquire(cap, target);
        if(sync && target instanceof Player player) updateClientArtifactInfo(player, List.of(artifact), PlayerArtifactSyncSTC.ADD);
        return true;
    }

    private boolean removeArtifact(UUID artifactId, BeyonderCapability cap, Player player, boolean sync){
        if(artifactId == null) return false;
        ArtifactHolder artifact = artifacts.remove(artifactId);
        MinecraftForge.EVENT_BUS.post(new ArtifactPossessionEvent.Lost(artifact, player));
        artifact.onRemove(cap, player);
        if(sync) updateClientArtifactInfo(player, List.of(artifact), PlayerArtifactSyncSTC.REMOVE);
        return true;
    }


    private static void iterateThroughInventory(Player player, Consumer<ItemStack> consumer){
        for(ItemStack itemStack: player.getInventory().items){
            consumer.accept(itemStack);
        }
        for(ItemStack itemStack: player.getArmorSlots()){
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

    public void onTick(BeyonderCapability cap, LivingEntity target){
        if(!abilities.isEmpty()){
            abilities.values().forEach(ability -> {
                ability.passive(cap, target);
                ability.tickCooldown(target);
            });
        }
        artifacts.values().forEach(ability -> {
            ability.passives(cap, target);
        });
        disabledManager.tickDisabledAbilities(cap, target);
    }


    public void clearAbilities(BeyonderCapability cap, LivingEntity target){
        abilities.values().forEach(ability -> ability.deactivate(cap, target));
        abilities = new LinkedHashMap<>();
    }

    public void clearArtifacts(BeyonderCapability cap, LivingEntity target){
        artifacts.values().forEach(ability -> ability.onRemove(cap, target));
        artifacts = new LinkedHashMap<>();
    }

    /**
     * called on loading NBT tag. saves the abilities without initializing them.
     * @param abilitiesToSet
     */
    public void loadIntrinsicAbilities(List<Ability> abilitiesToSet) {
        if(!intrisicAbilitiesBuffer.isEmpty()) return;
        intrisicAbilitiesBuffer.addAll(abilitiesToSet);
    }
    public void setAndInitializeIntrinsicAbilities(List<Ability> abilitiesToSet, int pathwaySequenceId, BeyonderCapability cap, LivingEntity target) {
        if(abilitiesToSet.isEmpty()){
            cap.getAbilitiesManager().clearAbilities(cap, target);
            return;
        }

        //first, remove abilities you lose. they shouldnt be affecting you anymore.
        //only removes abilities that are intrinsic
        List<UUID> abilitiesToRemove = new ArrayList<>();
        for(Ability abl: getAllAbilities(AbilityInfo.Group.INTRINSIC)){
            if(abilitiesToSet.stream().map(Ability::getAbilityId).noneMatch(abl::is))
                abilitiesToRemove.add(abl.getInstanceId());
        }
        //sync false since we update in the end
        abilitiesToRemove.forEach(key -> removeAbility(key, cap, target, false));

        //then, upgrade/downgrade any and all existing intrinsic abilities to the target sequence level, regardless of their original pathway.
        upgradeIntrinsicAbilitiesToLevel(pathwaySequenceId%10, cap, target);

        //then grant new abilities
        for(Ability abl: abilitiesToSet){
            //already checks if it exists
            addAndInitializeAbility(abl, cap, target, true, false);
        }
        //then replace any cogitation abilities with the pathway one, so it shows up first.
        replaceCogitation(pathwaySequenceId, cap, target, true);

        //finally, update client info
        if(target instanceof Player player) updateSetClientAbilityInfo(player);
    }

    private void replaceCogitation(int pathwaySequenceId, BeyonderCapability cap, LivingEntity target, boolean runOnAcquire) {
        if(abilities.isEmpty()) return;
        for(Ability abl: new ArrayList<>(getAllAbilities(AbilityInfo.Group.INTRINSIC))){
            if(abl.getAbilityId().toString().contains("cogitation")){
                abilities.remove(abl.getInstanceId());
                break;
            }
        }
        addAndInitializeAbility(Pathways.getPathwayBySequenceId(pathwaySequenceId).getCogitationAbility().construct(pathwaySequenceId%10, AbilityInfo.Group.INTRINSIC), cap, target, true, false);
    }

    private void upgradeIntrinsicAbilitiesToLevel(int sequenceLevel, BeyonderCapability cap, LivingEntity target){
        for(Ability abl: new ArrayList<>(getAllAbilities(AbilityInfo.Group.INTRINSIC))){
            if(abl.getSequenceLevel() != sequenceLevel){
                abl.permanentlyUpgradeToLevel(sequenceLevel, cap, target);
            }
        }
    }

    public boolean removeAbility(UUID ablId, BeyonderCapability cap, LivingEntity target, boolean sync){
        if(!abilities.containsKey(ablId)) return false;
        Ability abl = abilities.get(ablId);
        MinecraftForge.EVENT_BUS.post(new AbilityPossessionEvent.Lost(abl, target));
        abl.deactivate(cap, target);
        abilities.remove(ablId);
        if(sync && target instanceof Player player) updateClientAbilityInfo(player, List.of(abl.getAbilityInfo()), AbilitySyncMessage.REMOVE);
        return true;
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
    public void setAbilityEnabled(ResourceLocation abilityId, int sequenceLevel, boolean state, BeyonderCapability cap, LivingEntity target) {
        applyToValidAbilities(abl -> abl.setEnabled(cap, target, state), abilityId, sequenceLevel, true);
    }

    private void applyToValidAbilities(Consumer<Ability> applier, ResourceLocation abilityId, int sequenceLevel, boolean specificLevel){
        for(Ability abl: getAllAbilities()){
            if(abl.is(abilityId) &&
                    (abl.getSequenceLevel() == sequenceLevel ||
                            (!specificLevel && abl.getSequenceLevel() > sequenceLevel))
            ){
                applier.accept(abl);
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
    public void putAbilityOnCooldown(ResourceLocation abilityId, int sequenceLevel, int cooldownTicks, LivingEntity target){
        applyToValidAbilities(abl -> abl.putOnCooldown(cooldownTicks, target), abilityId, sequenceLevel, false);
        /*for(Map.Entry<AbilityKey, Ability> abilityEntry: abilities.entrySet()){
            AbilityKey iKey = abilityEntry.getKey();
            if(iKey.isSameAbility(abilityId) && abilityEntry.getValue().getSequenceLevel() >= sequenceLevel){
                abilityEntry.getValue().putOnCooldown(cooldownTicks, target);
            }
        }*/
    }

    public void putAbilityOnCooldown(UUID ablId, int cooldownTicks, LivingEntity target){
        Ability abl = getAbilityInstance(ablId);
        if(abl == null) return;
        abl.putOnCooldown(cooldownTicks, target);
    }

    public boolean isEnabledExactLevel(ResourceLocation abilityId, int sequenceLevel){
        for(Ability abl: getAllAbilities()){
            if(abl.is(abilityId, sequenceLevel)){
                return abl.isEnabled();
            }
        }
        return false;
    }

    public boolean isEnabledAtLevelOrLower(ResourceLocation abilityId, int sequenceLevel) {
        for(Ability abl: getAllAbilities()){
            if(abl.is(abilityId) && abl.getSequenceLevel() >= sequenceLevel){
                return abl.isEnabled();
            }
        }
        return false;
    }

    /**
     * method to set the abilities on the client-side manager to match with server-side, based on the corresponding ability infos
     * @param abilities
     */
    public void setAbilitiesOnClient(List<AbilityInfo> abilities, BeyonderCapability cap, LivingEntity target) {
        if(!target.level().isClientSide()) return;
        clearAbilities(cap, target);
        addAbilitiesOnClient(abilities, cap, target, true);
        updateAbilitiesOnClient(abilities, cap, target);
    }

    public void setArtifactsOnClient(List<ArtifactHolder> artifacts, BeyonderCapability cap, Player player) {
        if(!player.level().isClientSide()) return;
        clearArtifacts(cap, player);
        addArtifactsOnClient(artifacts, cap, player, false);
    }

    public void updateArtifactsOnClient(List<ArtifactHolder> artifacts, BeyonderCapability cap, Player player){
        if(!player.level().isClientSide()) return;
        for(ArtifactHolder artifact: artifacts){
            ArtifactHolder oldArtifact = this.artifacts.getOrDefault(artifact.getArtifactId(), null);
            if(oldArtifact == null) continue;
            this.artifacts.put(artifact.getArtifactId(), artifact);
        }
    }

    public void addAbilitiesOnClient(List<AbilityInfo> abilities, @NotNull BeyonderCapability cap, LivingEntity target, boolean runOnAcquire) {
        if(!target.level().isClientSide()) return;
        for(AbilityInfo abl: abilities){
            Ability ability = Ability.constructAbility(abl);
            if(!addAndInitializeAbility(ability, cap, target, runOnAcquire, false)){
                System.out.println("Warning: Tried to add an ability that already exists on client: " + abl);
            }
        }
    }

    public void addArtifactsOnClient(List<ArtifactHolder> artifacts, @NotNull BeyonderCapability cap, Player player, boolean runOnAcquire) {
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

    public void removeAbilitiesOnClient(List<AbilityInfo> abilities, @NotNull BeyonderCapability cap, LivingEntity target) {
        if(!target.level().isClientSide()) return;
        for(AbilityInfo abl: abilities){
            UUID toRemove = abl.getInstanceId();
            if(!removeAbility(toRemove, cap, target, false)){
                System.out.println("Warning: Tried to remove an ability that doesnt exist on client: " + abl);
            }
        }
    }

    public void removeArtifactsOnClient(List<ArtifactHolder> artifacts, @NotNull BeyonderCapability cap, Player player) {
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

    public void updateAbilitiesOnClient(List<AbilityInfo> abilities2, @NotNull BeyonderCapability cap, LivingEntity target) {
        if(!target.level().isClientSide()) return;
        for(AbilityInfo info: abilities2){
            Ability abl = getAbilityById(info.getInstanceId());
            if(abl == null){
                continue;
            }
            abl.receiveUpdateOnClient(info, cap, target);
        }
    }

    public void onAbilityUpdateData(Ability ability, BeyonderCapability cap, LivingEntity target) {
        if(!(target instanceof Player player)) return;
        if(player.level().isClientSide()) return;
        PacketHandler.sendMessageSTC(new AbilitySyncMessage(ability.getAbilityInfo(), AbilitySyncMessage.UPDATE), player);

        //if it can be found in the standard abilities map, then its not an artifact.
        if(abilities.containsKey(ability.getInstanceId())) return;

        //otherwise, attempt to update its item.
        ArtifactHolder artifactHolder = null;
        for(ArtifactHolder artifact: artifacts.values()){
            if(!artifact.hasAbility(ability.getInstanceId())) continue;
            artifactHolder = artifact;
            break;
        }
        if(artifactHolder == null) {
            String message = "[Potioneer] Error: Tried to update ability data, but it wasnt found in the abilities map nor in any artifacts." + ability;
            System.err.println(message);
            Potioneer.LOGGER.error(message);
            return;
        }
        List<ItemStack> items = getItemsInInventory(player);
        for(ItemStack item: items){
            UUID itemId = MysticalItemHelper.getArtifactIdFromItem(item);
            if(itemId == null) continue;
            if(itemId.equals(artifactHolder.getArtifactId())){
                MysticalItemHelper.updateArtifactTagOnItem(artifactHolder, item);
                return;
            }
        }
        String message = "[Potioneer] Error: Tried to update artifact data into item but no matching item was found: " + ability;
        System.err.println(message);
        Potioneer.LOGGER.error(message);
    }

    public static List<ItemStack> getItemsInInventory(Player player){
        List<ItemStack> result = new ArrayList<>(player.getInventory().items);
        if(ModList.get().isLoaded("curios")){
            if(CuriosApi.getCuriosInventory(player).resolve().isPresent()){
                ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
                Map<String, ICurioStacksHandler> curios = curiosInventory.getCurios();
                for(ICurioStacksHandler handler: curios.values()){
                    int slots = handler.getSlots();
                    for(int i = 0; i < slots; i++){
                        ItemStack itemStack = handler.getStacks().getStackInSlot(i);
                        result.add(itemStack);
                    }
                }
            }
        }
        return result;
    }

    public int getNumArtifacts() {
        return artifacts.size();
    }

    public int getSequenceLevelOfAbility(ResourceLocation ablId){
        int highest = 10;
        for(Ability abl: abilities.values()){
            if(abl.is(ablId) && abl.getSequenceLevel() < highest){
                highest = abl.getSequenceLevel();
            }
        }
        if(highest != 10) return highest;
        for(ArtifactHolder artifact: artifacts.values()){
            for(Ability abl: artifact.getAbilities()){
                if(abl.is(ablId) && abl.getSequenceLevel() < highest)
                    highest = abl.getSequenceLevel();
            }
        }
        return highest;
    }

    public boolean hasAbilityOrBetter(ResourceLocation ablId, int sequenceLevel) {
        for(Ability abl: abilities.values()){
            if(abl.is(ablId) && abl.getSequenceLevel() <= sequenceLevel) return true;
        }
        for(ArtifactHolder artifact: artifacts.values()){
            for(Ability abl: artifact.getAbilities()){
                if(abl.is(ablId) && abl.getSequenceLevel() <= sequenceLevel) return true;
            }
        }
        return false;
    }

    public boolean hasAbility(ResourceLocation ablId) {
        return hasAbilityOrBetter(ablId, 9);
    }

    public boolean hasAbilityAndIsOutOfCooldown(ResourceLocation ablId){
        List<Ability> abilities = getAllAbilities(ablId);
        return abilities.stream().anyMatch(abl -> abl.getCooldown() == 0);
    }

    public List<Ability> getAllAbilities() {
        List<Ability> res = new ArrayList<>(abilities.values());
        for(ArtifactHolder art: artifacts.values()) res.addAll(art.getAbilities());
        return res;
    }
    public List<Ability> getAllAbilities(ResourceLocation abilityId) {
        return getAllAbilities().stream().filter(abl -> abl.is(abilityId)).toList();
    }

    public void updateArtifact(@Nullable UUID artifactId, Player player, ItemStack artifactStack) {
        if(artifactId == null || !artifacts.containsKey(artifactId)) return;
        ArtifactHolder artifact = artifacts.get(artifactId).withStack(artifactStack);
        updateClientArtifactInfo(player, List.of(artifact), PlayerArtifactSyncSTC.UPDATE);
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


    /**
     * ability should already be pre-initialized and loaded if relevant. the UUID associated will be the one used.
     * @param ability
     * @param cap
     * @param target
     * @param runOnAcquire
     * @param sync
     * @return
     */
    public boolean addAndInitializeAbility(Ability ability, BeyonderCapability cap, LivingEntity target, boolean runOnAcquire, boolean sync){
        if(abilities.containsKey(ability.getInstanceId())) return false;
        boolean exists = getAllAbilities(ability.getAbilityInfo().getGroup()).stream().anyMatch(abl -> abl.is(ability.getAbilityId(), ability.getSequenceLevel()));
        if(exists) return false;
        ability.init();
        abilities.put(ability.getInstanceId(), ability);
        disabledManager.onAbilityGained(ability, cap, target);
        MinecraftForge.EVENT_BUS.post(new AbilityPossessionEvent.Gained(ability, target));
        if (runOnAcquire) ability.onAcquire(cap, target);
        if(sync && target instanceof ServerPlayer player) updateClientAbilityInfo(player, List.of(ability.getAbilityInfo()), AbilitySyncMessage.ADD);
        return true;
    }

    public boolean removeFirstAbilityLike(ResourceLocation abilityId, AbilityInfo.Group abilityGroup, BeyonderCapability cap, LivingEntity target, boolean sync){
        if(abilityGroup == AbilityInfo.Group.INTRINSIC) return false;
        Optional<Ability> optAbl = findAbility(abilityId, abilityGroup);
        if(optAbl.isEmpty()) return false;
        Ability abl = optAbl.get();
        MinecraftForge.EVENT_BUS.post(new AbilityPossessionEvent.Lost(abl, target));
        abl.deactivate(cap, target);
        abilities.remove(abl.getInstanceId());
        if(sync && target instanceof ServerPlayer player) updateClientAbilityInfo(player, List.of(abl.getAbilityInfo()), AbilitySyncMessage.REMOVE);
        return true;
    }

    private Optional<Ability> findAbility(ResourceLocation abilityId, AbilityInfo.Group abilityGroup){
        return getAllAbilities().stream().filter(abl -> abl.is(abilityId) && abl.isOfGroup(abilityGroup)).findFirst();
    }

    public void useAbility(BeyonderCapability cap, LivingEntity tar, UUID ablId, boolean sync, boolean primary, CompoundTag args){
        Ability abl = getAbilityById(ablId);
        if(abl == null) return;
        abl.castAbility(cap, tar, primary, args);
        if(sync && tar.level().isClientSide()){
            PacketHandler.sendMessageCTS(new PlayerCastAbilityMessageCTS(ablId, primary, args));
        }
//        if(ability != null && cap.getSpirituality() >= Abilities.getAbilityById(key.getAbilityId()).getCostSpirituality()){
    }

    /*public void setEnabledAtLevel(String ablId, int sequenceLevel, boolean enabling, BeyonderCapability cap, LivingEntity target){
        applyToValidAbilities(abl -> abl.setEnabled(cap, target, enabling), ablId, sequenceLevel, true);
        /*for(Map.Entry<AbilityKey, Ability> entry: abilities.entrySet()){
            AbilityKey iKey = entry.getKey();
            if(iKey.isSameAbility(ablId) && iKey.getSequenceLevel() == sequenceLevel){
                entry.getValue().setEnabled(cap, target, enabling);
            }
        }
    }

    public void setEnabledAtLevelOrLower(String ablId, int sequenceLevel, boolean enabling, BeyonderCapability cap, LivingEntity target){
        applyToValidAbilities(abl -> abl.setEnabled(cap, target, enabling), ablId, sequenceLevel, false);
        /*for(Map.Entry<AbilityKey, Ability> entry: abilities.entrySet()){
            AbilityKey iKey = entry.getKey();
            if(iKey.isSameAbility(ablId) && iKey.getSequenceLevel() >= sequenceLevel){
                entry.getValue().setEnabled(cap, target, enabling);
            }
        }
    }*/

    public void updateClientAbilityInfo(Player player, List<AbilityInfo> abilities, int operation){
        if(player.level().isClientSide()) return;
        PacketHandler.sendMessageSTC(new AbilitySyncMessage(abilities, operation), player);
    }
    public void updateClientAbilityInfo(Player player, int operation){
        if(player.level().isClientSide()) return;
        PacketHandler.sendMessageSTC(new AbilitySyncMessage(abilities.values().stream().map(Ability::getAbilityInfo).toList(), operation), player);
    }

    public void updateSetClientAbilityInfo(Player player){
        if(player.level().isClientSide()) return;
        PacketHandler.sendMessageSTC(new AbilitySyncMessage(getAbilityInfos(), AbilitySyncMessage.SET), player);
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
        return getAllAbilities().stream().map(Ability::getAbilityInfo).toList();
    }

    public void saveNBTData(CompoundTag nbt){
        ListTag hotbar = new ListTag();
        for (UUID uuid : clientHotbar) {
            hotbar.add(NbtUtils.createUUID(uuid));
        }
        nbt.put("hotbar", hotbar);

        if(quickAbility != null) nbt.putUUID("quick", quickAbility);

        ListTag ablTag = new ListTag();
        for(Ability abl: abilities.values()){
            ablTag.add(abl.saveAbility());
        }
        nbt.put("abilityTags", ablTag);

        ListTag artifactsTag = new ListTag();
        for(ArtifactHolder artifact: artifacts.values()){
            artifactsTag.add(artifact.saveToTag(true));
        }
        nbt.put("artifacts", artifactsTag);

        nbt.put("disabledData", disabledManager.saveNbt());
    }

    public void loadNBTData(CompoundTag nbt, BeyonderCapability cap, LivingEntity target) {
        clientHotbar.clear();

        if (nbt.contains("abilityTags", Tag.TAG_LIST)) {
            ListTag ablTag = nbt.getList("abilityTags", Tag.TAG_COMPOUND);
            //for every ability data read in the tag...
            for (int i = 0; i < ablTag.size(); i++) {
                CompoundTag singleAblTag = ablTag.getCompound(i);
                ResourceLocation ablId = new ResourceLocation(singleAblTag.getString("AbilityId"));

                //since the charManager already added the intrinsic abilities we should have, try to find a match.
                Ability intrinsicToLoad = null;
                for(Ability abl: intrisicAbilitiesBuffer){
                    if(abl.is(ablId)) intrinsicToLoad = abl;
                }

                if(intrinsicToLoad != null){
                    //if the tag we're loading is an intrinsic ability, it was already added, so just load data.
                    intrinsicToLoad.loadTag(singleAblTag);
                    abilities.put(intrinsicToLoad.getInstanceId(), intrinsicToLoad);
                } else {
                    //otherwise, instantiate a new one.
                    Abilities.getFactory(ablId).ifPresent(factory -> {
                        Ability abl = factory.construct(
                                singleAblTag.contains("SequenceLevel") ? singleAblTag.getInt("SequenceLevel") : 9,
                                AbilityInfo.Group.INTRINSIC
                        );
                        abl.loadTag(singleAblTag);
                        //intrinsic abilities should have been added above. if we add an intrinsic ability here, that means that it was removed between mod versions.
                        if(abl.isOfGroup(AbilityInfo.Group.INTRINSIC)) return;
                        abilities.put(abl.getInstanceId(), abl);
                    });
                }
            }
        }

        intrisicAbilitiesBuffer.clear();
        //finally, regardless of how it was created or what it is, initialize them with the proper state data.
        for (Ability abl : abilities.values()) {
            abl.init();
        }

        //back to your scheduled programming...
        if (nbt.contains("hotbar", Tag.TAG_LIST)) {
            ListTag hotbarTag = nbt.getList("hotbar", Tag.TAG_INT_ARRAY); // NbtUtils.createUUID uses IntArray tags
            for (Tag tag : hotbarTag) clientHotbar.add(NbtUtils.loadUUID(tag));
        }

        if (nbt.hasUUID("quick")) quickAbility = nbt.getUUID("quick");
        else quickAbility = null;


        if (nbt.contains("artifacts", Tag.TAG_LIST)) {
            ListTag artifactTag = nbt.getList("artifacts", Tag.TAG_COMPOUND);
            for (Tag tag : artifactTag) {
                if (tag instanceof CompoundTag artTag) {
                    addArtifact(ArtifactHolder.loadFromTag(artTag), cap, target, false, false);
                }
            }
        }

        if (nbt.contains("disabledData", Tag.TAG_COMPOUND)) {
            disabledManager.loadNbt(nbt.getCompound("disabledData"), cap, target);
        }
    }

}
