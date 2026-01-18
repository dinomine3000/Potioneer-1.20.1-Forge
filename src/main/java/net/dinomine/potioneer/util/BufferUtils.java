package net.dinomine.potioneer.util;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public class BufferUtils {

    public static void writeStringToBuffer(String string, FriendlyByteBuf buf){
        buf.writeInt(string.length());
        for(int i = 0; i < string.length(); i++){
            buf.writeChar(string.charAt(i));
        }
    }

    public static String readString(FriendlyByteBuf buf){
        int size = buf.readInt();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++){
            builder.append(buf.readChar());
        }
        return builder.toString();
    }

    public static void writeIntListToBuffer(List<Integer> list, FriendlyByteBuf buffer){
        buffer.writeInt(list.size());
        for(int i = 0; i < list.size(); i++){
            buffer.writeInt(list.get(i));
        }
    }

    public static List<Integer> readIntListFromBuffer(FriendlyByteBuf buffer){
        List<Integer> list = new ArrayList<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++){
            list.add(buffer.readInt());
        }
        return list;
    }
}
