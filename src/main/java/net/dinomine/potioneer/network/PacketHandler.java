package net.dinomine.potioneer.network;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.dinomine.potioneer.network.messages.SequenceSTCSyncRequest;
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

    public static void init() {
        INSTANCE.registerMessage(0, PlayerAdvanceMessage.class, PlayerAdvanceMessage::encode, PlayerAdvanceMessage::decode, PlayerAdvanceMessage::handle);
        INSTANCE.registerMessage(1, SequenceSTCSyncRequest.class, SequenceSTCSyncRequest::encode, SequenceSTCSyncRequest::decode, SequenceSTCSyncRequest::handle);
    }
}
