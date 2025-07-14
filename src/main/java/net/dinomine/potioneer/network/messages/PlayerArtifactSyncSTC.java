package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//message sent to the client when the server updates its list of artifacts
public class PlayerArtifactSyncSTC {
    public String ablId;
    public int sequence;
    public boolean add;

    public PlayerArtifactSyncSTC(String ablId, int sequence, boolean add){
        this.ablId = ablId;
        this.sequence = sequence;
        this.add = add;
    }

    public static void encode(PlayerArtifactSyncSTC msg, FriendlyByteBuf buffer){
        buffer.writeBoolean(msg.add);
        buffer.writeInt(msg.sequence);
        buffer.writeInt(msg.ablId.length());
        for (int i = 0; i < msg.ablId.length(); i++){
            buffer.writeChar(msg.ablId.charAt(i));
        }
    }

    public static PlayerArtifactSyncSTC decode(FriendlyByteBuf buffer){
        boolean add = buffer.readBoolean();
        int sequence = buffer.readInt();
        int size = buffer.readInt();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++){
            builder.append(buffer.readChar());
        }
        return new PlayerArtifactSyncSTC(builder.toString(), sequence, add);
    }

    public static void handle(PlayerArtifactSyncSTC msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ClientArtifactSyncHandler.handlePacket(msg, contextSupplier);
        });

        context.setPacketHandled(true);
    }
}

@OnlyIn(Dist.CLIENT)
class ClientArtifactSyncHandler
{
    public static void handlePacket(PlayerArtifactSyncSTC msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
//                ClientAbilitiesData.setAbilities(msg.list.stream().map(Ability::getInfo).toList());
        Player player = Minecraft.getInstance().player;
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(msg.add){
                cap.getAbilitiesManager().updateAddArtifact(cap, player, msg.ablId, msg.sequence, false);
            } else {
                cap.getAbilitiesManager().updateRemoveArtifact(cap, player, msg.ablId, false);
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }

}
