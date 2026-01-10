package net.dinomine.potioneer.network.messages.advancement;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//called on world load and general syncing for advancing and updating the players pathway on both server and client
public class PlayerAdvanceMessage {
    public int id;

    public PlayerAdvanceMessage(int pathwayId){
        this.id = pathwayId;
    }

    public static void encode(PlayerAdvanceMessage msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.id);
    }

    public static PlayerAdvanceMessage decode(FriendlyByteBuf buffer){
        int id = buffer.readInt();
        boolean adv = buffer.readBoolean();
        return new PlayerAdvanceMessage(id);
    }

    public static void handle(PlayerAdvanceMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSyncMessage.handlePacket(msg, contextSupplier)));
            } else {
                System.out.println("Receiving on server side. Id = " + msg.id);
                Player player = context.getSender();
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                    cap.consumeCharacteristic(msg.id);
                });
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientSyncMessage
{
    public static void handlePacket(PlayerAdvanceMessage msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Player player = Minecraft.getInstance().player;

        if (player != null)
        {
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                System.out.println("Handling sequence syncing on client side...");
                cap.consumeCharacteristic(msg.id);
            });
        }
    }
}