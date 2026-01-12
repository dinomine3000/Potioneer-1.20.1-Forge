package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

//called frequently to update the client stats for the hud display
public class PlayerSTCHudStatsSync {
    public float spirituality;
    public int maxSpirituality;
    public int sanity;
    public int pathwayId;
    public float actingProgress;

    public PlayerSTCHudStatsSync(float spirituality, int maxSpirituality, int sanity, int pathwayId, float actingProgress) {
        this.spirituality = spirituality;
        this.maxSpirituality = maxSpirituality;
        this.sanity = sanity;
        this.pathwayId = pathwayId;
        this.actingProgress = actingProgress;
    }

    public static void encode(PlayerSTCHudStatsSync msg, FriendlyByteBuf buffer){
        buffer.writeFloat(msg.spirituality);
        buffer.writeInt(msg.maxSpirituality);
        buffer.writeInt(msg.sanity);
        buffer.writeInt(msg.pathwayId);
        buffer.writeFloat(msg.actingProgress);
    }

    public static PlayerSTCHudStatsSync decode(FriendlyByteBuf buffer){
        float spir = buffer.readFloat();
        int max = buffer.readInt();
        int san = buffer.readInt();
        int id = buffer.readInt();
        float acting = buffer.readFloat();
        return new PlayerSTCHudStatsSync(spir, max, san, id, acting);
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
        ClientStatsData.setActing(msg.actingProgress);
//        ClientStatsData.setLuck(msg.luck, msg.minLuck, msg.maxLuck);

        Minecraft.getInstance().player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.setSpirituality(msg.spirituality);
            cap.setMaxSpirituality(msg.maxSpirituality);
            cap.setSanity(msg.sanity);
        });
    }
}