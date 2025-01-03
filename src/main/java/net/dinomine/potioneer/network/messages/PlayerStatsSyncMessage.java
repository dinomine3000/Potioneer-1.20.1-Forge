package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.player.BeyonderStats;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//called frequently to update the client stats for the hud display
public class PlayerStatsSyncMessage {
    public float miningSpeed;

    public PlayerStatsSyncMessage(float miningSpeed) {
        this.miningSpeed = miningSpeed;
    }

    public static void encode(PlayerStatsSyncMessage msg, FriendlyByteBuf buffer){
        buffer.writeFloat(msg.miningSpeed);
    }

    public static PlayerStatsSyncMessage decode(FriendlyByteBuf buffer){
        float miningSpeed = buffer.readFloat();
        return new PlayerStatsSyncMessage(miningSpeed);
    }

    public static void handle(PlayerStatsSyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientStatsSyncMessage.handlePacket(msg, contextSupplier)));
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
class ClientStatsSyncMessage
{
    public static void handlePacket(PlayerStatsSyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Player player = Minecraft.getInstance().player;
        if(player != null){
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.getBeyonderStats().setMiningSpeed(msg.miningSpeed);
            });
        }
    }
}
