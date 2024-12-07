package net.dinomine.potioneer.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.dinomine.potioneer.Potioneer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PotionCauldronRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ResourceLocation id;
    private final int waterLevel;
    private final boolean needsFire;

    public PotionCauldronRecipe(NonNullList<Ingredient> inputItems, ItemStack output, ResourceLocation id, int waterLevel, boolean needsFire) {
        this.inputItems = inputItems;
        this.output = output;
        this.id = id;
        this.waterLevel = waterLevel;
        this.needsFire = needsFire;
    }

    @Override
    public boolean matches(SimpleContainer simpleContainer, Level level) {
        if(level.isClientSide()) return false;

        //will return true if every item in the recipe is contained in the container
        for(Ingredient recipeItem: inputItems){
            if(!contains(simpleContainer, recipeItem)) return false;
        }
        return true;
    }

    private boolean contains(SimpleContainer container, Ingredient item){
        for(int i = 0; i < container.getContainerSize(); ++i) {
            if (item.test(container.getItem(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer simpleContainer, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
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

    public int getWaterLevel(){
        return this.waterLevel;
    }

    public boolean needsFire(){
        return this.needsFire;
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
            NonNullList<Ingredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "ingredients"));
            int waterLevel = GsonHelper.getAsInt(pJson, "water_level");
            boolean needsFire = GsonHelper.getAsBoolean(pJson, "needs_fire");

            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for potion cauldron recipe");
            } else if (ingredients.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is 9");
            } else {
                ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "output"));
                return new PotionCauldronRecipe(ingredients, output, pRecipeId, waterLevel, needsFire);
            }
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < pIngredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i), false);
                nonnulllist.add(ingredient);
            }

            return nonnulllist;
        }

        @Override
        public @Nullable PotionCauldronRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {

            NonNullList<Ingredient> inputs = NonNullList.withSize(friendlyByteBuf.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(friendlyByteBuf));
            }

            int waterLevel = friendlyByteBuf.readInt();

            boolean needsFire = friendlyByteBuf.readBoolean();

            ItemStack output = friendlyByteBuf.readItem();

            return new PotionCauldronRecipe(inputs, output, resourceLocation, waterLevel, needsFire);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, PotionCauldronRecipe potionCauldronRecipe) {
            friendlyByteBuf.writeInt(potionCauldronRecipe.inputItems.size());
            for (Ingredient i: potionCauldronRecipe.inputItems) {
                i.toNetwork(friendlyByteBuf);
            }

            friendlyByteBuf.writeInt(potionCauldronRecipe.getWaterLevel());

            friendlyByteBuf.writeBoolean(potionCauldronRecipe.needsFire());

            friendlyByteBuf.writeItemStack(potionCauldronRecipe.getResultItem(null), false);
        }
    }
}
