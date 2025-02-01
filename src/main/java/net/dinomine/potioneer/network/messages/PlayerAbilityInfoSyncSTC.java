package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//message sent to client when advancing, to synchronize the abilities available to the player
//it assumes the server data is the true data and sets the data on client side to that
public class PlayerAbilityInfoSyncSTC {
    public ArrayList<AbilityInfo> list;
    public boolean changing;

    public PlayerAbilityInfoSyncSTC(ArrayList<AbilityInfo> list, boolean changing){
        this.list = list;
        this.changing = changing;
    }

    public PlayerAbilityInfoSyncSTC(List<AbilityInfo> list, boolean changing){
        this(new ArrayList<>(list), changing);
    }

    public static void encode(PlayerAbilityInfoSyncSTC msg, FriendlyByteBuf buffer){
        buffer.writeBoolean(msg.changing);
        buffer.writeInt(msg.list.size());
        for(AbilityInfo i : msg.list){
            i.encode(buffer);
        }
    }

    public static PlayerAbilityInfoSyncSTC decode(FriendlyByteBuf buffer){
        boolean changing = buffer.readBoolean();
        ArrayList<AbilityInfo> res = new ArrayList<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++){
            res.add(AbilityInfo.decode(buffer));
        }
        return new PlayerAbilityInfoSyncSTC(res ,changing);
    }

    public static void handle(PlayerAbilityInfoSyncSTC msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                System.out.println("Receiving ability info on client side");
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAbilityInfoSyncMessage.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientAbilityInfoSyncMessage
{
    public static void handlePacket(PlayerAbilityInfoSyncSTC msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
//                ClientAbilitiesData.setAbilities(msg.list.stream().map(Ability::getInfo).toList());
        ClientAbilitiesData.setAbilities(msg.list, msg.changing);
    }

}