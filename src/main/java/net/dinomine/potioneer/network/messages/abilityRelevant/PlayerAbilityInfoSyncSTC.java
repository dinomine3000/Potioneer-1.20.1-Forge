package net.dinomine.potioneer.network.messages.abilityRelevant;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

//message sent to client when advancing, to synchronize the abilities available to the player
//it assumes the server data is the true data and sets the data on client side to that
public class PlayerAbilityInfoSyncSTC {
    public LinkedHashMap<String, AbilityInfo> abilityInfoMap;
    public boolean changing;

    public PlayerAbilityInfoSyncSTC(LinkedHashMap<String, AbilityInfo> list, boolean changing){
        this.abilityInfoMap = list;
        this.changing = changing;
    }


    public static void encode(PlayerAbilityInfoSyncSTC msg, FriendlyByteBuf buffer){
        buffer.writeBoolean(msg.changing);
        buffer.writeInt(msg.abilityInfoMap.size());
        for(String ablId: msg.abilityInfoMap.keySet()){
            BufferUtils.writeStringToBuffer(ablId, buffer);
            msg.abilityInfoMap.get(ablId).encode(buffer);
        }
    }

    public static PlayerAbilityInfoSyncSTC decode(FriendlyByteBuf buffer){
        boolean changing = buffer.readBoolean();
        LinkedHashMap<String, AbilityInfo> res = new LinkedHashMap<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++){
            String ablId = BufferUtils.readString(buffer);
            AbilityInfo ablInfo = AbilityInfo.decode(buffer);
            res.put(ablId, ablInfo);
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
        ClientAbilitiesData.setAbilities(msg.abilityInfoMap, msg.changing);
    }

}