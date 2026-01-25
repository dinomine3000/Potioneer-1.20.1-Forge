package net.dinomine.potioneer.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.Optional;

public class PotionIngredient {
    public static final PotionIngredient EMPTY = new PotionIngredient();
    private final ItemStack item;
    private final TagKey<Item> tag;
    private final int tagCount;

    public PotionIngredient(){
        this.item = null;
        this.tag = null;
        this.tagCount = 0;
    }

    public PotionIngredient(ItemStack stack){
        this.item = stack;
        this.tag = null;
        this.tagCount = 0;
    }

    public PotionIngredient(TagKey<Item> tag){
        this(tag, 1);
    }

    public PotionIngredient(TagKey<Item> tag, int count){
        this.item = null;
        this.tag = tag;
        this.tagCount = count;
    }


    public boolean isEmpty() {
        return item == null && tag == null;
    }

    public void writeToBuffer(FriendlyByteBuf buf){
        buf.writeBoolean(item == null && tag == null);
        if(item != null){
            buf.writeBoolean(true);
            buf.writeItem(item);
        } else {
            buf.writeBoolean(false);
            BufferUtils.writeStringToBuffer(tag.location().toString(), buf);
            buf.writeInt(tagCount);
        }
    }

    public static PotionIngredient readFromBuffer(FriendlyByteBuf buf){
        if(buf.readBoolean()) return EMPTY;
        if(buf.readBoolean()){
            ItemStack stack = buf.readItem();
            return new PotionIngredient(stack);
        } else {
            ResourceLocation location = new ResourceLocation(BufferUtils.readString(buf));
            TagKey<Item> tag = TagKey.create(Registries.ITEM, location);
            int tagCount = buf.readInt();
            return new PotionIngredient(tag, tagCount);
        }
    }

    public PotionIngredient withCount(int count){
        if(isTagIngredient()){
            return new PotionIngredient(tag, count);
        }
        if(isItemIngredient()){
            return new PotionIngredient(item.copyWithCount(count));
        }
        return EMPTY;
    }

    public static PotionIngredient fromJson(JsonElement jsonElement) {
        return valueFromJson(jsonElement.getAsJsonObject());
    }

    public static PotionIngredient valueFromJson(JsonObject pJson) {
        if (pJson.has("item") && pJson.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        } else if (pJson.has("item")) {
            ItemStack item = ShapedRecipe.itemStackFromJson(pJson.getAsJsonObject());
            return new PotionIngredient(item);
        } else if (pJson.has("tag")) {
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(pJson, "tag"));
            TagKey<Item> tag = TagKey.create(Registries.ITEM, resourcelocation);
            if(pJson.has("count")) return new PotionIngredient(tag, pJson.get("count").getAsInt());
            return new PotionIngredient(tag);
        } else {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
    }

    public void toNbt(CompoundTag temp) {
        if(item != null){
            CompoundTag itemTag = new CompoundTag();
            temp.put("item", item.save(itemTag));
            return;
        }
        if(tag != null){
            temp.putString("itemTag", tag.location().toString());
            temp.putInt("tagCount", tagCount);
        }
    }

    public static PotionIngredient fromNbt(CompoundTag temp) {
        if (!temp.contains("item") && !temp.contains("itemTag")) return EMPTY;
        if(temp.contains("item")){
            ItemStack stack = ItemStack.of(temp.getCompound("item"));
            return new PotionIngredient(stack);
        } else if (temp.contains("itemTag")){
            ResourceLocation resourcelocation = new ResourceLocation(temp.getString("itemTag"));
            TagKey<Item> tagkey = TagKey.create(Registries.ITEM, resourcelocation);
            int count = temp.getInt("tagCount");
            return new PotionIngredient(tagkey, count);
        }
        return EMPTY;
    }

    public int getCount() {
        if(item != null){
            return item.getCount();
        }
        if(tag != null){
            return tagCount;
        }
        return 0;
    }

    public boolean isItemIngredient(){
        return item != null;
    }

    public boolean isTagIngredient(){
        return tag != null;
    }

    public ItemStack getStack() {
        if(item != null) return item.copy();
        if(tag != null){
//            Optional<Holder<Item>> first = BuiltInRegistries.ITEM
//                    .getTag(tag)
//                    .flatMap(set -> set.stream().findFirst());
//
//            return first.map(ItemStack::new).orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getRepresentativeStack(){
        if(item != null) return item.copy();
        if(tag != null){
            Optional<Holder<Item>> first = BuiltInRegistries.ITEM
                    .getTag(tag)
                    .flatMap(set -> set.stream().findFirst());

            return first.map(ItemStack::new).orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    public TagKey<Item> getItemTag(){
        return tag;
    }

    public boolean is(ItemStack testItem) {
        if(isTagIngredient()){
            return testItem.is(tag);
        } else if (isItemIngredient()){
            boolean itemCheck = testItem.is(this.item.getItem());
            boolean tagCheck = !this.item.hasTag() || (testItem.hasTag() && containsTag(this.item, testItem));
            return itemCheck && tagCheck;
        }
        return false;
    }

    public static ItemStack getRepresentativeStackForTag(TagKey<Item> tag){
        return ItemStack.EMPTY;
    }

    public static boolean contains(SimpleContainer container, PotionIngredient ingredient, boolean exact){
        int hold = 0;
        for(int i = 0; i < container.getContainerSize(); ++i) {
            if (ingredient.is(container.getItem(i))) {
                hold += container.getItem(i).getCount();
            }
        }
        return hold == ingredient.getCount() || (!exact && hold >= ingredient.getCount());
    }

    @Override
    public String toString() {
        return item != null ? item.toString() : (tag != null ? tag.location() + " - " +  tagCount : "empty_ingredient");
    }

    public static boolean containsTag(ItemStack reference, ItemStack test){
        return NbtUtils.compareNbt(reference.getTag(), test.getTag(), true);
//        CompoundTag testTag = test.getTag();
//        if(testTag == null) return false;
//        ArrayList<String> keys = new ArrayList<>(reference.getTag().getAllKeys().stream().toList());
//        ArrayList<String> testKeys = new ArrayList<>(test.getTag().getAllKeys().stream().toList());
//        if(testKeys.size() < keys.size()) return false;
//        for (int i = 0; i < keys.size(); i++) {
//            String key = keys.get(i);
//            if(!reference.getTag().get(key).equals(testTag.get(key))){
//                return false;
//            }
//        }
//        return true;
    }
}
