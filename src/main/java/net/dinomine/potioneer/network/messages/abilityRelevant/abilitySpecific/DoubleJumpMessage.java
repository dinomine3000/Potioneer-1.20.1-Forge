package net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DoubleJumpMessage {
    public DoubleJumpMessage() {}

    public DoubleJumpMessage(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}
    public static DoubleJumpMessage decode(FriendlyByteBuf buf) {return new DoubleJumpMessage();}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.jumpFromGround();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
