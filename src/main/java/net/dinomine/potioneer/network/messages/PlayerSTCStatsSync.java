package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//called frequently to update the client stats mainly for the hud display, but also for other stats
public class PlayerSTCStatsSync {
    public float spirituality;
    public int maxSpirituality;
    public int sanity;
    public float actingProgress;
    public int[] stats;

    public PlayerSTCStatsSync(float spirituality, int maxSpirituality, int sanity, float actingProgress, int[] stats) {
        this.spirituality = spirituality;
        this.maxSpirituality = maxSpirituality;
        this.sanity = sanity;
        this.actingProgress = actingProgress;
        this.stats = stats;
    }

    public static void encode(PlayerSTCStatsSync msg, FriendlyByteBuf buffer){
        buffer.writeFloat(msg.spirituality);
        buffer.writeInt(msg.maxSpirituality);
        buffer.writeInt(msg.sanity);
        buffer.writeFloat(msg.actingProgress);
        buffer.writeInt(msg.stats[0]);
        buffer.writeInt(msg.stats[1]);
        buffer.writeInt(msg.stats[2]);
        buffer.writeInt(msg.stats[3]);
        buffer.writeInt(msg.stats[4]);
    }

    public static PlayerSTCStatsSync decode(FriendlyByteBuf buffer){
        float spir = buffer.readFloat();
        int max = buffer.readInt();
        int san = buffer.readInt();
        float acting = buffer.readFloat();
        int hp = buffer.readInt();
        int dmg = buffer.readInt();
        int armor = buffer.readInt();
        int tough = buffer.readInt();
        int knockback = buffer.readInt();
        return new PlayerSTCStatsSync(spir, max, san, acting, new int[]{hp, dmg, armor, tough, knockback});
    }

    public static void handle(PlayerSTCStatsSync msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHudStatsSyncMessage.handlePacket(msg));
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientHudStatsSyncMessage
{
    public static void handlePacket(PlayerSTCStatsSync msg)
    {
        ClientStatsData.setActing(msg.actingProgress);
//        ClientStatsData.setLuck(msg.luck, msg.minLuck, msg.maxLuck);
        if(Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.setSpirituality(msg.spirituality);
            cap.setMaxSpirituality(msg.maxSpirituality);
            cap.setSanity(msg.sanity);
            cap.getBeyonderStats().setAttributes(msg.stats);
        });
    }
}