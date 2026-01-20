package net.dinomine.potioneer.network.messages.advancement;

import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//sent from server to client to begin the advancement
public class BeginAdvancementMessage {
    public int pathwaySequenceId;
    public int difficulty;

    public BeginAdvancementMessage(int targetPathwaySequenceId, int addedDifficulty){
        this.pathwaySequenceId = targetPathwaySequenceId;
        this.difficulty = addedDifficulty;
    }

    public static void encode(BeginAdvancementMessage msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.pathwaySequenceId);
        buffer.writeInt(msg.difficulty);
    }

    public static BeginAdvancementMessage decode(FriendlyByteBuf buffer){
        return new BeginAdvancementMessage(buffer.readInt(), buffer.readInt());
    }

    public static void handle(BeginAdvancementMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();
        //potion advancement
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientBeginAdvancementHandler.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }

}


@OnlyIn(Dist.CLIENT)
class ClientBeginAdvancementHandler
{
    public static void handlePacket(BeginAdvancementMessage msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Player player = Minecraft.getInstance().player;

        if (player != null)
        {
            ClientStatsData.attemptAdvancement(msg.pathwaySequenceId, msg.difficulty);
        }
    }
}