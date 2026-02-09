package net.dinomine.potioneer.network;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.network.messages.AllySystem.AllyChangeMessageC2S;
import net.dinomine.potioneer.network.messages.AllySystem.AllyGroupSyncMessage;
import net.dinomine.potioneer.network.messages.*;
import net.dinomine.potioneer.network.messages.abilityRelevant.*;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.AppraisalDataMessage;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.AuraEffectMessage;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.SourceRecipientUpdateMessage;
import net.dinomine.potioneer.network.messages.effects.EvaporateEffect;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.network.messages.effects.WaterPrisonEffectSTC;
import net.dinomine.potioneer.network.messages.advancement.AdvancementFailMessageCTS;
import net.dinomine.potioneer.network.messages.advancement.BeginAdvancementMessage;
import net.dinomine.potioneer.network.messages.advancement.PlayerAdvanceMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Potioneer.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
            );

    public static void init() {
        int i = 0;
        //ability relevant
        INSTANCE.registerMessage(i++, WaterPrisonEffectSTC.class, WaterPrisonEffectSTC::encode, WaterPrisonEffectSTC::decode, WaterPrisonEffectSTC::handle);
        INSTANCE.registerMessage(i++, EvaporateEffect.class, EvaporateEffect::encode, EvaporateEffect::decode, EvaporateEffect::handle);
        INSTANCE.registerMessage(i++, GeneralAreaEffectMessage.class, GeneralAreaEffectMessage::encode, GeneralAreaEffectMessage::decode, GeneralAreaEffectMessage::handle);
        INSTANCE.registerMessage(i++, AppraisalDataMessage.class, AppraisalDataMessage::encode, AppraisalDataMessage::decode, AppraisalDataMessage::handle);
        INSTANCE.registerMessage(i++, AuraEffectMessage.class, AuraEffectMessage::encode, AuraEffectMessage::decode, AuraEffectMessage::handle);
        INSTANCE.registerMessage(i++, SourceRecipientUpdateMessage.class, SourceRecipientUpdateMessage::encode, SourceRecipientUpdateMessage::decode, SourceRecipientUpdateMessage::handle);

        INSTANCE.registerMessage(i++, AbilitySyncMessage.class, AbilitySyncMessage::encode, AbilitySyncMessage::decode, AbilitySyncMessage::handle);
        INSTANCE.registerMessage(i++, BeyonderEffectSyncMessage.class, BeyonderEffectSyncMessage::encode, BeyonderEffectSyncMessage::decode, BeyonderEffectSyncMessage::handle);
        INSTANCE.registerMessage(i++, PlayerArtifactSyncSTC.class, PlayerArtifactSyncSTC::encode, PlayerArtifactSyncSTC::decode, PlayerArtifactSyncSTC::handle);
        INSTANCE.registerMessage(i++, PlayerCastAbilityMessageCTS.class, PlayerCastAbilityMessageCTS::encode, PlayerCastAbilityMessageCTS::decode, PlayerCastAbilityMessageCTS::handle);
        INSTANCE.registerMessage(i++, PlayerSyncHotbarMessage.class, PlayerSyncHotbarMessage::encode, PlayerSyncHotbarMessage::decode, PlayerSyncHotbarMessage::handle);

        INSTANCE.registerMessage(i++, AdvancementFailMessageCTS.class, AdvancementFailMessageCTS::encode, AdvancementFailMessageCTS::decode, AdvancementFailMessageCTS::handle);
        INSTANCE.registerMessage(i++, PlayerAdvanceMessage.class, PlayerAdvanceMessage::encode, PlayerAdvanceMessage::decode, PlayerAdvanceMessage::handle);
        INSTANCE.registerMessage(i++, BeginAdvancementMessage.class, BeginAdvancementMessage::encode, BeginAdvancementMessage::decode, BeginAdvancementMessage::handle);

        INSTANCE.registerMessage(i++, AllyChangeMessageC2S.class, AllyChangeMessageC2S::encode, AllyChangeMessageC2S::decode, AllyChangeMessageC2S::handle);
        INSTANCE.registerMessage(i++, AllyGroupSyncMessage.class, AllyGroupSyncMessage::encode, AllyGroupSyncMessage::decode, AllyGroupSyncMessage::handle);

        INSTANCE.registerMessage(i++, OpenScreenMessage.class, OpenScreenMessage::encode, OpenScreenMessage::decode, OpenScreenMessage::handle);
        INSTANCE.registerMessage(i++, PlayerMiningSpeedSync.class, PlayerMiningSpeedSync::encode, PlayerMiningSpeedSync::decode, PlayerMiningSpeedSync::handle);
        INSTANCE.registerMessage(i++, PlayerSTCStatsSync.class, PlayerSTCStatsSync::encode, PlayerSTCStatsSync::decode, PlayerSTCStatsSync::handle);
        INSTANCE.registerMessage(i++, RitualC2STextSync.class, RitualC2STextSync::encode, RitualC2STextSync::decode, RitualC2STextSync::handle);
        INSTANCE.registerMessage(i++, SequenceSTCSyncRequest.class, SequenceSTCSyncRequest::encode, SequenceSTCSyncRequest::decode, SequenceSTCSyncRequest::handle);

        INSTANCE.registerMessage(i++, PlayerNameSyncMessage.class, PlayerNameSyncMessage::encode, PlayerNameSyncMessage::decode, PlayerNameSyncMessage::handle);
    }

    public static <T> void sendMessageSTC(T message, LivingEntity player){
        if(player.level().isClientSide) return;
        if(!(player instanceof ServerPlayer sPlayer)) return;
        if(sPlayer.connection == null) return;
        INSTANCE.send(PacketDistributor.PLAYER.with(() ->  sPlayer), message);
    }

    public static <T> void sendMessageCTS(T message){
        INSTANCE.sendToServer(message);
    }

    public static <T> void sendMessageToClientsAround(BlockPos pos, Level level, int radius, T message) {
        List<LivingEntity> entities = AbilityFunctionHelper.getLivingEntitiesAround(pos, level, radius);
        for(LivingEntity ent: entities){
            sendMessageSTC(message, ent);
        }
    }
    public static <T> void sendMessageToClientsAround(LivingEntity target, int radius, T message) {
        List<LivingEntity> entities = AbilityFunctionHelper.getLivingEntitiesAround(target, radius);
        for(LivingEntity ent: entities){
            sendMessageSTC(message, ent);
        }
    }
}
