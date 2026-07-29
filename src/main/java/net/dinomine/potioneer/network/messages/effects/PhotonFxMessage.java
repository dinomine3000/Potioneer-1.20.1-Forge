package net.dinomine.potioneer.network.messages.effects;

import net.dinomine.potioneer.particle.custom.GenericParticleOptions;
import net.dinomine.potioneer.util.BufferUtils;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Supplier;

public class PhotonFxMessage {
    public Integer targetId = null;
    public BlockPos blockPos = null;
    ParticleMaker.Preset preset;

    public PhotonFxMessage(ParticleMaker.Preset preset, Entity target){
        this(preset, target.getId());
    }
    public PhotonFxMessage(ParticleMaker.Preset preset, int targetId){
        this.preset = preset;
        this.targetId = targetId;
    }
    public PhotonFxMessage(ParticleMaker.Preset preset, BlockPos blockPos){
        this.preset = preset;
        this.blockPos = blockPos;
    }
    public PhotonFxMessage(ParticleMaker.Preset preset, Integer targetId, BlockPos blockPos){
        this.preset = preset;
        this.targetId = targetId;
        this.blockPos = blockPos;
    }

    public static void encode(PhotonFxMessage msg, FriendlyByteBuf buffer){
        BufferUtils.writeStringToBuffer(msg.preset.name(), buffer);
        if(msg.targetId != null){
            buffer.writeBoolean(true);
            buffer.writeInt(msg.targetId);
            return;
        }
        buffer.writeBoolean(false);
        buffer.writeBlockPos(msg.blockPos);
    }

    public static PhotonFxMessage decode(FriendlyByteBuf buffer){
        ParticleMaker.Preset preset = ParticleMaker.Preset.valueOf(BufferUtils.readString(buffer));
        Integer targetId = null;
        BlockPos blockPos = null;
        if(buffer.readBoolean()){
            targetId = buffer.readInt();
        } else blockPos = buffer.readBlockPos();
        return new PhotonFxMessage(preset, targetId, blockPos);
    }


    public static void handle(PhotonFxMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PhotonFxMessageClient.handlePacket(msg, contextSupplier)));
        });

        context.setPacketHandled(true);
    }
}

@OnlyIn(Dist.CLIENT)
class PhotonFxMessageClient
{
    public static void handlePacket(PhotonFxMessage msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Player player = Minecraft.getInstance().player;
        if (player != null)
        {
            Level level = player.level();
            GenericParticleOptions waterTrap = new GenericParticleOptions(new Vector4f(0f, 0.2f, 1f, 1f), 1, 0.9f, 0, 20);
            switch (msg.preset){
                case WATER_JET:
                    ParticleMaker.createWaterJet(msg.targetId, player.level());
                    break;
            }
        }
    }
}
