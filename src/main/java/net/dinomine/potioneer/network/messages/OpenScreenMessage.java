package net.dinomine.potioneer.network.messages;

import com.eliotlash.mclib.math.functions.limit.Min;
import net.dinomine.potioneer.beyonder.client.screen.DivinationScreen;
import net.dinomine.potioneer.beyonder.client.screen.FormulaScreen;
import net.dinomine.potioneer.beyonder.client.screen.KnowledgeBookScreen;
import net.dinomine.potioneer.beyonder.client.screen.RepScreen;
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
    public int intId;
    public int reputation;
    public static enum Screen{
        Formula,
        Divination,
        Book,
        Reputation
    }

    public OpenScreenMessage(PotionRecipeData data, boolean error) {
        this.data = data;
        this.error = error;
        this.screenType = Screen.Formula;
    }

    public OpenScreenMessage(Screen screenType){
        this(screenType, -1);
    }

    public OpenScreenMessage(Screen screenType, int intId){
        this.screenType = screenType;
        this.intId = intId;
    }

    public OpenScreenMessage(Screen screenType, int intId, int reputation){
        this.screenType = screenType;
        this.intId = intId;
        this.reputation = reputation;
    }

    public static void encode(OpenScreenMessage msg, FriendlyByteBuf buffer){
        BufferUtils.writeStringToBuffer(msg.screenType.name(), buffer);
        switch (msg.screenType){
            case Formula:
                buffer.writeBoolean(msg.error);
                msg.data.encode(buffer);
                break;
            case Book:
                buffer.writeInt(msg.intId);
                break;
            case Reputation:
                buffer.writeInt(msg.intId);
                buffer.writeInt(msg.reputation);
                break;
        }
    }

    public static OpenScreenMessage decode(FriendlyByteBuf buffer){
        Screen screenType = Screen.valueOf(BufferUtils.readString(buffer));
        switch (screenType){
            case Formula:
                boolean error = buffer.readBoolean();
                return new OpenScreenMessage(PotionRecipeData.decode(buffer), error);
            case Book:
                int pageId = buffer.readInt();
                return new OpenScreenMessage(Screen.Book, pageId);
            case Reputation:
                int pathway = buffer.readInt();
                int rep = buffer.readInt();
                return new OpenScreenMessage(Screen.Reputation, pathway, rep);
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
            case Book -> Minecraft.getInstance().setScreen(new KnowledgeBookScreen(msg.intId));
            case Reputation -> Minecraft.getInstance().setScreen(new RepScreen(msg.intId, msg.reputation));
        }

    }
}