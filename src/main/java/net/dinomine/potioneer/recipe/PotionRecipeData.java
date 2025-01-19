package net.dinomine.potioneer.recipe;

import net.dinomine.potioneer.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;

public class PotionRecipeData {

    public PotionRecipeData(String name, int amount, boolean bottle, int color, boolean canConflict) {
        this.name = name;
        this.amount = amount;
        this.bottle = bottle;
        this.color = color;
        this.canConflict = canConflict;
    }

    public void save(CompoundTag tag){
        tag.putString("name", name);
        tag.putInt("amount", amount);
        tag.putBoolean("bottle", bottle);
        tag.putInt("color", color);
        tag.putBoolean("canConflict", canConflict);
    }

    public static PotionRecipeData load(CompoundTag tag){
        return new PotionRecipeData(tag.getString("name"), tag.getInt("amount"),
                tag.getBoolean("bottle"), tag.getInt("color"), tag.getBoolean("canConflict"));
    }

    public static PotionRecipeData EMPTY = new PotionRecipeData("EMPTY", 0, false, 0, false);

    public static PotionRecipeData getConflictingResult(boolean bottle){
        int color = (int) (0x00FFFFFF * Math.random());
        return new PotionRecipeData("conflict", 0, bottle, color, false);
    }

    public PotionRecipeData copy(){
        return new PotionRecipeData(this.name, this.amount, this.bottle, this.color, this.canConflict);
    }

    public boolean isEmpty(){
        return Objects.equals(this.name, "EMPTY");
    }

    public String name;
    public int amount;
    public boolean bottle;
    public int color;
    public boolean canConflict;

    public boolean  isValidContainer(ItemStack stack){
        Item item = stack.getItem();
        System.out.println("analysing item: " + item);
        if(item == Items.GLASS_BOTTLE && this.bottle
                || ((item == ModItems.VIAL.get() || item == ModItems.FLASK.get()) && !this.bottle)) {
            if(stack.getTag() != null){
                System.out.println("tag isnt null");
                CompoundTag info = stack.getTag().getCompound("potion_info");
                System.out.println("info gotten: " + info);
                int level = info.getInt("amount");
                System.out.println("amount gotten: " + level);
                if(item == ModItems.FLASK.get()){
                    return level < 2;
                } else {
                    return level < 1;
                }
            } else {
                return true;
            }
        } return false;
    }

    private void writeStringIntoBuffer(FriendlyByteBuf friendlyByteBuf, String string){
        friendlyByteBuf.writeByte(string.length());
        for(Character c : string.toCharArray()){
            friendlyByteBuf.writeChar(c);
        }
    }

    private static String readStringFromBuffer(FriendlyByteBuf friendlyByteBuf){
        int length = friendlyByteBuf.readByte();
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < length; i++){
            res.append(friendlyByteBuf.readChar());
        }
        return res.toString();
    }

    public void writeIntoByteBuf(FriendlyByteBuf friendlyByteBuf){
        writeStringIntoBuffer(friendlyByteBuf, this.name);
        friendlyByteBuf.writeInt(this.amount);
        friendlyByteBuf.writeBoolean(this.bottle);
        friendlyByteBuf.writeInt(this.color);
        friendlyByteBuf.writeBoolean(this.canConflict);
    }

    public static PotionRecipeData readFromByteBuf(FriendlyByteBuf friendlyByteBuf){
        String name = readStringFromBuffer(friendlyByteBuf);
        int amount = friendlyByteBuf.readInt();
        boolean bottle = friendlyByteBuf.readBoolean();
        int color = friendlyByteBuf.readInt();
        boolean canConflict = friendlyByteBuf.readBoolean();
        return new PotionRecipeData(name, amount, bottle, color, canConflict);
    }

}
