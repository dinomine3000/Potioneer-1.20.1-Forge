package net.dinomine.potioneer.beyonder.abilities;

import net.minecraft.network.FriendlyByteBuf;

public record AbilityInfo(int posX, int posY, String name) {

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(posX);
        buffer.writeInt(posY);
        buffer.writeInt(name.length());
        for(Character c : name.toCharArray()){
            buffer.writeChar(c);
        }
    }

    public static AbilityInfo decode(FriendlyByteBuf buffer){
        int x = buffer.readInt();
        int y = buffer.readInt();
        int size = buffer.readInt();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < size; i++){
            stringBuilder.append(buffer.readChar());
        }
        return new AbilityInfo(x, y, stringBuilder.toString());
    }
}
