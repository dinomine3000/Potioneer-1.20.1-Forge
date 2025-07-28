package net.dinomine.potioneer.network.messages;

import com.mojang.authlib.GameProfile;
import net.dinomine.potioneer.beyonder.client.ClientAllyData;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.function.Supplier;

//called on world load and general syncing for advancing and updating the players pathway on both server and client
public class AllyGroupSyncMessage {
    List<String> groupNames;
    LinkedHashMap<UUID, String> players;
    String messageType;
    //S2C -> sends list of players from specific group
    private AllyGroupSyncMessage(LinkedHashMap<UUID, String> player, List<String> groupNames, String type){
        this.players = player;
        this.groupNames = groupNames;
        this.messageType = type;
    }

    //C2S requests
    public static AllyGroupSyncMessage requestGroups(){
        return new AllyGroupSyncMessage(new LinkedHashMap<>(), new ArrayList<>(), "");
    }

    public static AllyGroupSyncMessage requestPlayers(String groupName){
        return new AllyGroupSyncMessage(new LinkedHashMap<>(), new ArrayList<>(), groupName);
    }

    //S2C replies
    public static AllyGroupSyncMessage sendPlayerList(List<UUID> players, ServerLevel level){
        return new AllyGroupSyncMessage(getPlayerNamesFromUUIDs(level.getServer(), players), new ArrayList<>(), "players");
    }

    public static AllyGroupSyncMessage sendGroupList(List<String> groups){
        return new AllyGroupSyncMessage(new LinkedHashMap<>(), groups, "groups");
    }

    public static void encode(AllyGroupSyncMessage msg, FriendlyByteBuf buffer){
        //write message type
        BufferUtils.writeStringToBuffer(msg.messageType, buffer);

        //write player (UUID) list
        buffer.writeInt(msg.players.size());
        for(UUID id: msg.players.keySet()){
            buffer.writeUUID(id);
            BufferUtils.writeStringToBuffer(msg.players.get(id), buffer);
        }

        //write groups (String) list
        buffer.writeInt(msg.groupNames.size());
        for(int i = 0; i < msg.groupNames.size(); i++){
            BufferUtils.writeStringToBuffer(msg.groupNames.get(i), buffer);
        }

    }

    public static AllyGroupSyncMessage decode(FriendlyByteBuf buffer){
        //read message type
        String messageType = BufferUtils.readString(buffer);


        //read player (UUID) list
        LinkedHashMap<UUID, String> players = new LinkedHashMap<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++){
            UUID id = buffer.readUUID();
            String name = BufferUtils.readString(buffer);
            players.put(id, name);
        }

        //read groups (String) list
        List<String> groupNames = new ArrayList<>();
        size = buffer.readInt();
        for(int i = 0; i < size; i++){
            groupNames.add(BufferUtils.readString(buffer));
        }

        return new AllyGroupSyncMessage(players, groupNames, messageType);
    }

    public static void handle(AllyGroupSyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> AllyClientSync.handlePacket(msg, contextSupplier)));
            } else {
                ServerPlayer player = context.getSender();
                ServerLevel level = (ServerLevel) player.level();
                AllySystemSaveData data = AllySystemSaveData.from(level);
                if(msg.messageType.isEmpty()){
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            AllyGroupSyncMessage.sendGroupList(data.getGroupNamesAllyIsIn(player.getUUID())));
                } else {
                    List<UUID> players = data.getPlayersInGroup(msg.messageType);
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            AllyGroupSyncMessage.sendPlayerList(players, level));
                }
            }
        });

        context.setPacketHandled(true);
    }

    public static LinkedHashMap<UUID, String> getPlayerNamesFromUUIDs(MinecraftServer server, List<UUID> uuids) {
        LinkedHashMap<UUID, String> result = new LinkedHashMap<>();

        int i = 0;
        for (UUID uuid : uuids) {
            // First, try to get the player if they're online
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                result.put(uuid, player.getGameProfile().getName());
                continue;
            }

            // Fallback to the profile cache (for players who have joined before)
            Optional<GameProfile> cachedProfile = server.getProfileCache().get(uuid);
            cachedProfile.ifPresent(profile -> result.put(uuid, profile.getName()));

            if(cachedProfile.isEmpty()){
                result.put(uuid, "unknown" + i++);
            }
        }

        return result;
    }

}

@OnlyIn(Dist.CLIENT)
class AllyClientSync
{
    public static void handlePacket(AllyGroupSyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Player player = Minecraft.getInstance().player;

        if (player != null)
        {
            if(msg.messageType.equals("groups")){
                ClientAllyData.setGroups(msg.groupNames);
            } else if(msg.messageType.equals("players")){
                ClientAllyData.setCurrentPlayers(msg.players);
            } else {
                System.out.println("Invalid message type given: " + msg.messageType);
            }
        }
    }
}