package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

//called frequently to update the client stats for the hud display
public class PlayerSTCHudStatsSync {
    public float spirituality;
    public int maxSpirituality;
    public int sanity;
    public int pathwayId;
    public Map<String, Boolean> enabledList;

    public PlayerSTCHudStatsSync(float spirituality, int maxSpirituality, int sanity, int pathwayId, Map<String, Boolean> enabled) {
        this.spirituality = spirituality;
        this.maxSpirituality = maxSpirituality;
        this.sanity = sanity;
        this.pathwayId = pathwayId;
        this.enabledList = enabled;
    }

    public static void encode(PlayerSTCHudStatsSync msg, FriendlyByteBuf buffer){
        buffer.writeFloat(msg.spirituality);
        buffer.writeInt(msg.maxSpirituality);
        buffer.writeInt(msg.sanity);
        buffer.writeInt(msg.pathwayId);
        buffer.writeInt(msg.enabledList.size());
        for(Map.Entry<String, Boolean> entry : msg.enabledList.entrySet()){
            String id = entry.getKey();
            buffer.writeInt(id.length());
            for(int i = 0; i < id.length(); i++){
                buffer.writeChar(id.charAt(i));
            }
            buffer.writeBoolean(entry.getValue());
        }
    }

    public static PlayerSTCHudStatsSync decode(FriendlyByteBuf buffer){
        float spir = buffer.readFloat();
        int max = buffer.readInt();
        int san = buffer.readInt();
        int id = buffer.readInt();
        HashMap<String, Boolean> res = new HashMap<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++){
            int idLen = buffer.readInt();
            StringBuilder idBuilder = new StringBuilder();
            for(int j = 0; j < idLen; j++){
                idBuilder.append(buffer.readChar());
            }
            res.put(idBuilder.toString(), buffer.readBoolean());
        }
        return new PlayerSTCHudStatsSync(spir, max, san, id, res);
    }

    public static void handle(PlayerSTCHudStatsSync msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHudStatsSyncMessage.handlePacket(msg, contextSupplier)));
            }
            /*else {
                System.out.println("Receiving on server side");
                Player player = context.getSender();
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                    cap.advance(msg.id, player, false);
                });
            }*/
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientHudStatsSyncMessage
{
    public static void handlePacket(PlayerSTCHudStatsSync msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        ClientStatsData.setSpirituality(msg.spirituality);
        ClientStatsData.setMaxSpirituality(msg.maxSpirituality);
        ClientStatsData.setSanity(msg.sanity);
        ClientStatsData.setPathwayId(msg.pathwayId);

        ClientAbilitiesData.setEnabledList(msg.enabledList);
        contextSupplier.get().getSender().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getAbilitiesManager().setMap(cap.getAbilitiesManager().enabledDisabled, msg.enabledList);
            cap.setSpirituality(msg.spirituality);
            cap.setMaxSpirituality(msg.maxSpirituality);
            cap.setSanity(msg.sanity);
        });
    }
}