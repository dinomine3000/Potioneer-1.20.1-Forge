package net.dinomine.potioneer.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.util.PotionIngredient;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class PotionCauldronRecipe implements Recipe<PotionCauldronContainer> {
    private final PotionContentData output;
    private final ResourceLocation id;
    private final PotionRecipeData recipeData;
    public PotionRecipeData getDefaultRecipeData(){return recipeData;}

    @Override
    public String toString() {
        return recipeData.toString();
    }

    public PotionCauldronRecipe(PotionContentData output, ResourceLocation id, PotionRecipeData recData) {
        this.output = output;
        this.id = id;
        this.recipeData = recData;
    }

    @Override
    public boolean matches(@NotNull PotionCauldronContainer simpleContainer, Level level) {
        if(level.isClientSide()) return false;
        if(this.recipeData.fire() && !simpleContainer.isOnFire()) return false;
        if(this.recipeData.waterLevel() != simpleContainer.getWaterLevel()) return false;

        //will return true if it finds a valid characteristic-like item
        for(int i = 0; i < simpleContainer.getContainerSize(); i++){
            if(simpleContainer.getItem(i).hasTag()
                    && simpleContainer.getItem(i).getTag().contains("beyonder_info")){
                CompoundTag beyonderTag = simpleContainer.getItem(i).getTag().getCompound("beyonder_info");
                if(beyonderTag.getInt("id") == this.recipeData.id()) return true;
            }
        }

        //will return true if every item in the recipe is contained in the container
        for(PotionIngredient recipeItem: recipeData.main()){
            if(!PotionIngredient.contains(simpleContainer, recipeItem, false)) {
                return false;
            }
        }
        return true;
    }
//
//    public boolean canCraft(PotionCauldronContainer container, Level level){
//        if(this.recipeData.fire() && !container.isOnFire()) return false;
//        if(this.recipeData.waterLevel() != container.getWaterLevel()) return false;
//
//        boolean charFlag = false;
//        for(int i = 0; i < container.getContainerSize(); i++){
//            if(container.getItem(i).hasTag()
//                    && container.getItem(i).getTag().contains("beyonder_info")){
//                CompoundTag beyonderTag = container.getItem(i).getTag().getCompound("beyonder_info");
//                if(beyonderTag.getInt("id") == this.recipeData.id()) charFlag = true;
//            }
//        }
//
//        for(ItemStack recipeItem: recipeData.main()){
//            if(charFlag) break;
//            if(!Tools.contains(container, recipeItem, false)) return false;
//        }
//
//        for(ItemStack recipeItem: recipeData.supplementary()){
//            if(!Tools.contains(container, recipeItem, false)) return false;
//        }
//        return true;
//    }

    @Override
    public ItemStack assemble(PotionCauldronContainer simpleContainer, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public PotionContentData getOutput(){
        return this.output;
    }

    public static class Type implements RecipeType<PotionCauldronRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "potion_cauldron_brew";
    }

    public static class Serializer implements RecipeSerializer<PotionCauldronRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(Potioneer.MOD_ID, "potion_cauldron_brew");

        @Override
        public PotionCauldronRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            NonNullList<PotionIngredient> mainIngredients = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "main_ingredients"));
            NonNullList<PotionIngredient> suppIngredients = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "supplementary_ingredients"));
            int waterLevel = GsonHelper.getAsInt(pJson, "water_level");
            boolean needsFire = GsonHelper.getAsBoolean(pJson, "needs_fire");


            if ((count(mainIngredients) + count(suppIngredients)) > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is 9");
            } else {
                PotionContentData output = getOutputFromJson(GsonHelper.getAsJsonObject(pJson, "output"));

                return new PotionCauldronRecipe(output, pRecipeId,
                                new PotionRecipeData(new ArrayList<>(mainIngredients), new ArrayList<>(suppIngredients),
                                waterLevel, needsFire, PotioneerMathHelper.isInteger(output.name) ? Integer.parseInt(output.name) : -1, true, output.name));
            }
        }

        public PotionContentData getOutputFromJson(JsonObject obj) {
            String name = GsonHelper.getAsString(obj, "name");
            int count = GsonHelper.getAsInt(obj, "amount");
            boolean bottle = GsonHelper.getAsBoolean(obj, "bottle");
            int color = GsonHelper.getAsInt(obj, "color");
            boolean canConflict = GsonHelper.getAsBoolean(obj, "can_conflict");
            return new PotionContentData(name, count, bottle, color, canConflict);
        }

        private int count(NonNullList<PotionIngredient> stacks){
            int hold = 0;
            for(PotionIngredient stc : stacks){
                hold += stc.getCount();
            }
            return hold;
        }

        private static NonNullList<PotionIngredient> itemsFromJson(JsonArray pItemArray) {
            NonNullList<PotionIngredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < pItemArray.size(); ++i) {
                PotionIngredient ingredient = PotionIngredient.fromJson(pItemArray.get(i));
                nonnulllist.add(ingredient);
            }

            return nonnulllist;
        }

        @Override
        public @Nullable PotionCauldronRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            PotionRecipeData data = PotionRecipeData.decode(friendlyByteBuf);
            PotionContentData output = PotionContentData.readFromByteBuf(friendlyByteBuf);

            return new PotionCauldronRecipe(output, resourceLocation, data);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, PotionCauldronRecipe potionCauldronRecipe) {
            potionCauldronRecipe.recipeData.encode(friendlyByteBuf);
            potionCauldronRecipe.output.writeIntoByteBuf(friendlyByteBuf);
    }
    }
}
