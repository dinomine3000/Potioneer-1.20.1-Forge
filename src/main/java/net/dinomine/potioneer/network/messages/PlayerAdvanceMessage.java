package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerAdvanceMessage {
    public int id;
    public boolean sync;

    public PlayerAdvanceMessage(int pathwayId, boolean sync){
        this.id = pathwayId;
        this.sync = sync;
    }

    public static void enconde(PlayerAdvanceMessage msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.id);
        buffer.writeBoolean(msg.sync);
    }

    public static PlayerAdvanceMessage decode(FriendlyByteBuf buffer){
        return new PlayerAdvanceMessage(buffer.readInt(), buffer.readBoolean());
    }

    public static void handle(PlayerAdvanceMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();
        //potion advancement
        if(context.getDirection().getReceptionSide().isServer() && !msg.sync){
            context.enqueueWork(() -> {
               Player player = context.getSender();

               player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                   cap.advance(msg.id, player);
               });
            });
        }
        //general syncing
        else if(context.getDirection().getReceptionSide().isClient() && msg.sync){
            context.enqueueWork(() -> {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientThirstSyncMessage.handlePacket(msg, contextSupplier));
            });
        }

        context.setPacketHandled(true);
    }

}


@OnlyIn(Dist.CLIENT)
class ClientThirstSyncMessage
{
    public static void handlePacket(PlayerAdvanceMessage message, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Player player = Minecraft.getInstance().player;

        if (player != null)
        {
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap ->
            {
                cap.setPathway(message.id);
            });
        }
    }
}