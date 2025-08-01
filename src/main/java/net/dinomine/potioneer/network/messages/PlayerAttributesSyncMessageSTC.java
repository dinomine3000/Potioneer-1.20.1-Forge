package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//used to sync beyonder stats on advancement
public class PlayerAttributesSyncMessageSTC {
    public int[] stats;

    public PlayerAttributesSyncMessageSTC(int[] stats){
        this.stats = stats;
    }

    public static void encode(PlayerAttributesSyncMessageSTC msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.stats[0]);
        buffer.writeInt(msg.stats[1]);
        buffer.writeInt(msg.stats[2]);
        buffer.writeInt(msg.stats[3]);
        buffer.writeInt(msg.stats[4]);
    }

    public static PlayerAttributesSyncMessageSTC decode(FriendlyByteBuf buffer){
        int hp = buffer.readInt();
        int dmg = buffer.readInt();
        int armor = buffer.readInt();
        int tough = buffer.readInt();
        int knockback = buffer.readInt();
        return new PlayerAttributesSyncMessageSTC(new int[]{hp, dmg, armor, tough, knockback});
    }

    public static void handle(PlayerAttributesSyncMessageSTC msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                System.out.println("Receiving ability cooldown info on client side");
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientStatsMessageSTC.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientStatsMessageSTC
{
    public static void handlePacket(PlayerAttributesSyncMessageSTC msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
//                ClientAbilitiesData.setAbilities(msg.list.stream().map(Ability::getInfo).toList());
        ClientStatsData.setStats(msg.stats);
    }

}