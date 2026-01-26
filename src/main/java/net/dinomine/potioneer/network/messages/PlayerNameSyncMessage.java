package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

//used to make sure the clients have the updated UUID-Name map for players.
//TODO optimize this so it doesnt run so often (shouldn be an issue)
public record PlayerNameSyncMessage(Map<UUID, String> map) {

    public static void encode(PlayerNameSyncMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.map.size());
        for (UUID id : msg.map.keySet()) {
            buffer.writeUUID(id);
            BufferUtils.writeStringToBuffer(msg.map.get(id), buffer);
        }
    }

    public static PlayerNameSyncMessage decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        Map<UUID, String> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            UUID id = buffer.readUUID();
            String name = BufferUtils.readString(buffer);
            map.put(id, name);
        }
        return new PlayerNameSyncMessage(map);
    }

    public static void handle(PlayerNameSyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPlayerNameSync.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }
}

@OnlyIn(Dist.CLIENT)
class ClientPlayerNameSync
{
    public static void handlePacket(PlayerNameSyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
        ClientStatsData.setServerPlayerList(msg.map());
    }
}
