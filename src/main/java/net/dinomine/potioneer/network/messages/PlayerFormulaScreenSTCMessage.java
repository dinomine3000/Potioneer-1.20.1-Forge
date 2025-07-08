package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.screen.FormulaScreen;
import net.dinomine.potioneer.savedata.PotionRecipeData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//called frequently to update the client stats for the hud display
public class PlayerFormulaScreenSTCMessage {
    public PotionRecipeData data;
    public boolean error;

    public PlayerFormulaScreenSTCMessage(PotionRecipeData data, boolean error) {
        this.data = data;
    }

    public static void encode(PlayerFormulaScreenSTCMessage msg, FriendlyByteBuf buffer){
        buffer.writeBoolean(msg.error);
        msg.data.encode(buffer);
    }

    public static PlayerFormulaScreenSTCMessage decode(FriendlyByteBuf buffer){
        boolean error = buffer.readBoolean();
        return new PlayerFormulaScreenSTCMessage(PotionRecipeData.decode(buffer), error);
    }

    public static void handle(PlayerFormulaScreenSTCMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientFormulaScreenHandler.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientFormulaScreenHandler
{
    public static void handlePacket(PlayerFormulaScreenSTCMessage msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Minecraft.getInstance().setScreen(new FormulaScreen(msg.data, msg.error));
    }
}