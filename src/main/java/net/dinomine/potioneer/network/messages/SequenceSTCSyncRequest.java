package net.dinomine.potioneer.network.messages;

import com.mojang.authlib.GameProfile;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

//sent from client to server on world join to request a STC sync
public class SequenceSTCSyncRequest {

    public SequenceSTCSyncRequest(){
    }

    public static void encode(SequenceSTCSyncRequest msg, FriendlyByteBuf buffer){
    }

    public static SequenceSTCSyncRequest decode(FriendlyByteBuf buffer){
        return new SequenceSTCSyncRequest();
    }

    public static void handle(SequenceSTCSyncRequest msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) context.getSender();
        //Server receives message
        context.enqueueWork(() -> {
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                if(!context.getDirection().getReceptionSide().isClient()){
                    cap.syncSequenceData(player);
                    Map<UUID, GameProfileCache.GameProfileInfo> profileMap = player.level().getServer().getProfileCache().profilesByUUID;
                    Map<UUID, String> nameMap = new HashMap<>();
                    for(UUID id: profileMap.keySet()){
                        nameMap.put(id, profileMap.get(id).getProfile().getName());
                    }
                    PacketHandler.sendMessageSTC(new PlayerNameSyncMessage(nameMap), player);
                }
            });
        });

        context.setPacketHandled(true);
    }

}

