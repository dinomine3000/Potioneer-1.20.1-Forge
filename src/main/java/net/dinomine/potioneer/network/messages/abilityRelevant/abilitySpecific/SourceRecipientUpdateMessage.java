package net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific;

import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.misc.AbstractSourceRecipientEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SourceRecipientUpdateMessage {
    public HashMap<UUID, Integer> sources;
    public String id;

    public SourceRecipientUpdateMessage(String effectId, HashMap<UUID, Integer> sources){
        this.id = effectId;
        this.sources = sources;
    }

    public static void encode(SourceRecipientUpdateMessage msg, FriendlyByteBuf buf){
        BufferUtils.writeStringToBuffer(msg.id, buf);
        buf.writeInt(msg.sources.size());
        for(UUID id: msg.sources.keySet()){
            buf.writeUUID(id);
            buf.writeInt(msg.sources.get(id));
        }
    }

    public static SourceRecipientUpdateMessage decode(FriendlyByteBuf buffer){
        String id = BufferUtils.readString(buffer);
        HashMap<UUID, Integer> sources = new HashMap<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++){
            sources.put(buffer.readUUID(), buffer.readInt());
        }
        return new SourceRecipientUpdateMessage(id, sources);
    }

    public static void handle(SourceRecipientUpdateMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSourceRecipient.handlePacket(msg)));
            }
        });

        context.setPacketHandled(true);
    }
}


@OnlyIn(Dist.CLIENT)
class ClientSourceRecipient
{

    public static void handlePacket(SourceRecipientUpdateMessage msg)
    {
        Player player = Minecraft.getInstance().player;
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            BeyonderEffect eff = cap.getEffectsManager().getEffect(msg.id);
            if(!(eff instanceof AbstractSourceRecipientEffect sourceEffect)) return;
            sourceEffect.setSourceOnClient(msg.sources);
        });
    }
}

