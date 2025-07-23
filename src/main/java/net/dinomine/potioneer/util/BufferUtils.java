package net.dinomine.potioneer.util;

import net.minecraft.network.FriendlyByteBuf;

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
}
