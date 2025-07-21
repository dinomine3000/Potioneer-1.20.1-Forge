package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.util.misc.CharacteristicHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//sent from client to server on world join to request a STC sync
public class AdvancementFailMessageCTS {
    public int sequence;

    public AdvancementFailMessageCTS(int failedSequence){
        this.sequence = failedSequence;
    }

    public static void encode(AdvancementFailMessageCTS msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.sequence);
    }

    public static AdvancementFailMessageCTS decode(FriendlyByteBuf buffer){
        return new AdvancementFailMessageCTS(buffer.readInt());
    }

    public static void handle(AdvancementFailMessageCTS msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) context.getSender();
        //Server receives message
        context.enqueueWork(() -> {
            int sequence = msg.sequence;
            if(!player.isCreative()){
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> cap.setSanity(0));
            }
            CharacteristicHelper.addCharacteristicToLevel(sequence, player.level(), player, player.position(), player.getRandom());
        });

        context.setPacketHandled(true);
    }

}

