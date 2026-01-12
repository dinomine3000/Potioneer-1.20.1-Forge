package net.dinomine.potioneer.network.messages.abilityRelevant;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//message sent to the server when a client has authorized the casting of an ability
public class PlayerCastAbilityMessageCTS {
    public String ablId;
    public boolean primary;

    public PlayerCastAbilityMessageCTS(String ablId, boolean primary){
        this.ablId = ablId;
        this.primary = primary;
    }

    public static void encode(PlayerCastAbilityMessageCTS msg, FriendlyByteBuf buffer){
        BufferUtils.writeStringToBuffer(msg.ablId, buffer);
        buffer.writeBoolean(msg.primary);
    }

    public static PlayerCastAbilityMessageCTS decode(FriendlyByteBuf buffer){
        String ablId = BufferUtils.readString(buffer);
        boolean primary = buffer.readBoolean();
        return new PlayerCastAbilityMessageCTS(ablId, primary);
    }

    public static void handle(PlayerCastAbilityMessageCTS msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(!context.getDirection().getReceptionSide().isClient()){
//                System.out.println("Receiving ability cast on server side");
                Player player = context.getSender();
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                    cap.getAbilitiesManager().useAbility(cap, player, msg.ablId, false, msg.primary);
                });
            } else {
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientCastAbilityMessage.handlePacket(msg, contextSupplier)));
            }
        });

        context.setPacketHandled(true);
    }
}

@OnlyIn(Dist.CLIENT)
class ClientCastAbilityMessage
{
    public static void handlePacket(PlayerCastAbilityMessageCTS msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
//                ClientAbilitiesData.setAbilities(msg.list.stream().map(Ability::getInfo).toList());
        Player player = Minecraft.getInstance().player;
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getAbilitiesManager().useAbility(cap, player, msg.ablId, false, msg.primary);
        });
    }

}
