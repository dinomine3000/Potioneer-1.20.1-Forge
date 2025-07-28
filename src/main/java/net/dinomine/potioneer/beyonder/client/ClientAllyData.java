package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.AllyGroupSyncMessage;

import java.util.*;

public class ClientAllyData {
    private static List<String> groups = new ArrayList<>();
    private static LinkedHashMap<UUID, String> currentDisplayPlayers = new LinkedHashMap<>();

    public static String getGroupNameById(int idx){
        if(groups.isEmpty()) return "";
        return groups.get(idx%groups.size());
    }

    public static int getPlayerNumber(){
        return Math.min(currentDisplayPlayers.size(), 3);
    }

    public static int getTotalPlayerNumber(){
        return currentDisplayPlayers.size();
    }

    public static int getTotalGroupSize(){
        return groups.size();
    }

    public static int getGroupsSize(){
        return Math.min(groups.size(), 3);
    }

    public static String getGroupNameForRender(int idx){
        if(groups.size() <= idx) return "";
        return groups.get(idx);
    }

    public static void requestPlayers(String groupName){
        PacketHandler.INSTANCE.sendToServer(AllyGroupSyncMessage.requestPlayers(groupName));
    }

    public static void requestGroups(){
        PacketHandler.INSTANCE.sendToServer(AllyGroupSyncMessage.requestGroups());
    }

    public static void setGroups(List<String> newGroups){
        System.out.println("Set groups to: " + newGroups);
        groups = newGroups;
    }

    public static void setCurrentPlayers(LinkedHashMap<UUID, String> newPlayers){
        currentDisplayPlayers = newPlayers;
    }

    public static Map.Entry<UUID, String> getEntryAt(int idx) {
        int i = 0;
        for (Map.Entry<UUID, String> entry : currentDisplayPlayers.entrySet()) {
            if (i == idx) {
                return entry;
            }
            i++;
        }
        return null;
    }

    public static void clearPlayers() {
        currentDisplayPlayers = new LinkedHashMap<>();
    }
}
