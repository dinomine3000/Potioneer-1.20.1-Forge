package net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific;

import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility;
import net.dinomine.potioneer.beyonder.client.screen.ContractScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//viewing contract doesnt use this message, so it only handles Creating a contract OR Signing a contract.
public class OpenContractScreenMessage {
    public List<ContractAbility.ContractOption> options;
    public int id;
    public AbilityKey key = null;
    public boolean writer = true;

    public OpenContractScreenMessage(List<ContractAbility.ContractOption> options, int targetId, AbilityKey key){
        this.options = options;
        this.key = key;
        this.id = targetId;
    }
    public OpenContractScreenMessage(ContractAbility.ContractOption condition, ContractAbility.ContractOption reward, int targetId){
        this.options = List.of(condition, reward);
        this.writer = false;
        this.id = targetId;
    }

    public static void encode(OpenContractScreenMessage msg, FriendlyByteBuf buf){
        buf.writeInt(msg.options.size());
        for(ContractAbility.ContractOption opt: msg.options) opt.encode(buf);
        buf.writeInt(msg.id);
        buf.writeBoolean(msg.writer);
        if(msg.writer)
            msg.key.writeToBuffer(buf);
    }

    public static OpenContractScreenMessage decode(FriendlyByteBuf buffer){
        int size = buffer.readInt();
        List<ContractAbility.ContractOption> options = new ArrayList<>();
        for(int i = 0; i < size; i++) options.add(ContractAbility.ContractOption.decode(buffer).get());
        int id = buffer.readInt();
        boolean writer = buffer.readBoolean();
        if(!writer) return new OpenContractScreenMessage(options.get(0), options.get(1), id);
        AbilityKey key = AbilityKey.readFromBuffer(buffer);
        return new OpenContractScreenMessage(options, id, key);
    }

    public static void handle(OpenContractScreenMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientOpenContractScreen.handlePacket(msg)));
            }
        });

        context.setPacketHandled(true);
    }
}


@OnlyIn(Dist.CLIENT)
class ClientOpenContractScreen
{

    public static void handlePacket(OpenContractScreenMessage msg)
    {
        if(msg.writer) Minecraft.getInstance().setScreen(new ContractScreen(msg.options, msg.id, msg.key));
        else ContractScreen.openContractToSign(msg.options.get(0), msg.options.get(1), msg.id);
    }
}

