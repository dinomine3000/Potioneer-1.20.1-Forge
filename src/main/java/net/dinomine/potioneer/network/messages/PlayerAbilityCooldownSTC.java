package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//used to sync ability cooldowns from server to client
public class PlayerAbilityCooldownSTC {
    public String descId;
    public int cd;
    public int maxCd;

    public PlayerAbilityCooldownSTC(String descId, int cd, int maxCd){
        this.cd = cd;
        this.descId = descId;
        this.maxCd = maxCd;
    }

    public static void encode(PlayerAbilityCooldownSTC msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.cd);
        buffer.writeInt(msg.maxCd);
        buffer.writeInt(msg.descId.length());
        for (int i = 0; i < msg.descId.length(); i++) {
            buffer.writeChar(msg.descId.charAt(i));
        }
    }

    public static PlayerAbilityCooldownSTC decode(FriendlyByteBuf buffer){
        int cd = buffer.readInt();
        int maxCd = buffer.readInt();
        int idSize = buffer.readInt();
        StringBuilder idBuilder = new StringBuilder();
        for (int i = 0; i < idSize; i++) {
            idBuilder.append(buffer.readChar());
        }
        return new PlayerAbilityCooldownSTC(idBuilder.toString(), cd, maxCd);
    }

    public static void handle(PlayerAbilityCooldownSTC msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                System.out.println("Receiving ability cooldown info on client side");
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAbilityCooldownSTC.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientAbilityCooldownSTC
{
    public static void handlePacket(PlayerAbilityCooldownSTC msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
//                ClientAbilitiesData.setAbilities(msg.list.stream().map(Ability::getInfo).toList());
        ClientAbilitiesData.setCooldown(msg.descId, msg.cd, msg.maxCd);
    }

}