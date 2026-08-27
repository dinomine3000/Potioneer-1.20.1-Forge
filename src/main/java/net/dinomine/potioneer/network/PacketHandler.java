package net.dinomine.potioneer.network;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.network.messages.AllySystem.AllyChangeMessageC2S;
import net.dinomine.potioneer.network.messages.AllySystem.AllyGroupSyncMessage;
import net.dinomine.potioneer.network.messages.*;
import net.dinomine.potioneer.network.messages.abilityRelevant.*;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.*;
import net.dinomine.potioneer.network.messages.effects.*;
import net.dinomine.potioneer.network.messages.advancement.AdvancementFailMessageCTS;
import net.dinomine.potioneer.network.messages.advancement.BeginAdvancementMessage;
import net.dinomine.potioneer.network.messages.advancement.PlayerAdvanceMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Collection;
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
        INSTANCE.registerMessage(i++, PlayerSTCStatsSync.class, PlayerSTCStatsSync::encode, PlayerSTCStatsSync::decode, PlayerSTCStatsSync::handle);
        INSTANCE.registerMessage(i++, RitualC2STextSync.class, RitualC2STextSync::encode, RitualC2STextSync::decode, RitualC2STextSync::handle);
        INSTANCE.registerMessage(i++, SequenceSTCSyncRequest.class, SequenceSTCSyncRequest::encode, SequenceSTCSyncRequest::decode, SequenceSTCSyncRequest::handle);
        INSTANCE.registerMessage(i++, PhotonFxMessage.class, PhotonFxMessage::encode, PhotonFxMessage::decode, PhotonFxMessage::handle);
        INSTANCE.registerMessage(i++, EntityEffectVisualMessage.class, EntityEffectVisualMessage::encode, EntityEffectVisualMessage::decode, EntityEffectVisualMessage::handle);
        INSTANCE.registerMessage(i++, OpenContractScreenMessage.class, OpenContractScreenMessage::encode, OpenContractScreenMessage::decode, OpenContractScreenMessage::handle);
        INSTANCE.registerMessage(i++, SignContractMessage.class, SignContractMessage::encode, SignContractMessage::decode, SignContractMessage::handle);
        INSTANCE.registerMessage(i++, RulePylonMessage.class, RulePylonMessage::encode, RulePylonMessage::decode, RulePylonMessage::handle);
        INSTANCE.registerMessage(i++, DoubleJumpMessage.class, DoubleJumpMessage::encode, DoubleJumpMessage::decode, DoubleJumpMessage::handle);
        INSTANCE.registerMessage(i++, ElytraFlyMessage.class, ElytraFlyMessage::encode, ElytraFlyMessage::decode, ElytraFlyMessage::handle);
    }
    public static <T> void sendToPlayer(T message, ServerPlayer player) {
        if (player.connection == null) return;
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    /** Send to a list/collection of specific players (Server -> Client) */
    public static <T> void sendToPlayers(T message, Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            sendToPlayer(message, player);
        }
    }

    /** Send to all players connected to the server (Server -> Client) */
    public static <T> void sendToAll(T message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    /** Send to all players tracking a specific entity (Server -> Client) */
    public static <T> void sendToTrackingEntity(T message, Entity entity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }

    /** Send to all players tracking a specific entity AND the entity itself if it's a player */
    public static <T> void sendToTrackingEntityAndSelf(T message, Entity entity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
    }

    /** Send to all players near a position in a world dimension */
    public static <T> void sendToNear(T message, ResourceKey<Level> dimension, double x, double y, double z, double radius) {
        INSTANCE.send(PacketDistributor.NEAR.with(() ->
                new PacketDistributor.TargetPoint(x, y, z, radius, dimension)
        ), message);
    }

    /** Send to all players in a specific dimension */
    public static <T> void sendToDimension(T message, ResourceKey<Level> dimension) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

    /** Send from Client to Server */
    public static <T> void sendToServer(T message) {
        INSTANCE.sendToServer(message);
    }

    public static <T> void sendMessageSTC(T message, LivingEntity player) {
        if (player.level().isClientSide()) return;
        if (player instanceof ServerPlayer sPlayer) {
            sendToPlayer(message, sPlayer);
        }
    }

    public static <T> void sendMessageCTS(T message) {
        sendToServer(message);
    }

    public static <T> void sendMessageToClientsAround(BlockPos pos, Level level, int radius, T message) {
        if (level.isClientSide()) return;
        sendToNear(message, level.dimension(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, radius);
    }

    public static <T> void sendMessageToClientsAround(Entity target, int radius, T message) {
        if (target.level().isClientSide()) return;
        Vec3 pos = target.position();
        sendToNear(message, target.level().dimension(), pos.x, pos.y, pos.z, radius);
    }
}
