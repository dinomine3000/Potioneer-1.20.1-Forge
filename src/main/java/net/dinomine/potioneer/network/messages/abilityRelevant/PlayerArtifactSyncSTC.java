package net.dinomine.potioneer.network.messages.abilityRelevant;

import net.dinomine.potioneer.beyonder.abilities.ArtifactHolder;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//message sent to the client when the server updates its list of artifacts
public class PlayerArtifactSyncSTC {
    public static final int ADD = 0;
    public static final int REMOVE = 1;
    public static final int SET = 2;
    public List<ArtifactHolder> artifacts;
    public int messageOp;

    public PlayerArtifactSyncSTC(List<ArtifactHolder> artifacts, int op){
        this.artifacts = artifacts;
        this.messageOp = op;
    }

    public static void encode(PlayerArtifactSyncSTC msg, FriendlyByteBuf buffer){
        List<ArtifactHolder> artifacts = msg.artifacts;
        buffer.writeInt(msg.messageOp);
        buffer.writeInt(artifacts.size());
        for(int i = 0; i < artifacts.size(); i++){
            buffer.writeNbt(artifacts.get(i).saveToTag(new CompoundTag()));
        }
    }

    public static PlayerArtifactSyncSTC decode(FriendlyByteBuf buffer){
        int op = buffer.readInt();
        List<ArtifactHolder> artifacts = new ArrayList<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++){
            CompoundTag tag = buffer.readAnySizeNbt();
            if(tag == null) System.out.println("Warning: reading nbt for artifact holder when receiving message is null");
            else artifacts.add(ArtifactHolder.loadFromTag(tag));
        }
        return new PlayerArtifactSyncSTC(artifacts, op);
    }

    public static void handle(PlayerArtifactSyncSTC msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientArtifactSyncHandler.handlePacket(msg, contextSupplier));
            }
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
            switch(msg.messageOp){
                case PlayerArtifactSyncSTC.ADD:
                    cap.getAbilitiesManager().addArtifactsOnClient(msg.artifacts, cap, player, true);
                    break;
                case PlayerArtifactSyncSTC.REMOVE:
                    cap.getAbilitiesManager().removeArtifactsOnClient(msg.artifacts, cap, player);
                    break;
                case PlayerArtifactSyncSTC.SET:
                    cap.getAbilitiesManager().setArtifactsOnClient(msg.artifacts, cap, player);
                    break;
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }

}
