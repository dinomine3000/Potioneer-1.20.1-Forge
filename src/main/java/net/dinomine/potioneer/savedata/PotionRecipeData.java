package net.dinomine.potioneer.savedata;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dinomine.potioneer.Potioneer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public record PotionRecipeData(ArrayList<ItemStack> main, ArrayList<ItemStack> supplementary, int waterLevel, boolean fire, int id) {

    public void save(CompoundTag nbt){
        nbt.putInt("id", id);
        nbt.putInt("water", waterLevel());
        nbt.putBoolean("fire", fire());
        nbt.putInt("size_main", main.size());
        for(int i = 0; i < main.size(); i++){
            CompoundTag temp = new CompoundTag();
            main.get(i).save(temp);
            nbt.put("main_" + i, temp);
        }

        nbt.putInt("size_supp", supplementary.size());
        for(int i = 0; i < supplementary.size(); i++){
            CompoundTag temp = new CompoundTag();
            supplementary.get(i).save(temp);
            nbt.put("supp_" + i, temp);
        }
    }

    public static PotionRecipeData load(CompoundTag nbt){
        int id = nbt.getInt("id");
        int water = nbt.getInt("water");
        boolean fire = nbt.getBoolean("fire");

        int size = nbt.getInt("size_main");
        ArrayList<ItemStack> main = new ArrayList<>();
        if(size != 0){
            for(int i = 0; i < size; i++){
                CompoundTag temp = nbt.getCompound("main_" + i);
                main.add(ItemStack.of(temp));
            }
        }

        int sizeSupp = nbt.getInt("size_supp");
        ArrayList<ItemStack> supp = new ArrayList<>();
        if(sizeSupp != 0){
            for(int i = 0; i < sizeSupp; i++){
                CompoundTag temp = nbt.getCompound("supp_" + i);
                supp.add(ItemStack.of(temp));
            }
        }
        return new PotionRecipeData(main, supp, water, fire, id);
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(waterLevel);
        buffer.writeInt(id);
        buffer.writeBoolean(fire);
        buffer.writeInt(main.size());
        for(int i = 0; i < main.size(); i++){
            buffer.writeItemStack(main.get(i), false);
        }
        buffer.writeInt(supplementary.size());
        for(int i = 0; i < supplementary.size(); i++){
            buffer.writeItemStack(supplementary.get(i), false);
        }
    }

    public static PotionRecipeData decode(FriendlyByteBuf buffer){
        int water = buffer.readInt();
        int id = buffer.readInt();
        boolean fire = buffer.readBoolean();

        ArrayList<ItemStack> mainIngredients = new ArrayList<>();
        int sizeMain = buffer.readInt();
        for(int i = 0; i < sizeMain; i++){
            mainIngredients.add(buffer.readItem());
        }

        ArrayList<ItemStack> suppIngredients = new ArrayList<>();
        int sizeSupp = buffer.readInt();
        for(int i = 0; i < sizeSupp; i++){
            suppIngredients.add(buffer.readItem());
        }
        return new PotionRecipeData(mainIngredients, suppIngredients, water, fire, id);
    }

    public static PotionRecipeData convertFromJson(JsonObject obj){
        NonNullList<ItemStack> mainIngredients = itemsFromJson(GsonHelper.getAsJsonArray(obj, "main_ingredients"));
        NonNullList<ItemStack> suppIngredients = itemsFromJson(GsonHelper.getAsJsonArray(obj, "supplementary_ingredients"));
        ArrayList<ItemStack> main = new ArrayList<>(mainIngredients);
        ArrayList<ItemStack> supp = new ArrayList<>(suppIngredients);
        int waterLevel = GsonHelper.getAsInt(obj, "water_level");
        boolean needsFire = GsonHelper.getAsBoolean(obj, "needs_fire");
        JsonObject output = GsonHelper.getAsJsonObject(obj, "output");
        int id = Integer.parseInt(GsonHelper.getAsString(output, "name"));
        return new PotionRecipeData(main, supp, waterLevel, needsFire, id);
    }

    private static NonNullList<ItemStack> itemsFromJson(JsonArray pItemArray) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();

        for(int i = 0; i < pItemArray.size(); ++i) {
            ItemStack ingredient = ShapedRecipe.itemStackFromJson(pItemArray.get(i).getAsJsonObject());
            nonnulllist.add(ingredient);
        }

        return nonnulllist;
    }

    public PotionRecipeData copy(){
        return new PotionRecipeData(new ArrayList<>(main), new ArrayList<>(supplementary), waterLevel, fire, id);
    }

    @Override
    public String toString() {
        return "PotionRecipeData{" +
                "main=" + main +
                ", supplementary=" + supplementary +
                ", waterLevel=" + waterLevel +
                ", fire=" + fire +
                ", id=" + id +
                '}';
    }
}
