package net.dinomine.potioneer.network.messages.abilityRelevant;

import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

//message sent between server and client to keep the ability hotbar info between world loads
public class PlayerSyncHotbarMessage {
    public ArrayList<UUID> hotbar;
    public UUID quick;

    public PlayerSyncHotbarMessage(ArrayList<UUID> hotbar, UUID quickAbility){
        this.hotbar = new ArrayList<>(hotbar);
        this.quick = quickAbility;
    }

    public static void encode(PlayerSyncHotbarMessage msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.hotbar.size());
        for(int i = 0; i < msg.hotbar.size(); i++){
            buffer.writeUUID(msg.hotbar.get(i));
        }
        buffer.writeUUID(msg.quick);
    }

    public static PlayerSyncHotbarMessage decode(FriendlyByteBuf buffer){
        int size = buffer.readInt();
        ArrayList<UUID> hotbar = new ArrayList<>();
        for(int i = 0; i < size; i++){
            hotbar.add(buffer.readUUID());
        }

        return new PlayerSyncHotbarMessage(hotbar, buffer.readUUID());
    }

    public static void handle(PlayerSyncHotbarMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(!context.getDirection().getReceptionSide().isClient()){
//                System.out.println("Receiving ability cast on server side");
                Player player = context.getSender();
                player.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
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
        ClientAbilitiesData.setQuickAbility(msg.quick);
    }
}
