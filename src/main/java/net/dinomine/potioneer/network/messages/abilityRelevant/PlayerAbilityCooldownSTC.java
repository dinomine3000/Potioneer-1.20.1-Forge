package net.dinomine.potioneer.network.messages.abilityRelevant;

import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

//used to sync ability cooldowns from server to client
public class PlayerAbilityCooldownSTC {
    public String cAblId;
    public int cd;
    public int maxCd;

    public PlayerAbilityCooldownSTC(String cAblId, int cd, int maxCd){
        this.cd = cd;
        this.cAblId = cAblId;
        this.maxCd = maxCd;
    }

    public PlayerAbilityCooldownSTC(String cAblId, int cd){
        this.cd = cd;
        this.cAblId = cAblId;
        this.maxCd = cd;
    }

    public static void encode(PlayerAbilityCooldownSTC msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.cd);
        buffer.writeInt(msg.maxCd);
        BufferUtils.writeStringToBuffer(msg.cAblId, buffer);
    }

    public static PlayerAbilityCooldownSTC decode(FriendlyByteBuf buffer){
        int cd = buffer.readInt();
        int maxCd = buffer.readInt();
        String cAblId = BufferUtils.readString(buffer);
        return new PlayerAbilityCooldownSTC(cAblId, cd, maxCd);
    }

    public static void handle(PlayerAbilityCooldownSTC msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAbilityCooldownSTC.handlePacket(msg, contextSupplier));
        });

        context.setPacketHandled(true);
    }

}

@OnlyIn(Dist.CLIENT)
class ClientAbilityCooldownSTC
{
    public static void handlePacket(PlayerAbilityCooldownSTC msg, Supplier<NetworkEvent.Context> contextSupplier)
    {
//                ClientAbilitiesData.setAbilities(msg.list.stream().map(Ability::getInfo).toList());
        ClientAbilitiesData.setCooldown(msg.cAblId, msg.cd, msg.maxCd);
    }

}