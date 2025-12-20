package net.dinomine.potioneer.network;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.network.messages.*;
import net.dinomine.potioneer.network.messages.AllySystem.AllyChangeMessageC2S;
import net.dinomine.potioneer.network.messages.AllySystem.AllyGroupSyncMessage;
import net.dinomine.potioneer.network.messages.abilityRelevant.*;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.EvaporateEffect;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.OpenDivinationScreenSTC;
import net.dinomine.potioneer.network.messages.abilityRelevant.abilitySpecific.WaterPrisonEffectSTC;
import net.dinomine.potioneer.network.messages.advancement.AdvancementFailMessageCTS;
import net.dinomine.potioneer.network.messages.advancement.PlayerAdvanceMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Potioneer.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
            );

    //TODO can improve performance by removing messages that try to sync the capability between client and server
    //the beyonder capability on client is mostly redundant, all calculations are performed on server and the needed
    //info is then synched to client-side classes for player use
    public static void init() {
        int i = 0;
        INSTANCE.registerMessage(i++, PlayerAdvanceMessage.class, PlayerAdvanceMessage::encode, PlayerAdvanceMessage::decode, PlayerAdvanceMessage::handle);
        INSTANCE.registerMessage(i++, SequenceSTCSyncRequest.class, SequenceSTCSyncRequest::encode, SequenceSTCSyncRequest::decode, SequenceSTCSyncRequest::handle);
        INSTANCE.registerMessage(i++, PlayerSTCHudStatsSync.class, PlayerSTCHudStatsSync::encode, PlayerSTCHudStatsSync::decode, PlayerSTCHudStatsSync::handle);
        INSTANCE.registerMessage(i++, PlayerMiningSpeedSync.class, PlayerMiningSpeedSync::encode, PlayerMiningSpeedSync::decode, PlayerMiningSpeedSync::handle);
        INSTANCE.registerMessage(i++, PlayerCastAbilityMessageCTS.class, PlayerCastAbilityMessageCTS::encode, PlayerCastAbilityMessageCTS::decode, PlayerCastAbilityMessageCTS::handle);
        INSTANCE.registerMessage(i++, PlayerAbilityInfoSyncSTC.class, PlayerAbilityInfoSyncSTC::encode, PlayerAbilityInfoSyncSTC::decode, PlayerAbilityInfoSyncSTC::handle);
        INSTANCE.registerMessage(i++, PlayerAbilityCooldownSTC.class, PlayerAbilityCooldownSTC::encode, PlayerAbilityCooldownSTC::decode, PlayerAbilityCooldownSTC::handle);
        INSTANCE.registerMessage(i++, PlayerFormulaScreenSTCMessage.class, PlayerFormulaScreenSTCMessage::encode, PlayerFormulaScreenSTCMessage::decode, PlayerFormulaScreenSTCMessage::handle);
        INSTANCE.registerMessage(i++, PlayerSyncHotbarMessage.class, PlayerSyncHotbarMessage::encode, PlayerSyncHotbarMessage::decode, PlayerSyncHotbarMessage::handle);
        INSTANCE.registerMessage(i++, OpenDivinationScreenSTC.class, OpenDivinationScreenSTC::encode, OpenDivinationScreenSTC::decode, OpenDivinationScreenSTC::handle);
        INSTANCE.registerMessage(i++, WaterPrisonEffectSTC.class, WaterPrisonEffectSTC::encode, WaterPrisonEffectSTC::decode, WaterPrisonEffectSTC::handle);
        INSTANCE.registerMessage(i++, PlayerAttributesSyncMessageSTC.class, PlayerAttributesSyncMessageSTC::encode, PlayerAttributesSyncMessageSTC::decode, PlayerAttributesSyncMessageSTC::handle);
        INSTANCE.registerMessage(i++, EvaporateEffect.class, EvaporateEffect::encode, EvaporateEffect::decode, EvaporateEffect::handle);
        INSTANCE.registerMessage(i++, AdvancementFailMessageCTS.class, AdvancementFailMessageCTS::encode, AdvancementFailMessageCTS::decode, AdvancementFailMessageCTS::handle);
        INSTANCE.registerMessage(i++, PlayerArtifactSyncSTC.class, PlayerArtifactSyncSTC::encode, PlayerArtifactSyncSTC::decode, PlayerArtifactSyncSTC::handle);
        INSTANCE.registerMessage(i++, RitualC2STextSync.class, RitualC2STextSync::encode, RitualC2STextSync::decode, RitualC2STextSync::handle);
        INSTANCE.registerMessage(i++, AllyChangeMessageC2S.class, AllyChangeMessageC2S::encode, AllyChangeMessageC2S::decode, AllyChangeMessageC2S::handle);
        INSTANCE.registerMessage(i++, AllyGroupSyncMessage.class, AllyGroupSyncMessage::encode, AllyGroupSyncMessage::decode, AllyGroupSyncMessage::handle);
    }

    public static <T> void sendMessageSTC(T message, Player player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), message);
    }

    public static <T> void sendMessageCTS(T message){
        INSTANCE.sendToServer(message);
    }
}
