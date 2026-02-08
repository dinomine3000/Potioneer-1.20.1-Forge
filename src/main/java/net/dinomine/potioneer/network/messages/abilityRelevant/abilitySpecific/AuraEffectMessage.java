package net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific;

import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientHudData;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class AuraEffectMessage {
    public UUID entityId;

    public AuraEffectMessage(UUID enforcerId){
        this.entityId = enforcerId;
    }

    public static void encode(AuraEffectMessage msg, FriendlyByteBuf buf){
        buf.writeUUID(msg.entityId);
    }

    public static AuraEffectMessage decode(FriendlyByteBuf buffer){
        return new AuraEffectMessage(buffer.readUUID());
    }

    public static void handle(AuraEffectMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAuraData.handlePacket(msg)));
            }
        });

        context.setPacketHandled(true);
    }
}


@OnlyIn(Dist.CLIENT)
class ClientAuraData
{

    public static void handlePacket(AuraEffectMessage msg)
    {
        ClientAbilitiesData.AbilitySpecific.addEnforcerAura(msg.entityId);
    }
}