package net.dinomine.potioneer.network.messages.abilityRelevant;

import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

//message sent to the server when a client has authorized the casting of an ability
//OR sent from server to client when casting artifact abilities in full
public class PlayerCastAbilityMessageCTS {
    public AbilityKey key;
    public boolean primary;
    public CompoundTag args;
    public UUID artifactId = null;

    public PlayerCastAbilityMessageCTS(UUID artifactId){
        this.artifactId = artifactId;
    }

    public PlayerCastAbilityMessageCTS(AbilityKey key, boolean primary, CompoundTag args){
        this.key = key;
        this.primary = primary;
        this.args = args;
    }

    public static void encode(PlayerCastAbilityMessageCTS msg, FriendlyByteBuf buffer){
        if(msg.artifactId == null){
            buffer.writeBoolean(true);
            msg.key.writeToBuffer(buffer);
            buffer.writeBoolean(msg.primary);
            buffer.writeNbt(msg.args);
        } else{
            buffer.writeBoolean(false);
            buffer.writeUUID(msg.artifactId);
        }
    }

    public static PlayerCastAbilityMessageCTS decode(FriendlyByteBuf buffer){
        if(buffer.readBoolean()){
            AbilityKey ablId = AbilityKey.readFromBuffer(buffer);
            boolean primary = buffer.readBoolean();
            CompoundTag args = buffer.readNbt();
            return new PlayerCastAbilityMessageCTS(ablId, primary, args);
        } else {
            UUID artifactId = buffer.readUUID();
            return new PlayerCastAbilityMessageCTS(artifactId);
        }
    }

    public static void handle(PlayerCastAbilityMessageCTS msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(!context.getDirection().getReceptionSide().isClient()){
                //on server side
                Player player = context.getSender();
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                    cap.getAbilitiesManager().useAbility(cap, player, msg.key, false, msg.primary, msg.args);
                });
            } else {
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientCastAbilityMessage.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }
}

@OnlyIn(Dist.CLIENT)
class ClientCastAbilityMessage
{
    public static void handlePacket(PlayerCastAbilityMessageCTS msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        Player player = Minecraft.getInstance().player;
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getAbilitiesManager().castArtifactAbility(msg.artifactId, cap, player);
        });
    }

}
