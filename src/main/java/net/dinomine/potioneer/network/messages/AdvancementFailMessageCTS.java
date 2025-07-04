package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

//sent from client to server on world join to request a STC sync
public class AdvancementFailMessageCTS {

    public AdvancementFailMessageCTS(){
    }

    public static void encode(AdvancementFailMessageCTS msg, FriendlyByteBuf buffer){
    }

    public static AdvancementFailMessageCTS decode(FriendlyByteBuf buffer){
        return new AdvancementFailMessageCTS();
    }

    public static void handle(AdvancementFailMessageCTS msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) context.getSender();
        //Server receives message
        context.enqueueWork(() -> {
            if(!player.isCreative()) player.kill();
        });

        context.setPacketHandled(true);
    }

}

