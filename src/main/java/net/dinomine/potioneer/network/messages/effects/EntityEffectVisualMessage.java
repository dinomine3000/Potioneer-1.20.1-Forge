package net.dinomine.potioneer.network.messages.effects;

import net.dinomine.potioneer.server.ClientEffectVisualHandling;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class EntityEffectVisualMessage {

    public enum Operation {
        ADD,
        REMOVE
    }

    public final List<Integer> entityIds;
    public final Operation operation;
    public final String effectType;

    public EntityEffectVisualMessage(Collection<Integer> entityIds, Operation operation, String effectType) {
        this.entityIds = new ArrayList<>(entityIds);
        this.operation = operation;
        this.effectType = effectType;
    }

    // Convenience constructor for single entity targets
    public EntityEffectVisualMessage(int entityId, Operation operation, String effectType) {
        this(List.of(entityId), operation, effectType);
    }

    public static void encode(EntityEffectVisualMessage msg, FriendlyByteBuf buffer) {
        buffer.writeEnum(msg.operation);
        BufferUtils.writeStringToBuffer(msg.effectType, buffer);

        // Write collection size and array of ints
        buffer.writeVarInt(msg.entityIds.size());
        for (int id : msg.entityIds) {
            buffer.writeInt(id);
        }
    }

    public static EntityEffectVisualMessage decode(FriendlyByteBuf buffer) {
        Operation operation = buffer.readEnum(Operation.class);
        String effectType = BufferUtils.readString(buffer);

        // Read collection size and reconstructed ID list
        int count = buffer.readVarInt();
        List<Integer> entityIds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            entityIds.add(buffer.readInt());
        }

        return new EntityEffectVisualMessage(entityIds, operation, effectType);
    }

    public static void handle(EntityEffectVisualMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> EntityEffectSyncClient.handlePacket(msg, contextSupplier));
        });

        context.setPacketHandled(true);
    }
}

@OnlyIn(Dist.CLIENT)
class EntityEffectSyncClient {

    public static void handlePacket(EntityEffectVisualMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        switch (msg.effectType){
            case "mist":
                if(msg.operation == EntityEffectVisualMessage.Operation.ADD)
                    for (int entityId : msg.entityIds) ClientEffectVisualHandling.addMistEntity(entityId);
                else
                    for (int entityId : msg.entityIds) ClientEffectVisualHandling.removeMistEntity(entityId);
                break;
            case "invisible":
                if(msg.operation == EntityEffectVisualMessage.Operation.ADD)
                    for (int entityId : msg.entityIds) ClientEffectVisualHandling.addInvisibleEntity(level, entityId);
                else
                    for (int entityId : msg.entityIds) ClientEffectVisualHandling.removeInvisibleEntity(level, entityId);
                break;
            case "ranma":
                if(msg.operation == EntityEffectVisualMessage.Operation.ADD)
                    for (int entityId : msg.entityIds) ClientEffectVisualHandling.addRanmaEntity(entityId);
                else
                    for (int entityId : msg.entityIds) ClientEffectVisualHandling.removeRanmaEntity(entityId);
                break;
        }
    }
}