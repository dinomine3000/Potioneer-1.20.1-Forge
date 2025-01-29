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
    public int caret;
    public int cd;
    public int maxCd;

    public PlayerAbilityCooldownSTC(int caret, int cd, int maxCd){
        this.cd = cd;
        this.caret = caret;
        this.maxCd = maxCd;
    }

    public static void encode(PlayerAbilityCooldownSTC msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.caret);
        buffer.writeInt(msg.cd);
        buffer.writeInt(msg.maxCd);
    }

    public static PlayerAbilityCooldownSTC decode(FriendlyByteBuf buffer){
        int caret = buffer.readInt();
        int cd = buffer.readInt();
        int maxCd = buffer.readInt();
        return new PlayerAbilityCooldownSTC(caret, cd, maxCd);
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
        ClientAbilitiesData.setCooldown(msg.caret, msg.cd, msg.maxCd);
    }

}