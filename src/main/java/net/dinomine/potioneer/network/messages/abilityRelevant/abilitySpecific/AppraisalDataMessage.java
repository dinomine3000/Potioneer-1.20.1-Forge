package net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific;

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

import java.util.Optional;
import java.util.function.Supplier;

public class AppraisalDataMessage {
    public float[] data;
    public int entityId;
    public boolean luck;
    public boolean resetHudTimer = true;

    public AppraisalDataMessage(int entityId, float[] data, boolean luck){
        this.data = data;
        this.entityId = entityId;
        this.luck = luck;
    }

    public AppraisalDataMessage resetTimer(boolean override){
        this.resetHudTimer = override;
        return this;
    }

    public AppraisalDataMessage(LivingEntity entity, boolean luck){
        this.entityId = entity.getId();
        this.luck = luck;

        Optional<LivingEntityBeyonderCapability> optCap = entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        if(optCap.isPresent()){
            LivingEntityBeyonderCapability cap = optCap.get();
            if(luck){
                this.data = cap.getLuckManager().getDataForHud();
            } else {
                this.data = new float[]{entity.getHealth(), entity.getMaxHealth(), cap.getSpirituality(), cap.getMaxSpirituality(), cap.getSanity(), cap.getMaxSanity()};
            }
        } else {
            this.data = new float[0];
        }
    }

    public static void encode(AppraisalDataMessage msg, FriendlyByteBuf buf){
        buf.writeInt(msg.data.length);
        for(int i = 0; i < msg.data.length; i++){
            buf.writeFloat(msg.data[i]);
        }
        buf.writeInt(msg.entityId);
        buf.writeBoolean(msg.luck);
        buf.writeBoolean(msg.resetHudTimer);
    }

    public static AppraisalDataMessage decode(FriendlyByteBuf buffer){
        int size = buffer.readInt();
        float[] values = new float[size];
        for(int i = 0; i < size; i++){
            values[i] = buffer.readFloat();
        }
        int entityId = buffer.readInt();
        boolean luck = buffer.readBoolean();
        boolean resetHudTimer = buffer.readBoolean();
        return new AppraisalDataMessage(entityId, values, luck).resetTimer(resetHudTimer);
    }

    public static void handle(AppraisalDataMessage msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isClient()){
                context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAppraisalData.handlePacket(msg)));
            } else {
                ServerPlayer player = context.getSender();
                ServerLevel level = (ServerLevel) player.level();
                Entity ent = level.getEntity(msg.entityId);
                if(ent instanceof LivingEntity living){
                    PacketHandler.sendMessageSTC(new AppraisalDataMessage(living, msg.luck).resetTimer(false), player);
                }
            }
        });

        context.setPacketHandled(true);
    }
}


@OnlyIn(Dist.CLIENT)
class ClientAppraisalData
{

    public static void handlePacket(AppraisalDataMessage msg)
    {
        ClientHudData.setAppraisalData(msg.entityId, msg.data, msg.luck);
        if(msg.resetHudTimer)
            ClientHudData.showLuckHud(msg.luck);
    }
}