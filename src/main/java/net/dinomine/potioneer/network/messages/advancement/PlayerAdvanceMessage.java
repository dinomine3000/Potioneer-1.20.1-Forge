package net.dinomine.potioneer.network.messages.advancement;

import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//message to synchronize the client to have the same characteristics as the server.
//the server has final say in the advancement.
// S2C -> the client saves the characteristics
// C2S -> client tells the server to consume that characteristic
public class PlayerAdvanceMessage {
    public List<Integer> characteristics;

    public PlayerAdvanceMessage(List<Integer> characteristicList){
        this.characteristics = characteristicList;
    }

    public static void encode(PlayerAdvanceMessage msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.characteristics.size());
        for(Integer charac: msg.characteristics){
            buffer.writeInt(charac);
        }
    }

    public static PlayerAdvanceMessage decode(FriendlyByteBuf buffer){
        ArrayList<Integer> characteristics = new ArrayList<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++){
            characteristics.add(buffer.readInt());
        }
        return new PlayerAdvanceMessage(characteristics);
    }

    public static void handle(PlayerAdvanceMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSyncMessage.handlePacket(msg, contextSupplier)));
            } else {
                Player player = context.getSender();
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                    for(Integer charac: msg.characteristics){
                        cap.advance(charac, false);
                        Pathways.getPathwayBySequenceId(charac).applyRitualEffects(player, charac%10);
                    }
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
                cap.getCharacteristicManager().setCharacteristicsOnClient(player, msg.characteristics);
            });
        }
    }
}