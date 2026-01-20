package net.dinomine.potioneer.recipe;

import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;

public class PotionContentData {

    public PotionContentData(String name, int amount, boolean bottle, int color, boolean canConflict) {
        this(name, amount, bottle, color, canConflict, true);
    }

    private PotionContentData(String name, int amount, boolean bottle, int color, boolean canConflict, boolean isComplete) {
        this.name = name;
        this.amount = amount;
        this.bottle = bottle;
        this.color = color;
        this.canConflict = canConflict;
        this.isComplete = isComplete;
    }

    public PotionContentData setCompletionStatus(boolean isComplete){
        this.isComplete = isComplete;
        return this;
    }

    public CompoundTag save(CompoundTag tag){
        tag.putString("name", name);
        tag.putInt("amount", amount);
        tag.putBoolean("bottle", bottle);
        tag.putInt("color", color);
        tag.putBoolean("canConflict", canConflict);
        tag.putBoolean("isComplete", isComplete);
        return tag;
    }

    public static PotionContentData load(CompoundTag tag){
        return new PotionContentData(tag.getString("name"), tag.getInt("amount"),
                tag.getBoolean("bottle"), tag.getInt("color"), tag.getBoolean("canConflict"),
                !tag.contains("isComplete") || tag.getBoolean("isComplete"));
    }

    public static PotionContentData EMPTY = new PotionContentData("EMPTY", 0, false, 0, false, false);

    public static PotionContentData getConflictingResult(boolean bottle){
        int color = (int) (0x00FFFFFF * Math.random());
        return new PotionContentData("conflict", 0, bottle, color, false, false);
    }

    public static PotionContentData getIncompleteResult(boolean bottle){
        int color = (int) (0x00FFFFFF * Math.random());
        return new PotionContentData("awkward", 0, bottle, color, false, false);
    }

    public PotionContentData copy(){
        return new PotionContentData(this.name, this.amount, this.bottle, this.color, this.canConflict, this.isComplete);
    }

    public boolean isEmpty(){
        return Objects.equals(this.name, "EMPTY");
    }

    public String name;
    public int amount;
    public boolean bottle;
    public int color;
    public boolean canConflict;
    public boolean isComplete;

    public boolean isValidContainer(ItemStack stack){
        Item item = stack.getItem();
        if(item == Items.GLASS_BOTTLE && this.bottle
                || ((item == ModItems.VIAL.get() || item == ModItems.FLASK.get()) && !this.bottle)) {
            if(stack.getTag() != null && stack.getTag().contains("potion_info")){
                CompoundTag info = stack.getTag().getCompound("potion_info");
                int level = info.getInt("amount");
                if(item == ModItems.FLASK.get()){
                    return level < 2 && info.getString("name").equals(name);
                } else {
                    return level < 1;
                }
            } else {
                return true;
            }
        } return false;
    }

    public void writeIntoByteBuf(FriendlyByteBuf friendlyByteBuf){
        BufferUtils.writeStringToBuffer(this.name, friendlyByteBuf);
        friendlyByteBuf.writeInt(this.amount);
        friendlyByteBuf.writeBoolean(this.bottle);
        friendlyByteBuf.writeInt(this.color);
        friendlyByteBuf.writeBoolean(this.canConflict);
        friendlyByteBuf.writeBoolean(this.isComplete);
    }

    public static PotionContentData readFromByteBuf(FriendlyByteBuf friendlyByteBuf){
        String name = BufferUtils.readString(friendlyByteBuf);
        int amount = friendlyByteBuf.readInt();
        boolean bottle = friendlyByteBuf.readBoolean();
        int color = friendlyByteBuf.readInt();
        boolean canConflict = friendlyByteBuf.readBoolean();
        boolean isComplete = friendlyByteBuf.readBoolean();
        return new PotionContentData(name, amount, bottle, color, canConflict, isComplete);
    }

}
