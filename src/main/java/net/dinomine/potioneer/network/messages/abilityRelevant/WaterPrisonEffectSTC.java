package net.dinomine.potioneer.network.messages.abilityRelevant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static net.dinomine.potioneer.util.misc.MysticismHelper.radius;

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
        RandomSource random = level.getRandom();
        int particles = random.nextInt(100*radius/5, 200*radius/5);
        float scalar = 0.13f;

        ParticleOptions particle = ParticleTypes.END_ROD;
        for(int i = 0; i < particles; i++){
            level.addParticle(particle, msg.x, msg.y + 1, msg.z,
                    msg.radius*(scalar - 2*scalar*random.nextFloat()), msg.radius*(scalar - 2*scalar*random.nextFloat()), msg.radius*(scalar - 2*scalar*random.nextFloat()));
        }
    }

}