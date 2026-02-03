package net.dinomine.potioneer.savedata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class AllySystemSaveData extends SavedData {

    private Map<String, AllyGroup> groups;

    public boolean isPlayerAllyOf(UUID testPlayer, UUID playerTarget){
        if(testPlayer == null || playerTarget == null) return false;
        for (AllyGroup group: groups.values()){
            if(group.isPlayerInGroup(testPlayer) && group.isPlayerInGroup(playerTarget)) return true;
        }
        return false;
    }

    public List<AllyGroup> getGroupsPlayerIsIn(UUID player){
        return groups.values().stream().filter(group -> group.isPlayerInGroup(player)).toList();
    }

    public List<String> getGroupNamesAllyIsIn(UUID player){
        return groups.keySet().stream().filter(key -> groups.get(key).isPlayerInGroup(player)).toList();
    }

    public List<UUID> getAlliesOf(UUID player){
        List<UUID> res = new ArrayList<>();
        for(AllyGroup group: groups.values()){
            if(group.isPlayerInGroup(player)){
                res.addAll(group.playerList);
            }
        }
        return res;
    }

    public List<UUID> getPlayersInGroup(String groupName){
        if(!groups.containsKey(groupName)) return new ArrayList<>();
        return groups.get(groupName).playerList;
    }

    public boolean removePlayer(String groupName, UUID player){
        if(!groups.containsKey(groupName)) return false;
        boolean res = groups.get(groupName).removePlayer(player);
        if(groups.get(groupName).playerList.isEmpty()){
            System.out.println("Removing group " + groupName + " from memory.");
            groups.remove(groupName);
        }
        setDirty();
        return res;
    }

    public boolean tryAddPlayer(String groupName, UUID player, String testPassword){
        if(!groups.containsKey(groupName)) return false;
        setDirty();
        return groups.get(groupName).addPlayer(player, testPassword);
    }

    public boolean createGroup(String groupName, String password, UUID creator){
        if(!isNewGroupNameValid(groupName)) return false;
        if(password.length() < 3) return false;
        groups.put(groupName, new AllyGroup(creator, groupName, password));
        setDirty();
        return true;
    }

    public boolean isNewGroupNameValid(String testName) {return !groups.containsKey(testName);}

    public AllySystemSaveData(ServerLevel level){
        groups = new HashMap<>();
    }

    private AllySystemSaveData(Map<String, AllyGroup> groups){
        this.groups = groups;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        ListTag allyGroups = new ListTag();
        for(AllyGroup entry: this.groups.values()){
            allyGroups.add(entry.saveToTag());
        }
        compoundTag.put("groups", allyGroups);
        return compoundTag;
    }

    public static AllySystemSaveData loadAllies(CompoundTag nbt, Level level){
        Map<String, AllyGroup> allyGroups = new HashMap<>();
        ListTag listTag = nbt.getList("groups", Tag.TAG_COMPOUND);

        for (Tag tag : listTag) {
            if(tag instanceof CompoundTag compoundTag){
                AllyGroup group = AllyGroup.loadFromTag(compoundTag);
                allyGroups.put(group.getName(), group);
            }
        }
        return new AllySystemSaveData(allyGroups);
    }

    public static AllySystemSaveData from(ServerLevel level){
        return level.getServer().overworld().getDataStorage().computeIfAbsent((tag) -> loadAllies(tag, level),
                () -> new AllySystemSaveData(level), "potioneer_ally_groups");
    }

    public List<String> getGroups() {
        return groups.keySet().stream().toList();
    }

    public boolean areEntitiesAllies(LivingEntity target, LivingEntity ent) {
        if(!(target instanceof Player playerTarget) || !(ent instanceof Player playerEntity)) return false;
        return isPlayerAllyOf(playerEntity.getUUID(), playerEntity.getUUID());
    }

    public static class AllyGroup{
        private final List<UUID> playerList;
        private final String name;
        private final String password;

        public AllyGroup(UUID creator, String name, String password){
            this.playerList = new ArrayList<>();
            this.playerList.add(creator);
            this.name = name;
            this.password = password;
        }

        public AllyGroup(UUID creator, String password){
            this(creator, "", password);
        }

        private AllyGroup(List<UUID> player, String name, String password){
            this.playerList = player;
            this.name = name;
            this.password = password;
        }

        public String getName(){
            return this.name;
        }

        public boolean addPlayer(UUID player, String testPassword){
            if(!testPassword.equals(this.password)) return false;
            return playerList.add(player);
        }

        public boolean isPlayerInGroup(UUID player){
            return playerList.contains(player);
        }

        public boolean removePlayer(UUID player){
            return playerList.remove(player);
        }

        public CompoundTag saveToTag(){
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            for (UUID id : playerList) {
                list.add(NbtUtils.createUUID(id));   // each UUID → CompoundTag
            }
            tag.put("ids", list);
            tag.putString("name", name);
            tag.putString("password", password);
            return tag;
        }

        public static AllyGroup loadFromTag(CompoundTag tag) {
            List<UUID> result = new ArrayList<>();
            String name = tag.getString("name");
            if (tag.contains("ids", Tag.TAG_LIST)) {
                ListTag list = tag.getList("ids", Tag.TAG_INT_ARRAY);
                for (Tag t : list) {
                    try {
                        UUID id = NbtUtils.loadUUID(t);
                        result.add(id);
                        System.out.println("For group " + name + " loaded id: " + id);
                    } catch (Exception e) {
                        System.out.println("Error reading id: " + e.getMessage());
                    }
                }
            }
            return new AllyGroup(result, name, tag.getString("password"));
        }
    }


}
