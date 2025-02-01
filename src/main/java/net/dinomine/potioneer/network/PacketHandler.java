package net.dinomine.potioneer.network;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.network.messages.*;
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
        INSTANCE.registerMessage(0, PlayerAdvanceMessage.class, PlayerAdvanceMessage::encode, PlayerAdvanceMessage::decode, PlayerAdvanceMessage::handle);
        INSTANCE.registerMessage(1, SequenceSTCSyncRequest.class, SequenceSTCSyncRequest::encode, SequenceSTCSyncRequest::decode, SequenceSTCSyncRequest::handle);
        INSTANCE.registerMessage(2, PlayerSTCHudStatsSync.class, PlayerSTCHudStatsSync::encode, PlayerSTCHudStatsSync::decode, PlayerSTCHudStatsSync::handle);
        INSTANCE.registerMessage(3, PlayerStatsSyncMessage.class, PlayerStatsSyncMessage::encode, PlayerStatsSyncMessage::decode, PlayerStatsSyncMessage::handle);
        INSTANCE.registerMessage(4, PlayerCastAbilityMessageCTS.class, PlayerCastAbilityMessageCTS::encode, PlayerCastAbilityMessageCTS::decode, PlayerCastAbilityMessageCTS::handle);
        INSTANCE.registerMessage(5, PlayerAbilityInfoSyncSTC.class, PlayerAbilityInfoSyncSTC::encode, PlayerAbilityInfoSyncSTC::decode, PlayerAbilityInfoSyncSTC::handle);
        INSTANCE.registerMessage(6, PlayerAbilityCooldownSTC.class, PlayerAbilityCooldownSTC::encode, PlayerAbilityCooldownSTC::decode, PlayerAbilityCooldownSTC::handle);
        INSTANCE.registerMessage(7, PlayerFormulaScreenSTCMessage.class, PlayerFormulaScreenSTCMessage::encode, PlayerFormulaScreenSTCMessage::decode, PlayerFormulaScreenSTCMessage::handle);
        INSTANCE.registerMessage(8, PlayerSyncHotbarMessage.class, PlayerSyncHotbarMessage::encode, PlayerSyncHotbarMessage::decode, PlayerSyncHotbarMessage::handle);
    }
}
