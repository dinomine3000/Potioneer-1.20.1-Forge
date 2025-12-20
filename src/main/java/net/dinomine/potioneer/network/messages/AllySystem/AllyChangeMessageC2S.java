package net.dinomine.potioneer.network.messages.AllySystem;

import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AllyChangeMessageC2S {
    public boolean joinGroup;
    public boolean createGroup;
    public String groupName;
    public String groupPassword;

    public static AllyChangeMessageC2S createGroup(String name, String pass){
        return new AllyChangeMessageC2S(false, true, name, pass);
    }

    public static AllyChangeMessageC2S leaveGroup(String name){
        return new AllyChangeMessageC2S(false, false, name, "");
    }

    public static AllyChangeMessageC2S joinGroup(String name, String pass){
        return new AllyChangeMessageC2S(true, false, name, pass);
    }

    private AllyChangeMessageC2S(boolean join, boolean create, String name, String pass){
        this.joinGroup = join;
        this.createGroup = create;
        this.groupName = name;
        this.groupPassword = pass;
    }

    public static void encode(AllyChangeMessageC2S msg, FriendlyByteBuf buffer){
        buffer.writeBoolean(msg.joinGroup);
        buffer.writeBoolean(msg.createGroup);
        BufferUtils.writeStringToBuffer(msg.groupName, buffer);
        BufferUtils.writeStringToBuffer(msg.groupPassword, buffer);
    }

    public static AllyChangeMessageC2S decode(FriendlyByteBuf buffer){
        boolean join = buffer.readBoolean();
        boolean create = buffer.readBoolean();
        String name = BufferUtils.readString(buffer);
        String pass = BufferUtils.readString(buffer);
        return new AllyChangeMessageC2S(join, create, name, pass);
    }

    public static void handle(AllyChangeMessageC2S msg, Supplier<NetworkEvent.Context> contextSupplier){

        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();
        ServerLevel level = (ServerLevel) player.level();
        //Server receives message
        context.enqueueWork(() -> {
            AllySystemSaveData data = AllySystemSaveData.from(level);
            if(msg.joinGroup){
                data.tryAddPlayer(msg.groupName, player.getUUID(), msg.groupPassword);
            } else if(msg.createGroup){
                boolean createRest = data.createGroup(msg.groupName, msg.groupPassword, player.getUUID());
                System.out.println("Group creation attempt success: " + createRest);
            } else {
                boolean leaveRes = data.removePlayer(msg.groupName, player.getUUID());
                System.out.println("Leaving group " + msg.groupName + " success: " + leaveRes);
            }
        });

        context.setPacketHandled(true);
    }
}
