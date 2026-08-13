package net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific;

import net.dinomine.potioneer.beyonder.effects.tyrant.ContractedEffect;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility.ContractOption;

import java.util.UUID;
import java.util.function.Supplier;

public class SignContractMessage {
    public ContractOption condition;
    public ContractOption reward;
    public int targetId;
    public UUID casterId;

    public SignContractMessage(ContractOption condition, ContractOption reward, int targetId, UUID casterId){
        this.condition = condition;
        this.reward = reward;
        this.targetId = targetId;
        this.casterId = casterId;
    }

    public static void encode(SignContractMessage msg, FriendlyByteBuf buffer){
        msg.condition.encode(buffer);
        msg.reward.encode(buffer);
        buffer.writeInt(msg.targetId);
        buffer.writeBoolean(msg.casterId != null);
        if (msg.casterId != null) {
            buffer.writeUUID(msg.casterId);
        }
    }

    public static SignContractMessage decode(FriendlyByteBuf buffer){
        ContractOption condition = ContractOption.decode(buffer).get();
        ContractOption reward = ContractOption.decode(buffer).get();
        int targetId = buffer.readInt();
        boolean hasCasterId = buffer.readBoolean();
        UUID casterId = hasCasterId ? buffer.readUUID() : null;

        return new SignContractMessage(condition, reward, targetId, casterId);
    }

    public static void handle(SignContractMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(!context.getDirection().getReceptionSide().isClient()){
                Entity ent = context.getSender().level().getEntity(msg.targetId);
                if(ent instanceof LivingEntity entity){
                    entity.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
                        ContractedEffect eff = ContractedEffect.getInstance(msg.condition, msg.reward, msg.casterId);
                        cap.getEffectsManager().addOrReplaceEffect(eff, cap, entity);
                    });
                }
            }
        });

        context.setPacketHandled(true);
    }
}