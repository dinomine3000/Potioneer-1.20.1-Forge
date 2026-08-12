package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.client.HUD.MagicOrbOverlay;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//called frequently to update the client stats mainly for the hud display, but also for other stats
public class PlayerSTCStatsSync {
    public float spirituality;
    public int maxSpirituality;
    public int sanity;
    public int maxSanity;
    public float actingProgress;
    public int dmg;
    public List<Integer> pages;

    public PlayerSTCStatsSync(float spirituality, int maxSpirituality, int sanity, int maxSanity, float actingProgress, List<Integer> pages, int dmg) {
        this.spirituality = spirituality;
        this.maxSpirituality = maxSpirituality;
        this.sanity = sanity;
        this.maxSanity = maxSanity;
        this.actingProgress = actingProgress;
        if(pages.isEmpty()) pages.add(1);
        this.pages = pages;
        this.dmg = dmg;
    }

    public PlayerSTCStatsSync(float spirituality, int maxSpirituality, int sanity, int maxSanity, float actingProgress, int dmg) {
        this.spirituality = spirituality;
        this.maxSpirituality = maxSpirituality;
        this.sanity = sanity;
        this.maxSanity = maxSanity;
        this.actingProgress = actingProgress;
        this.pages = new ArrayList<>();
        this.dmg = dmg;
    }

    public static void encode(PlayerSTCStatsSync msg, FriendlyByteBuf buffer){
        buffer.writeFloat(msg.spirituality);
        buffer.writeInt(msg.maxSpirituality);
        buffer.writeInt(msg.sanity);
        buffer.writeInt(msg.maxSanity);
        buffer.writeFloat(msg.actingProgress);
        BufferUtils.writeIntListToBuffer(msg.pages, buffer);
        buffer.writeInt(msg.dmg);
    }

    public static PlayerSTCStatsSync decode(FriendlyByteBuf buffer){
        float spir = buffer.readFloat();
        int max = buffer.readInt();
        int san = buffer.readInt();
        int maxSan = buffer.readInt();
        float acting = buffer.readFloat();
        List<Integer> pages = BufferUtils.readIntListFromBuffer(buffer);
        int dmg = buffer.readInt();
        if(pages.isEmpty()) return new PlayerSTCStatsSync(spir, max, san, maxSan, acting, dmg);
        return new PlayerSTCStatsSync(spir, max, san, maxSan, acting, pages, dmg);
    }

    public static void handle(PlayerSTCStatsSync msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHudStatsSyncMessage.handlePacket(msg));
            }
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
        ClientStatsData.setMaxSanity(msg.maxSanity);
        ClientStatsData.setDamage(msg.dmg);
//        ClientStatsData.setLuck(msg.luck, msg.minLuck, msg.maxLuck);
        if(Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(msg.spirituality < cap.getSpirituality())
                MagicOrbOverlay.playSpiritualityDown();
            cap.setSpirituality(msg.spirituality);
            cap.setMaxSpirituality(msg.maxSpirituality);
            cap.setSanity(msg.sanity);
            if(!msg.pages.isEmpty()) cap.setPageList(msg.pages);
        });
    }
}