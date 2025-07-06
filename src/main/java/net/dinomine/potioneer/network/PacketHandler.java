package net.dinomine.potioneer.network;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.network.messages.*;
import net.dinomine.potioneer.network.messages.abilityRelevant.EvaporateEffect;
import net.dinomine.potioneer.network.messages.abilityRelevant.OpenDivinationScreenSTC;
import net.dinomine.potioneer.network.messages.abilityRelevant.WaterPrisonEffectSTC;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
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
        INSTANCE.registerMessage(i++, PlayerStatsSyncMessage.class, PlayerStatsSyncMessage::encode, PlayerStatsSyncMessage::decode, PlayerStatsSyncMessage::handle);
        INSTANCE.registerMessage(i++, PlayerCastAbilityMessageCTS.class, PlayerCastAbilityMessageCTS::encode, PlayerCastAbilityMessageCTS::decode, PlayerCastAbilityMessageCTS::handle);
        INSTANCE.registerMessage(i++, PlayerAbilityInfoSyncSTC.class, PlayerAbilityInfoSyncSTC::encode, PlayerAbilityInfoSyncSTC::decode, PlayerAbilityInfoSyncSTC::handle);
        INSTANCE.registerMessage(i++, PlayerAbilityCooldownSTC.class, PlayerAbilityCooldownSTC::encode, PlayerAbilityCooldownSTC::decode, PlayerAbilityCooldownSTC::handle);
        INSTANCE.registerMessage(i++, PlayerFormulaScreenSTCMessage.class, PlayerFormulaScreenSTCMessage::encode, PlayerFormulaScreenSTCMessage::decode, PlayerFormulaScreenSTCMessage::handle);
        INSTANCE.registerMessage(i++, PlayerSyncHotbarMessage.class, PlayerSyncHotbarMessage::encode, PlayerSyncHotbarMessage::decode, PlayerSyncHotbarMessage::handle);
        INSTANCE.registerMessage(i++, OpenDivinationScreenSTC.class, OpenDivinationScreenSTC::encode, OpenDivinationScreenSTC::decode, OpenDivinationScreenSTC::handle);
        INSTANCE.registerMessage(i++, WaterPrisonEffectSTC.class, WaterPrisonEffectSTC::encode, WaterPrisonEffectSTC::decode, WaterPrisonEffectSTC::handle);
        INSTANCE.registerMessage(i++, PlayerStatsMessageSTC.class, PlayerStatsMessageSTC::encode, PlayerStatsMessageSTC::decode, PlayerStatsMessageSTC::handle);
        INSTANCE.registerMessage(i++, EvaporateEffect.class, EvaporateEffect::encode, EvaporateEffect::decode, EvaporateEffect::handle);
        INSTANCE.registerMessage(i++, AdvancementFailMessageCTS.class, AdvancementFailMessageCTS::encode, AdvancementFailMessageCTS::decode, AdvancementFailMessageCTS::handle);
    }
}
