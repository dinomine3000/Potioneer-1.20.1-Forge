package net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific;

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

//used to sync ability cooldowns from server to client
public class EvaporateEffect {
    public double x;
    public double y;
    public double z;

    public EvaporateEffect(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(EvaporateEffect msg, FriendlyByteBuf buffer){
        buffer.writeDouble(msg.x);
        buffer.writeDouble(msg.y);
        buffer.writeDouble(msg.z);
    }

    public static EvaporateEffect decode(FriendlyByteBuf buffer){
        return new EvaporateEffect(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public static void handle(EvaporateEffect msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> EvaporateEffectSTCClientHandler.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class EvaporateEffectSTCClientHandler
{
    public static void handlePacket(EvaporateEffect msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        RandomSource random = level.getRandom();
        int particles = random.nextInt(5, 16);
        float scalar = 0.13f;

        ParticleOptions particle = ParticleTypes.POOF;
        for(int i = 0; i < particles; i++){
            level.addParticle(particle, msg.x + random.nextFloat(), msg.y + 1, msg.z + random.nextFloat(),
                    0, scalar, 0);
        }
    }

}