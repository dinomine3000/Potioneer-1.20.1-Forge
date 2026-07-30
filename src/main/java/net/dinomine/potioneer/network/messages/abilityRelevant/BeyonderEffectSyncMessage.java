package net.dinomine.potioneer.network.messages.abilityRelevant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//message to synchronize beyonder effects
public class BeyonderEffectSyncMessage {
    public enum Operation {
        ADD(0),
        REMOVE(1),
        SET(2),
        UPDATE(3);

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
    public static final int UPDATE = Operation.UPDATE.getId();

    public List<BeyonderEffect> effects;
    public Operation operation;

    public BeyonderEffectSyncMessage(List<BeyonderEffect> effects, int operation) {
        this(effects, Operation.fromId(operation));
    }

    public BeyonderEffectSyncMessage(List<BeyonderEffect> effects, Operation operation) {
        this.effects = effects;
        this.operation = operation;
    }

    public static void encode(BeyonderEffectSyncMessage msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.operation.id);
        buffer.writeInt(msg.effects.size());
        for(BeyonderEffect eff: msg.effects){
            eff.writeToBuffer(buffer);
        }
    }

    public static BeyonderEffectSyncMessage decode(FriendlyByteBuf buffer){
        int op = buffer.readInt();
        int size = buffer.readInt();
        List<BeyonderEffect> effects = new ArrayList<>();
        for(int i = 0; i < size; i++){
            effects.add(BeyonderEffect.readFromBuffer(buffer));
        }
        return new BeyonderEffectSyncMessage(effects, op);
    }

    public static void handle(BeyonderEffectSyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientEffectSyncMessage.handlePacket(msg)));
            }
        });

        context.setPacketHandled(true);
    }

    @Override
    public String toString() {
        StringBuilder blr = new StringBuilder();
        for(BeyonderEffect eff: effects){
            blr.append(eff.getId()).append(", ");
        }
        blr.append("\nOperation: ").append(operation.toString());
        return blr.toString();
    }
}

@OnlyIn(Dist.CLIENT)
class ClientEffectSyncMessage
{
    public static void handlePacket(BeyonderEffectSyncMessage msg)
    {
//                ClientAbilitiesData.setAbilities(msg.list.stream().map(Ability::getInfo).toList());
        System.out.println("Receiving effect info on client side: " + msg);
        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            switch (msg.operation){
                case ADD -> cap.getEffectsManager().addEffectsOnClient(msg.effects, cap, player);
                case SET -> cap.getEffectsManager().setEffectsOnClient(msg.effects, cap, player);
                case REMOVE -> cap.getEffectsManager().removeEffectsOnClient(msg.effects, cap, player);
                case UPDATE -> cap.getEffectsManager().updateEffectsOnClient(msg.effects);
            }
        });
    }

}