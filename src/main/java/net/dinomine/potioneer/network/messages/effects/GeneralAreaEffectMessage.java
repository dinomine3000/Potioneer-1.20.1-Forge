package net.dinomine.potioneer.network.messages.effects;

import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GeneralAreaEffectMessage {
    public BlockPos blockPos;
    public double radius;

    public GeneralAreaEffectMessage(BlockPos pos, double radius){
        this.blockPos = pos;
        this.radius = radius;
    }

    public static void encode(GeneralAreaEffectMessage msg, FriendlyByteBuf buffer){
        buffer.writeDouble(msg.radius);
        buffer.writeBlockPos(msg.blockPos);
    }

    public static GeneralAreaEffectMessage decode(FriendlyByteBuf buffer){
        double radius = buffer.readDouble();
        BlockPos pos = buffer.readBlockPos();
        return new GeneralAreaEffectMessage(pos, radius);
    }


    public static void handle(GeneralAreaEffectMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> GeneralAreaEffectClient.handlePacket(msg, contextSupplier)));
        });

        context.setPacketHandled(true);
    }
}

@OnlyIn(Dist.CLIENT)
class GeneralAreaEffectClient
{
    public static void handlePacket(GeneralAreaEffectMessage msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Player player = Minecraft.getInstance().player;
        if (player != null)
        {
            Level level = player.level();
            ParticleMaker.particleExplosionRandom(level, msg.radius, msg.blockPos.getX(), msg.blockPos.getY(), msg.blockPos.getZ());
        }
    }
}
