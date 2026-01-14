package net.dinomine.potioneer.network.messages.abilityRelevant;

import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
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
    public ArrayList<AbilityKey> hotbar;
    public AbilityKey quick;

    public PlayerSyncHotbarMessage(ArrayList<AbilityKey> hotbar, AbilityKey quickAbility){
        this.hotbar = new ArrayList<>(hotbar);
        this.quick = quickAbility;
    }

    public static void encode(PlayerSyncHotbarMessage msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.hotbar.size());
        for(int i = 0; i < msg.hotbar.size(); i++){
            msg.hotbar.get(i).writeToBuffer(buffer);
        }
        msg.quick.writeToBuffer(buffer);
    }

    public static PlayerSyncHotbarMessage decode(FriendlyByteBuf buffer){
        int size = buffer.readInt();
        ArrayList<AbilityKey> hotbar = new ArrayList<>();
        for(int i = 0; i < size; i++){
            hotbar.add(AbilityKey.readFromBuffer(buffer));
        }

        return new PlayerSyncHotbarMessage(hotbar, AbilityKey.readFromBuffer(buffer));
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
        ClientAbilitiesData.setQuickAbility(msg.quick);
    }
}
