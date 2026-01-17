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
    public static final int ADD = 0;
    public static final int REMOVE = 1;
    public static final int SET = 2;
    public static final int UPDATE = 3;
    public List<AbilityInfo> abilities;
    public int messageOp;

    public AbilitySyncMessage(List<AbilityInfo> abilities, int operation){
        this.abilities = abilities;
        this.messageOp = operation;
    }

    public AbilitySyncMessage(AbilityInfo ability, int operation){
        this.abilities = List.of(ability);
        this.messageOp = operation;
    }

    public static void encode(AbilitySyncMessage msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.messageOp);
        buffer.writeInt(msg.abilities.size());
        for(AbilityInfo abl: msg.abilities){
            abl.encode(buffer);
        }
    }

    public static AbilitySyncMessage decode(FriendlyByteBuf buffer){
        int op = buffer.readInt();
        int size = buffer.readInt();
        ArrayList<AbilityInfo> abilities = new ArrayList<>();
        for(int i = 0; i < size; i++){
            AbilityInfo info = AbilityInfo.decode(buffer);
            abilities.add(info);
        }
        return new AbilitySyncMessage(abilities, op);
    }

    public static void handle(AbilitySyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAbilityStateSTC.handlePacket(msg)));
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientAbilityStateSTC
{
    public static void handlePacket(AbilitySyncMessage msg)
    {
//                ClientAbilitiesData.setAbilities(msg.list.stream().map(Ability::getInfo).toList());
//        ClientAbilitiesData.setEnabled(msg.cAblId, msg.enabled);
        switch(msg.messageOp){
            case AbilitySyncMessage.ADD:
                ClientAbilitiesData.addAbilities(msg.abilities);
                break;
            case AbilitySyncMessage.REMOVE:
                ClientAbilitiesData.removeAbilities(msg.abilities);
                break;
            case AbilitySyncMessage.SET:
                ClientAbilitiesData.setAbilities(msg.abilities);
                break;
            case AbilitySyncMessage.UPDATE:
                ClientAbilitiesData.updateAbilities(msg.abilities);
                break;
        }
    }

}