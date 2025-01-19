package net.dinomine.potioneer.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.dinomine.potioneer.Potioneer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PotionCauldronRecipe implements Recipe<PotionCauldronContainer> {
    private final NonNullList<ItemStack> mainIngredients;
    private final NonNullList<ItemStack> suppIngredients;
    private final PotionRecipeData output;
    private final ResourceLocation id;
    private final int waterLevel;
    private final boolean needsFire;

    public PotionCauldronRecipe(NonNullList<ItemStack> mainIngredients, NonNullList<ItemStack> suppIngredients,
                                PotionRecipeData output, ResourceLocation id, int waterLevel, boolean needsFire) {
        this.mainIngredients = mainIngredients;
        this.suppIngredients = suppIngredients;
        this.output = output;
        this.id = id;
        this.waterLevel = waterLevel;
        this.needsFire = needsFire;
    }

    @Override
    public boolean matches(PotionCauldronContainer simpleContainer, Level level) {
        if(level.isClientSide()) return false;
        if(this.needsFire && !simpleContainer.isOnFire()) return false;
        if(this.waterLevel != simpleContainer.getWaterLevel()) return false;

        //will return true if every item in the recipe is contained in the container
        for(ItemStack recipeItem: mainIngredients){
            if(!contains(simpleContainer, recipeItem)) {
                return false;
            }
        }
        return true;
    }

    public boolean canCraft(PotionCauldronContainer container){
        if(this.needsFire && !container.isOnFire()) return false;
        if(this.waterLevel != container.getWaterLevel()) return false;

        for(ItemStack recipeItem: mainIngredients){
            if(!contains(container, recipeItem)) return false;
        }

        for(ItemStack recipeItem: suppIngredients){
            if(!contains(container, recipeItem)) return false;
        }
        return true;
    }

    private boolean contains(PotionCauldronContainer container, ItemStack item){
        int hold = 0;
        if(item.hasTag()){
            CompoundTag tag = item.getTag();
            String name = tag.getString("name");
            for(int i = 0; i < container.getContainerSize(); ++i) {
                if (item.is(container.getItem(i).getItem())
                        && container.getItem(i).getTag().getCompound("potion_info").getString("name").equals(name)) {
                    hold++;
                }
            }
        } else {
            for(int i = 0; i < container.getContainerSize(); ++i) {
                if (item.is(container.getItem(i).getItem())) {
                    hold++;
                }
            }
        }
        return hold >= item.getCount();
    }

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

    public int getWaterLevel(){
        return this.waterLevel;
    }

    public boolean needsFire(){
        return this.needsFire;
    }

    public PotionRecipeData getOutput(){
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
            NonNullList<ItemStack> mainIngredients = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "main_ingredients"));
            NonNullList<ItemStack> suppIngredients = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "supplementary_ingredients"));
            int waterLevel = GsonHelper.getAsInt(pJson, "water_level");
            boolean needsFire = GsonHelper.getAsBoolean(pJson, "needs_fire");


            if ((count(mainIngredients) + count(suppIngredients)) > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is 9");
            } else {
                PotionRecipeData output = getOutputFromJson(GsonHelper.getAsJsonObject(pJson, "output"));
                return new PotionCauldronRecipe(mainIngredients, suppIngredients, output, pRecipeId, waterLevel, needsFire);
            }
        }

        public PotionRecipeData getOutputFromJson(JsonObject obj) {
            String name = GsonHelper.getAsString(obj, "name");
            int count = GsonHelper.getAsInt(obj, "amount");
            boolean bottle = GsonHelper.getAsBoolean(obj, "bottle");
            int color = GsonHelper.getAsInt(obj, "color");
            boolean canConflict = GsonHelper.getAsBoolean(obj, "can_conflict");
            return new PotionRecipeData(name, count, bottle, color, canConflict);
        }

        private long convertToHex(String string){
            char[] list = string.toCharArray();
            long res = 0;
            if(list[1] != 'x' || list.length != 10) throw new IllegalArgumentException("String is not a valid hexadecimal. Size or x is wrong");
            for(int i = 0; i < list.length; i ++){
                if(i == 0 && list[i] != '0') throw new IllegalArgumentException("String is not a valid hexadecimal. First character must be a 0");
                if(i > 1){
                    if(!isHexadecimalValid(list[i])) throw new IllegalArgumentException("String is not a hexadecimal. At least 1 character failed conversion");
                    res += (long) (Character.getNumericValue(list[i]) * Math.pow(16, (9-i)));
                }
            }
            return res;
        }

        private boolean isHexadecimalValid(char obj){
            int code = obj;
            return (code > 64 && code < 71) || (code > 96 && code < 103) || (code > 47 && code < 58);
        }

        private int count(NonNullList<ItemStack> stacks){
            int hold = 0;
            for(ItemStack stc : stacks){
                hold += stc.getCount();
            }
            return hold;
        }

        private static NonNullList<ItemStack> itemsFromJson(JsonArray pItemArray) {
            NonNullList<ItemStack> nonnulllist = NonNullList.create();

            for(int i = 0; i < pItemArray.size(); ++i) {
                ItemStack ingredient = ShapedRecipe.itemStackFromJson(pItemArray.get(i).getAsJsonObject());
                nonnulllist.add(ingredient);
            }

            return nonnulllist;
        }

        @Override
        public @Nullable PotionCauldronRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            NonNullList<ItemStack> mainIngredients = NonNullList.withSize(friendlyByteBuf.readInt(), ItemStack.EMPTY);
            for (int i = 0; i < mainIngredients.size(); i++) {
                mainIngredients.set(i, friendlyByteBuf.readItem());
            }

            NonNullList<ItemStack> suppIngredients = NonNullList.withSize(friendlyByteBuf.readInt(), ItemStack.EMPTY);
            suppIngredients.replaceAll(ignored -> friendlyByteBuf.readItem());

            int waterLevel = friendlyByteBuf.readInt();

            boolean needsFire = friendlyByteBuf.readBoolean();

            PotionRecipeData output = PotionRecipeData.readFromByteBuf(friendlyByteBuf);

            return new PotionCauldronRecipe(mainIngredients, suppIngredients, output, resourceLocation, waterLevel, needsFire);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, PotionCauldronRecipe potionCauldronRecipe) {
            friendlyByteBuf.writeInt(potionCauldronRecipe.mainIngredients.size());
            for (ItemStack i: potionCauldronRecipe.mainIngredients) {
                friendlyByteBuf.writeItemStack(i, false);
            }

            friendlyByteBuf.writeInt(potionCauldronRecipe.suppIngredients.size());
            for (ItemStack i: potionCauldronRecipe.suppIngredients) {
                friendlyByteBuf.writeItemStack(i, false);
            }

            friendlyByteBuf.writeInt(potionCauldronRecipe.getWaterLevel());

            friendlyByteBuf.writeBoolean(potionCauldronRecipe.needsFire());

            potionCauldronRecipe.output.writeIntoByteBuf(friendlyByteBuf);
        }
    }
}
