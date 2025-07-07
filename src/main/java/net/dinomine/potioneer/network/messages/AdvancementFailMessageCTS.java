package net.dinomine.potioneer.network.messages;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.network.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

//sent from client to server on world join to request a STC sync
public class AdvancementFailMessageCTS {
    public int sequence;

    public AdvancementFailMessageCTS(int failedSequence){
        this.sequence = failedSequence;
    }

    public static void encode(AdvancementFailMessageCTS msg, FriendlyByteBuf buffer){
        buffer.writeInt(msg.sequence);
    }

    public static AdvancementFailMessageCTS decode(FriendlyByteBuf buffer){
        return new AdvancementFailMessageCTS(buffer.readInt());
    }

    public static void handle(AdvancementFailMessageCTS msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) context.getSender();
        //Server receives message
        context.enqueueWork(() -> {
            int sequence = msg.sequence;
            if(!player.isCreative()){
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> cap.setSanity(0));
                player.kill();
            }
            ItemStack characteristic = new ItemStack(ModItems.CHARACTERISTIC.get());
            CompoundTag root = new CompoundTag();

            CompoundTag charInfo = new CompoundTag();
            charInfo.putInt("id", sequence);
            root.put("beyonder_info", charInfo);
            characteristic.setTag(root);

            MysticismHelper.updateOrApplyMysticismTag(characteristic, 20, player);


            CharacteristicEntity entity = new CharacteristicEntity(ModEntities.CHARACTERISTIC.get(), player.level(), characteristic.copy(), sequence);
            entity.setSequenceId(sequence);
            entity.moveTo(player.position().offsetRandom(player.getRandom(), 0.5f));
            player.level().addFreshEntity(entity);
            System.out.println("Added potion characteristic on server side");
        });

        context.setPacketHandled(true);
    }

}

