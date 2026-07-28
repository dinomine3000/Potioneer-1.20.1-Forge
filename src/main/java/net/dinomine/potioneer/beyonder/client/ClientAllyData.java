package net.dinomine.potioneer.beyonder.client;

import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.AllySystem.AllyGroupSyncMessage;

import java.util.*;

public class ClientAllyData {
    private static List<String> groups = new ArrayList<>();
    private static Set<String> groupsPlayerIsIn = new HashSet<>();
    private static LinkedHashMap<UUID, String> currentDisplayPlayers = new LinkedHashMap<>();

    public static String getGroupNameMod(int idx){
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

    public static String getGroupName(int idx){
        if(groups.size() <= idx) return "";
        return groups.get(idx);
    }

    public static void requestPlayers(String groupName){
        PacketHandler.INSTANCE.sendToServer(AllyGroupSyncMessage.requestPlayersMessage(groupName));
    }

    public static void requestGroups(){
        PacketHandler.INSTANCE.sendToServer(AllyGroupSyncMessage.requestGroupsMessage());
    }

    public static void setGroups(List<String> allGroups, List<String> allyGroups){
        groups = allGroups;
        groupsPlayerIsIn = new HashSet<>(allyGroups);
    }

    public static boolean isPlayerInGroup(String group){return isPlayerInGroups(Collections.singleton(group));}
    public static boolean isPlayerInGroups(Collection<String> groupsToTest){
        return !Collections.disjoint(groupsToTest, groupsPlayerIsIn);
    }

    public static void setCurrentPlayers(LinkedHashMap<UUID, String> newPlayers){
        currentDisplayPlayers = newPlayers;
    }

    public static Map.Entry<UUID, String> getPlayerAtPosition(int idx) {
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
