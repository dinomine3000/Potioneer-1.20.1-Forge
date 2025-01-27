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

//message sent to the server when a client has authorized the casting of an ability
public class PlayerCastAbilityMessageCTS {
    public int caretPos;

    public PlayerCastAbilityMessageCTS(int caretPos){
        this.caretPos = caretPos;
    }

    public static void encode(PlayerCastAbilityMessageCTS msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.caretPos);
    }

    public static PlayerCastAbilityMessageCTS decode(FriendlyByteBuf buffer){
        int id = buffer.readInt();
        return new PlayerCastAbilityMessageCTS(id);
    }

    public static void handle(PlayerCastAbilityMessageCTS msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(!context.getDirection().getReceptionSide().isClient()){
//                System.out.println("Receiving ability cast on server side");
                Player player = context.getSender();
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                    cap.getAbilitiesManager().useAbility(cap, player, msg.caretPos);
                });
            }
        });

        context.setPacketHandled(true);
    }

}
