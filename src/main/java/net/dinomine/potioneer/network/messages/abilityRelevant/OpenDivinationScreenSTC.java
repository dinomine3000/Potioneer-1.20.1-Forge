package net.dinomine.potioneer.network.messages.abilityRelevant;

import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.screen.DivinationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//used to sync ability cooldowns from server to client
public class OpenDivinationScreenSTC {

    public OpenDivinationScreenSTC(){
    }

    public static void encode(OpenDivinationScreenSTC msg, FriendlyByteBuf buffer){
    }

    public static OpenDivinationScreenSTC decode(FriendlyByteBuf buffer){
        return new OpenDivinationScreenSTC();
    }

    public static void handle(OpenDivinationScreenSTC msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientDivinationScreenHandler.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientDivinationScreenHandler
{
    public static void handlePacket(OpenDivinationScreenSTC msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Minecraft.getInstance().setScreen(new DivinationScreen());
    }

}