package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//syncs data that needs to be referenced from both a client and server perspective, ie. mining speed
public class PlayerMiningSpeedSync {
    public float miningSpeed;

    public PlayerMiningSpeedSync(float miningSpeed) {
        this.miningSpeed = miningSpeed;
    }

    public static void encode(PlayerMiningSpeedSync msg, FriendlyByteBuf buffer){
        buffer.writeFloat(msg.miningSpeed);
    }

    public static PlayerMiningSpeedSync decode(FriendlyByteBuf buffer){
        return new PlayerMiningSpeedSync(buffer.readFloat());
    }

    public static void handle(PlayerMiningSpeedSync msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientStatsSyncMessage.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientStatsSyncMessage
{
    public static void handlePacket(PlayerMiningSpeedSync msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Player player = Minecraft.getInstance().player;
        if(player != null){
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.getBeyonderStats().setMiningSpeed(msg.miningSpeed);
            });
        }
    }
}
