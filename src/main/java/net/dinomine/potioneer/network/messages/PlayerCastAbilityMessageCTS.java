package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//message sent to the server when a client has authorized the casting of an ability
public class PlayerCastAbilityMessageCTS {
    public String ablId;

    public PlayerCastAbilityMessageCTS(String ablId){
        this.ablId = ablId;
    }

    public static void encode(PlayerCastAbilityMessageCTS msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.ablId.length());
        for (int i = 0; i < msg.ablId.length(); i++){
            buffer.writeChar(msg.ablId.charAt(i));
        }
    }

    public static PlayerCastAbilityMessageCTS decode(FriendlyByteBuf buffer){
        int size = buffer.readInt();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++){
            builder.append(buffer.readChar());
        }
        return new PlayerCastAbilityMessageCTS(builder.toString());
    }

    public static void handle(PlayerCastAbilityMessageCTS msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(!context.getDirection().getReceptionSide().isClient()){
//                System.out.println("Receiving ability cast on server side");
                Player player = context.getSender();
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                    cap.getAbilitiesManager().useAbility(cap, player, msg.ablId, false, false);
                });
            } else {
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientCastAbilityMessage.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }
}

@OnlyIn(Dist.CLIENT)
class ClientCastAbilityMessage
{
    public static void handlePacket(PlayerCastAbilityMessageCTS msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
//                ClientAbilitiesData.setAbilities(msg.list.stream().map(Ability::getInfo).toList());
        Player player = Minecraft.getInstance().player;
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getAbilitiesManager().useAbility(cap, player, msg.ablId, false, true);
        });
    }

}
