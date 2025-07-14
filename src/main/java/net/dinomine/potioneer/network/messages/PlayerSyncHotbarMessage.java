package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

//message sent between server and client to keep the ability hotbar info between world loads
public class PlayerSyncHotbarMessage {
    public ArrayList<String> hotbar;
    public String quick;

    public PlayerSyncHotbarMessage(ArrayList<String> hotbar, String quickAbility){
        this.hotbar = new ArrayList<>(hotbar);
        this.quick = quickAbility;
    }

    public static void encode(PlayerSyncHotbarMessage msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.hotbar.size());
        for(int i = 0; i < msg.hotbar.size(); i++){
            String id = msg.hotbar.get(i);
            buffer.writeInt(id.length());
            for(int j = 0; j < id.length(); j++){
                buffer.writeChar(id.charAt(j));
            }
        }
        buffer.writeInt(msg.quick.length());
        for(int i = 0; i < msg.quick.length(); i++){
            buffer.writeChar(msg.quick.charAt(i));
        }
    }

    public static PlayerSyncHotbarMessage decode(FriendlyByteBuf buffer){
        int size = buffer.readInt();
        ArrayList<String> hotbar = new ArrayList<>();
        StringBuilder builder;
        for(int i = 0; i < size; i++){
            builder = new StringBuilder();
            int iterations = buffer.readInt();
            for(int j = 0; j < iterations; j++){
                builder.append(buffer.readChar());
            }
            hotbar.add(builder.toString());
        }
        int quickSize = buffer.readInt();
        builder = new StringBuilder();
        for(int i = 0; i < quickSize; i++){
            builder.append(buffer.readChar());
        }
        return new PlayerSyncHotbarMessage(hotbar, builder.toString());
    }

    public static void handle(PlayerSyncHotbarMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(!context.getDirection().getReceptionSide().isClient()){
//                System.out.println("Receiving ability cast on server side");
                Player player = context.getSender();
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                    cap.getAbilitiesManager().clientHotbar = msg.hotbar;
                    cap.getAbilitiesManager().quickAbility = msg.quick;
                });
            } else {
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHotbarSyncMessage.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientHotbarSyncMessage
{
    public static void handlePacket(PlayerSyncHotbarMessage msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        ClientAbilitiesData.setHotbar(msg.hotbar);
        ClientAbilitiesData.setQuickAbilityCaret(msg.quick);
    }
}
