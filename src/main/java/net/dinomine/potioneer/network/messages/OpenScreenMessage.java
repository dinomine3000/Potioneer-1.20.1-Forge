package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.client.screen.DivinationScreen;
import net.dinomine.potioneer.beyonder.client.screen.FormulaScreen;
import net.dinomine.potioneer.beyonder.client.screen.KnowledgeBookScreen;
import net.dinomine.potioneer.recipe.PotionRecipeData;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//used to open a screen on client side
public class OpenScreenMessage {
    public PotionRecipeData data;
    public boolean error;
    public Screen screenType;
    public static enum Screen{
        Formula,
        Divination,
        Book
    }

    public OpenScreenMessage(PotionRecipeData data, boolean error) {
        this.data = data;
        this.error = error;
        this.screenType = Screen.Formula;
    }

    public OpenScreenMessage(Screen screenType){
        this.screenType = screenType;
    }


    public static void encode(OpenScreenMessage msg, FriendlyByteBuf buffer){
        BufferUtils.writeStringToBuffer(msg.screenType.name(), buffer);
        if(msg.screenType == Screen.Formula){
            buffer.writeBoolean(msg.error);
            msg.data.encode(buffer);
        }
    }

    public static OpenScreenMessage decode(FriendlyByteBuf buffer){
        Screen screenType = Screen.valueOf(BufferUtils.readString(buffer));
        if(screenType == Screen.Formula){
            boolean error = buffer.readBoolean();
            return new OpenScreenMessage(PotionRecipeData.decode(buffer), error);
        }
        return new OpenScreenMessage(screenType);
    }

    public static void handle(OpenScreenMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientOpenScreenHandler.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientOpenScreenHandler
{
    public static void handlePacket(OpenScreenMessage msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        switch (msg.screenType){
            case Formula -> Minecraft.getInstance().setScreen(new FormulaScreen(msg.data, msg.error));
            case Divination -> Minecraft.getInstance().setScreen(new DivinationScreen());
            case Book -> Minecraft.getInstance().setScreen(new KnowledgeBookScreen());
        }

    }
}