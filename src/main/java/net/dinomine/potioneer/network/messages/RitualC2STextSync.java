package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.block.entity.RitualAltarBlockEntity;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RitualC2STextSync {
    public BlockPos pos;
    public String firstVerse;
    public String secondVerse;
    public String thirdVerse;

    public RitualC2STextSync(BlockPos pos, String firstVerse, String secondVerse, String thirdVerse){
        this.pos = pos;
        this.firstVerse = firstVerse;
        this.secondVerse = secondVerse;
        this.thirdVerse = thirdVerse;
    }

    public static void encode(RitualC2STextSync msg, FriendlyByteBuf buffer){
        buffer.writeBlockPos(msg.pos);
        BufferUtils.writeStringToBuffer(msg.firstVerse, buffer);
        BufferUtils.writeStringToBuffer(msg.secondVerse, buffer);
        BufferUtils.writeStringToBuffer(msg.thirdVerse, buffer);
    }

    public static RitualC2STextSync decode(FriendlyByteBuf buffer){
        return new RitualC2STextSync(buffer.readBlockPos(), BufferUtils.readString(buffer), BufferUtils.readString(buffer), BufferUtils.readString(buffer));
    }

    public static void handle(RitualC2STextSync msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();

        //potion advancement
        context.enqueueWork(() -> {
            if(!context.getDirection().getReceptionSide().isClient()){
                if(context.getSender().level().getBlockEntity(msg.pos) instanceof RitualAltarBlockEntity be){
                    be.updateVerses(msg.firstVerse, msg.secondVerse, msg.thirdVerse);
                }
            }
        });

        context.setPacketHandled(true);
    }
}
