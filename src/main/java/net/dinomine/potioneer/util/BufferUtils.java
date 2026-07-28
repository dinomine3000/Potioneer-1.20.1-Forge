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

    public static <T> void writeList(List<T> list, FriendlyByteBuf buffer, FriendlyByteBuf.Writer<T> writer){
        buffer.writeInt(list.size());
        for (T t : list) writer.accept(buffer, t);
    }

    public static <T> List<T> readList(FriendlyByteBuf buffer, FriendlyByteBuf.Reader<T> reader){
        List<T> list = new ArrayList<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++){
            list.add(reader.apply(buffer));
        }
        return list;
    }

    public static void writeIntListToBuffer(List<Integer> list, FriendlyByteBuf buffer){
        writeList(list, buffer, FriendlyByteBuf::writeInt);
    }

    public static List<Integer> readIntListFromBuffer(FriendlyByteBuf buffer){
        return readList(buffer, FriendlyByteBuf::readInt);
    }
}
