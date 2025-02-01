package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.function.Supplier;

//sent from client to server on world join to request a STC sync
public class SequenceSTCSyncRequest {

    public SequenceSTCSyncRequest(){
    }

    public static void encode(SequenceSTCSyncRequest msg, FriendlyByteBuf buffer){
    }

    public static SequenceSTCSyncRequest decode(FriendlyByteBuf buffer){
        return new SequenceSTCSyncRequest();
    }

    public static void handle(SequenceSTCSyncRequest msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) context.getSender();
        //Server receives message
        context.enqueueWork(() -> {
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                if(!context.getDirection().getReceptionSide().isClient()){
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new PlayerAdvanceMessage(cap.getPathwayId(), false));
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new PlayerAbilityInfoSyncSTC(cap.getAbilitiesManager().getPathwayActives().stream().map(Ability::getInfo).toList(), false));
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new PlayerSyncHotbarMessage(cap.getAbilitiesManager().clientHotbar));
                }
            });
        });

        context.setPacketHandled(true);
    }

}

