package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAbilityCooldownSTC;
import net.dinomine.potioneer.network.messages.PlayerCastAbilityMessageCTS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerAbilitiesManager {
    private ArrayList<Ability> pathwayActives = new ArrayList<Ability>();
    private Map<String, Integer> activeCooldowns = new HashMap<>();
    public Map<String, Boolean> enabledDisabled = new HashMap<>();

    private ArrayList<Ability> pathwayPassives = new ArrayList<Ability>();
    private ArrayList<Ability> otherPassives = new ArrayList<Ability>();

    public ArrayList<Integer> clientHotbar = new ArrayList<>();
    public int quickAbility = -1;

    public void copyFrom(PlayerAbilitiesManager mng){
        this.enabledDisabled = new HashMap<>(mng.enabledDisabled);
        this.clientHotbar = mng.clientHotbar;
        this.quickAbility = mng.quickAbility;
    }

    public void onTick(EntityBeyonderManager cap, LivingEntity target){
        if(!pathwayPassives.isEmpty()){
            pathwayPassives.forEach(ability -> {
                ability.passive(cap, target);
            });
        }
        if(!otherPassives.isEmpty()){
            otherPassives.forEach(ability -> {
                ability.passive(cap, target);
            });
        }

        for (Ability pathwayActive : pathwayActives) {
            String ablId = pathwayActive.getInfo().descId();
            if (activeCooldowns.get(ablId) > 0) activeCooldowns.put(ablId, activeCooldowns.get(ablId) - 1);
        }
    }

    public void addPassiveAbility(Ability ability, boolean pathway){
        if(pathway) pathwayPassives.add(ability);
        else otherPassives.add(ability);
    }

    public void clear(boolean pathway, EntityBeyonderManager cap, LivingEntity target){
        if(pathway) {
            //condition on the passives list since its generally smaller than actives list
            //this way it only calls the deactivate once
            //as of right now, every passive ability is in actives, so you could ditch this first forEach
            //however, if in the future an ability is added that is completely passive, this will be necessary
            //TODO: remove this forEach when mod is complete if possible
            pathwayPassives.forEach(ability -> {if(!pathwayActives.contains(ability)) ability.deactivate(cap, target);});
            pathwayPassives = new ArrayList<>();
            pathwayActives.forEach(ability -> ability.deactivate(cap, target));
            pathwayActives = new ArrayList<>();
        }
        else {
            otherPassives.forEach(ability -> ability.deactivate(cap, target));
            otherPassives = new ArrayList<>();
        }
    }

    public void useAbility(EntityBeyonderManager cap, LivingEntity tar, int caret){
        if(pathwayActives.isEmpty()){
            System.out.println("active abilities are non existent");
            return;
        }
        if(tar instanceof Player player){
            if(tar.level().isClientSide()){
                PacketHandler.INSTANCE.sendToServer(new PlayerCastAbilityMessageCTS(caret));
                pathwayActives.get(caret).active(cap, tar);
            } else {
//                System.out.println(cap.getEffectsManager());
//                System.out.println(pathwayActives);
//                System.out.println(pathwayActives.get(caret));
                String ablId = pathwayActives.get(caret).getInfo().descId();
                if(activeCooldowns.get(ablId) == 0){
                    if(pathwayActives.get(caret).active(cap, tar)){
                        putOnCooldown(player, caret);
                    }
                } else {
                    System.out.println("Tried to activate ability on cooldown");
                }
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

        if(newMap.size() != pathwayActives.size()){
            System.out.println("WARNING: received list is NOT the same size as the pathwayActives list.\nProceed with caution!!!");
//            System.out.println("List size: " + list.size());
//            System.out.println("EnabledDisabled list size: " + enabledDisabled.size());
//            System.out.println("pathwayActives list size: " + pathwayActives.size());
        }
        for (int i = 0; i < Math.min(enabledDisabled.size(), pathwayActives.size()); i++) {
            if(pathwayActives.get(i).isActive) enabledDisabled.put(pathwayActives.get(i).getInfo().descId(), true);
        }
    }

    public void setEnabled(Ability abl, boolean bol, EntityBeyonderManager cap, LivingEntity target){
        if(!pathwayActives.contains(abl)) return;
        boolean prevStatus = enabledDisabled.get(abl.getInfo().descId());
        if(prevStatus && !bol){
            abl.deactivate(cap, target);
        } else if(!prevStatus && bol){
            abl.activate(cap, target);
        }
        enabledDisabled.put(abl.getInfo().descId(), bol);
    }

    public boolean isEnabled(Ability abl){
        if(enabledDisabled.isEmpty() || !pathwayActives.contains(abl)) return false;
        return enabledDisabled.get(abl.getInfo().descId());
    }

    public int getCaretForAbility(Ability abl){
        return pathwayActives.indexOf(abl);
    }

    public void putOnCooldown(Player player, int caret){
        putOnCooldown(player, caret, pathwayActives.get(caret).getCooldown());
    }

    public void putOnCooldown(Player player, int caret, int cd){
        putOnCooldown(player, caret, cd, pathwayActives.get(caret).getInfo().maxCooldown());
    }

    public void putOnCooldown(Player player, int caret, int cd, int maxCd){
        activeCooldowns.put(pathwayActives.get(caret).getInfo().descId(), cd);
        if(cd != 0){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAbilityCooldownSTC(pathwayActives.get(caret).getInfo().descId(), cd, maxCd));
        }
    }

    public void updateClientCooldownInfo(ServerPlayer player){
        for (Ability pathwayActive : pathwayActives) {
            String desc = pathwayActive.getInfo().descId();
            if (activeCooldowns.get(desc) != 0) {
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                        new PlayerAbilityCooldownSTC(desc, activeCooldowns.get(desc), pathwayActive.getInfo().maxCooldown()));
            }
        }
    }

    public void setPathwayActives(ArrayList<Ability> abilities){
//        System.out.println("Active abilities updates. Size is: " + abilities.size());
        pathwayActives = new ArrayList<>(abilities);
        this.activeCooldowns = abilities.stream()
                .collect(Collectors.toMap(a -> a.getInfo().descId(), a -> 0));

        this.enabledDisabled = abilities.stream()
                .collect(Collectors.toMap(a -> a.getInfo().descId(), a -> true));
    }

    public void onAcquireAbilities(EntityBeyonderManager cap, LivingEntity target){
        //TODO: only going through actives. if you want for passives, youll need to add it
        // Check if its enabled for when the player loads into the world -> dont activate extended reach if the ability was previously disabled
        //its loading the enabledDisabled list before calling this onAcquire function when loading data
        for (Ability ability : pathwayActives) {
            if (enabledDisabled.get(ability.getInfo().descId())) ability.onAcquire(cap, target);
        }
    }

    public ArrayList<Ability> getPathwayActives() {
        return pathwayActives;
    }

    public void setPathwayPassives(ArrayList<Ability> abilities){
        //System.out.println("Passive abilities updates. Size is: " + abilities.size());
        pathwayPassives = abilities;
    }

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
            hotbar.putInt(String.valueOf(j), clientHotbar.get(j));
        }
        hotbar.putInt("quick", quickAbility);
        nbt.put("hotbar", hotbar);
    }

    public void loadEnabledListFromTag(CompoundTag nbt, EntityBeyonderManager cap, LivingEntity target){
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

    public void loadNBTData(CompoundTag nbt, LivingEntity target){
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
                clientHotbar.add(hot.getInt(String.valueOf(i)));
            }
        }
        quickAbility = hot.getInt("quick");
    }

}
