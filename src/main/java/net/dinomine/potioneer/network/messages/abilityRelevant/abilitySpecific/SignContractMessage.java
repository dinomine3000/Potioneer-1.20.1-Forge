package net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific;

import net.dinomine.potioneer.beyonder.effects.tyrant.ContractedEffect;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility.ContractOption;

import java.util.function.Supplier;

public class SignContractMessage {
    public ContractOption condition;
    public ContractOption reward;
    public int targetId;

    public SignContractMessage(ContractOption condition, ContractOption reward, int targetId){
        this.condition = condition;
        this.reward = reward;
        this.targetId = targetId;
    }

    public static void encode(SignContractMessage msg, FriendlyByteBuf buffer){
        msg.condition.encode(buffer);
        msg.reward.encode(buffer);
        buffer.writeInt(msg.targetId);
    }

    public static SignContractMessage decode(FriendlyByteBuf buffer){
        return new SignContractMessage(ContractOption.decode(buffer).get(), ContractOption.decode(buffer).get(), buffer.readInt());
    }

    public static void handle(SignContractMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(!context.getDirection().getReceptionSide().isClient()){
                Entity ent = context.getSender().level().getEntity(msg.targetId);
                if(ent instanceof LivingEntity entity){
                    entity.getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
                        ContractedEffect eff = ContractedEffect.getInstance(msg.condition, msg.reward);
                        cap.getEffectsManager().addOrReplaceEffect(eff, cap, entity);
                    });
                }
            }
        });

        context.setPacketHandled(true);
    }
}
