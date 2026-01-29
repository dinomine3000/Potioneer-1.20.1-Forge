package net.dinomine.potioneer.network.messages.effects;

import net.dinomine.potioneer.util.BufferUtils;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class GeneralAreaEffectMessage {
    ParticleMaker.Preset preset;
    public Vector3f centerPos;
    public double radius;

    public GeneralAreaEffectMessage(ParticleMaker.Preset preset, Vector3f pos, double radius){
        this.preset = preset;
        this.centerPos = pos;
        this.radius = radius;
    }

    public static void encode(GeneralAreaEffectMessage msg, FriendlyByteBuf buffer){
        BufferUtils.writeStringToBuffer(msg.preset.name(), buffer);
        buffer.writeDouble(msg.radius);
        buffer.writeVector3f(msg.centerPos);
    }

    public static GeneralAreaEffectMessage decode(FriendlyByteBuf buffer){
        ParticleMaker.Preset preset = ParticleMaker.Preset.valueOf(BufferUtils.readString(buffer));
        double radius = buffer.readDouble();
        Vector3f pos = buffer.readVector3f();
        return new GeneralAreaEffectMessage(preset, pos, radius);
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
            switch (msg.preset){
                case AOE_GRAVITY:
                    ParticleMaker.fallingGlow(level, new Vec3(msg.centerPos), msg.radius);
                    break;
                case AOE_END_ROD:
                    ParticleMaker.particleExplosionRandom(level, msg.radius, msg.centerPos.x, msg.centerPos.y, msg.centerPos.z);
                    break;
            }
        }
    }
}
