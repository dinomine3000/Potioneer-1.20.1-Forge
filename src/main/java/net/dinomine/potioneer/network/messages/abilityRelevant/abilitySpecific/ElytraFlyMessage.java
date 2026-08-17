package net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ElytraFlyMessage {
    public boolean start = true;
    public ElytraFlyMessage() {}
    public ElytraFlyMessage(boolean start) {this.start = start;}

    public ElytraFlyMessage(FriendlyByteBuf buf) {this.start = buf.readBoolean();}

    public void encode(FriendlyByteBuf buf) {buf.writeBoolean(this.start);}
    public static ElytraFlyMessage decode(FriendlyByteBuf buf) {return new ElytraFlyMessage(buf);}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if(start){
                player.startFallFlying();
            } else {
                player.stopFallFlying();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
