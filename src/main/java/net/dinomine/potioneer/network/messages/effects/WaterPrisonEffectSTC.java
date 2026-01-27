package net.dinomine.potioneer.network.messages.effects;

import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//used to sync ability cooldowns from server to client
public class WaterPrisonEffectSTC {
    public double x;
    public double y;
    public double z;
    public double radius;

    public WaterPrisonEffectSTC(double x, double y, double z, double radius){
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    public static void encode(WaterPrisonEffectSTC msg, FriendlyByteBuf buffer){
        buffer.writeDouble(msg.x);
        buffer.writeDouble(msg.y);
        buffer.writeDouble(msg.z);
        buffer.writeDouble(msg.radius);
    }

    public static WaterPrisonEffectSTC decode(FriendlyByteBuf buffer){
        return new WaterPrisonEffectSTC(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public static void handle(WaterPrisonEffectSTC msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> WaterPrisonEffectSTCClientHandler.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class WaterPrisonEffectSTCClientHandler
{
    public static void handlePacket(WaterPrisonEffectSTC msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if(level == null) return;
        ParticleMaker.particleExplosionRandom(level, msg.radius, msg.x, msg.y + 1, msg.z);
    }

}