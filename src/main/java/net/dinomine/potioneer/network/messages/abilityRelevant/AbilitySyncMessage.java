package net.dinomine.potioneer.network.messages.abilityRelevant;

import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//used to sync abilities in client and server.
//based on the operation value, the client will either add, remove or reset the abilities it has (in the capabilty and info) to this list.
public class AbilitySyncMessage {
    public enum Operation {
        ADD(0),
        REMOVE(1),
        SET(2),
        UPDATE(3),
        CLEAR(4),
        LOAD(5);

        private final int id;

        Operation(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Operation fromId(int id) {
            for (Operation op : values()) {
                if (op.id == id) {
                    return op;
                }
            }
            throw new IllegalArgumentException("Invalid Operation ID: " + id);
        }
    }

    public static final int ADD = Operation.ADD.getId();
    public static final int REMOVE = Operation.REMOVE.getId();
    public static final int SET = Operation.SET.getId();
    public static final int LOAD = Operation.LOAD.getId();
    public static final int UPDATE = Operation.UPDATE.getId();

    public List<AbilityInfo> abilities;
    public Operation operation;

    public AbilitySyncMessage(List<AbilityInfo> abilities, int operation) {
        this(abilities, Operation.fromId(operation));
    }

    public AbilitySyncMessage(List<AbilityInfo> abilities, Operation operation) {
        this.abilities = abilities;
        this.operation = operation;
    }

    public AbilitySyncMessage(AbilityInfo ability, int operation) {
        this(List.of(ability), Operation.fromId(operation));
    }

    public AbilitySyncMessage(AbilityInfo ability, Operation operation) {
        this(List.of(ability), operation);
    }

    public static void encode(AbilitySyncMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.operation.getId());
        buffer.writeCollection(msg.abilities, (buf, abl) -> abl.encode(buf));
    }

    public static AbilitySyncMessage decode(FriendlyByteBuf buffer) {
        int op = buffer.readInt();
        List<AbilityInfo> abilities = new ArrayList<>(buffer.readList(AbilityInfo::decode));
        return new AbilitySyncMessage(abilities, op);
    }

    public static void handle(AbilitySyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAbilityStateSTC.handlePacket(msg)));
        }
        context.setPacketHandled(true);
    }
}

@OnlyIn(Dist.CLIENT)
class ClientAbilityStateSTC {
    public static void handlePacket(AbilitySyncMessage msg) {
        switch (msg.operation) {
            case ADD -> ClientAbilitiesData.addAbilities(msg.abilities);
            case REMOVE -> ClientAbilitiesData.removeAbilities(msg.abilities);
            case SET -> ClientAbilitiesData.setAbilities(msg.abilities, true);
            case LOAD -> ClientAbilitiesData.setAbilities(msg.abilities, false);
            case UPDATE -> ClientAbilitiesData.updateAbilities(msg.abilities);
        }
    }
}