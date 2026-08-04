package net.dinomine.potioneer.network.messages.AllySystem;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import net.dinomine.potioneer.beyonder.client.ClientAllyData;
import net.dinomine.potioneer.config.PotioneerGameplayConfig;
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


//TODO evaluate packet sizes on all message
//synchronizes ally group data to client
public class AllyGroupSyncMessage {
    List<String> groupNamesToSend;
    List<String> groupNamesPlayerIsIn;
    LinkedHashMap<UUID, String> playersInGroup;
    String messageType;

    //S2C -> sends list of players from specific group
    private AllyGroupSyncMessage(LinkedHashMap<UUID, String> player, List<String> groupNamesToSend, List<String> groupNamesPlayerIsIn, String type){
        this.playersInGroup = player;
        this.groupNamesToSend = groupNamesToSend;
        this.groupNamesPlayerIsIn = groupNamesPlayerIsIn;
        this.messageType = type;
    }

    //C2S requests
    public static AllyGroupSyncMessage requestGroupsMessage(){
        return new AllyGroupSyncMessage(new LinkedHashMap<>(), new ArrayList<>(), new ArrayList<>(), "");
    }

    public static AllyGroupSyncMessage requestPlayersMessage(String groupName){
        return new AllyGroupSyncMessage(new LinkedHashMap<>(), new ArrayList<>(), new ArrayList<>(), groupName);
    }

    //S2C replies
    private static AllyGroupSyncMessage sendPlayerList(List<UUID> players, ServerLevel level){
        return new AllyGroupSyncMessage(getPlayerNamesFromUUIDs(level.getServer(), players), new ArrayList<>(), new ArrayList<>(), "players");
    }

    private static AllyGroupSyncMessage sendGroupList(List<String> groupsToSend, List<String> groupsPlayerIsIn){
        return new AllyGroupSyncMessage(new LinkedHashMap<>(), groupsToSend, groupsPlayerIsIn, "groups");
    }

    public static void sendGroupList(ServerPlayer player){
        ServerLevel level = (ServerLevel) player.level();
        AllySystemSaveData data = AllySystemSaveData.from(level);
        sendGroupsToPlayer(data, player);
    }

    public static void encode(AllyGroupSyncMessage msg, FriendlyByteBuf buffer){
        //write message type
        BufferUtils.writeStringToBuffer(msg.messageType, buffer);

        //write player (UUID) list
        //Note: I do this manually here because i want to ensure they have the same order. If it turns out theres no need for that, then come back.
        buffer.writeMap(msg.playersInGroup, FriendlyByteBuf::writeUUID, (buf, name) -> BufferUtils.writeStringToBuffer(name, buf));
        buffer.writeCollection(msg.groupNamesToSend, ((buf, s) -> BufferUtils.writeStringToBuffer(s, buf)));
        buffer.writeCollection(msg.groupNamesPlayerIsIn, ((buf, s) -> BufferUtils.writeStringToBuffer(s, buf)));

    }

    public static AllyGroupSyncMessage decode(FriendlyByteBuf buffer){
        //read message type
        String messageType = BufferUtils.readString(buffer);

        LinkedHashMap<UUID, String> players = buffer.readMap(Maps::newLinkedHashMapWithExpectedSize, FriendlyByteBuf::readUUID, BufferUtils::readString);
        List<String> groupNames = buffer.readList(BufferUtils::readString);
        List<String> groupNamesPlayerIsIn = buffer.readList(BufferUtils::readString);
        return new AllyGroupSyncMessage(players, groupNames, groupNamesPlayerIsIn, messageType);
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
                    sendGroupsToPlayer(data, player);
                } else {
                    List<UUID> players = data.getPlayersInGroup(msg.messageType);
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            AllyGroupSyncMessage.sendPlayerList(players, level));
                }
            }
        });

        context.setPacketHandled(true);
    }

    public static void sendGroupsToPlayer(AllySystemSaveData data, ServerPlayer player){
        if(PotioneerGameplayConfig.PUBLIC_GROUPS.get()){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    AllyGroupSyncMessage.sendGroupList(
                            data.getGroups(),
                            data.getGroupNamesPlayerIsIn(player.getUUID())
                    ));
        } else {
            List<String> allyGroups = data.getGroupNamesPlayerIsIn(player.getUUID());
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    AllyGroupSyncMessage.sendGroupList(allyGroups, allyGroups));
        }
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
                ClientAllyData.setGroups(msg.groupNamesToSend, msg.groupNamesPlayerIsIn);
            } else if(msg.messageType.equals("players")){
                ClientAllyData.setCurrentPlayers(msg.playersInGroup);
            } else {
                System.out.println("Invalid message type given: " + msg.messageType);
            }
        }
    }
}