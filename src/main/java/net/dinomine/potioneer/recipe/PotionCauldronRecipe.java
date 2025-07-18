package net.dinomine.potioneer.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.savedata.PotionRecipeData;
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
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class PotionCauldronRecipe implements Recipe<PotionCauldronContainer> {
    private final PotionContentData output;
    private final ResourceLocation id;
    private final PotionRecipeData recipeData;
    public PotionRecipeData getDefaultRecipeData(){return recipeData;}
    public PotionRecipeData alternateRecipeData;

    @Override
    public String toString() {
        return recipeData.toString();
    }

    public PotionCauldronRecipe(PotionContentData output, ResourceLocation id,
                                PotionRecipeData recData) {
        this(output, id, recData, recData.copy());
    }

    public PotionCauldronRecipe(PotionContentData output, ResourceLocation id,
                                PotionRecipeData recData, PotionRecipeData alt){
        this.output = output;
        this.id = id;
        this.recipeData = recData;
        this.alternateRecipeData = alt;
    }

    @Override
    public boolean matches(@NotNull PotionCauldronContainer simpleContainer, Level level) {
        if(level.isClientSide()) return false;
        if(this.alternateRecipeData.fire() && !simpleContainer.isOnFire()) return false;
        if(this.alternateRecipeData.waterLevel() != simpleContainer.getWaterLevel()) return false;

        //will return true if it finds a valid characteristic-like item
        for(int i = 0; i < simpleContainer.getContainerSize(); i++){
            if(simpleContainer.getItem(i).hasTag()
                    && simpleContainer.getItem(i).getTag().contains("beyonder_info")){
                CompoundTag beyonderTag = simpleContainer.getItem(i).getTag().getCompound("beyonder_info");
                if(beyonderTag.getInt("id") == this.alternateRecipeData.id()) return true;
            }
        }

        //will return true if every item in the recipe is contained in the container
        for(ItemStack recipeItem: alternateRecipeData.main()){
            if(!contains(simpleContainer, recipeItem)) {
                return false;
            }
        }
        return true;
    }

    public boolean canCraft(PotionCauldronContainer container, Level level){
        if(this.alternateRecipeData.fire() && !container.isOnFire()) return false;
        if(this.alternateRecipeData.waterLevel() != container.getWaterLevel()) return false;

        boolean charFlag = false;
        for(int i = 0; i < container.getContainerSize(); i++){
            if(container.getItem(i).hasTag()
                    && container.getItem(i).getTag().contains("beyonder_info")){
                CompoundTag beyonderTag = container.getItem(i).getTag().getCompound("beyonder_info");
                if(beyonderTag.getInt("id") == this.alternateRecipeData.id()) charFlag = true;
            }
        }

        for(ItemStack recipeItem: alternateRecipeData.main()){
            if(charFlag) break;
            if(!contains(container, recipeItem)) return false;
        }

        for(ItemStack recipeItem: alternateRecipeData.supplementary()){
            if(!contains(container, recipeItem)) return false;
        }
        return true;
    }

//    private PotionRecipeData checkWorldSavedData(Level level){
//        if(recipeData.id() > -1) {
//            PotionFormulaSaveData data = PotionFormulaSaveData.from((ServerLevel) level);
//            //TODO remove this if check. supposedly, any recipe would already be in saved data, so it would never be null
//            if(data.getDataFromId(recipeData.id()) != null){
//                return data.getDataFromId(recipeData.id()).copy();
//            }
//        }
//        return recipeData;
//    }

    private boolean contains(PotionCauldronContainer container, ItemStack item){
        int hold = 0;
        if(item.hasTag()){
            CompoundTag tag = item.getTag();
            for(int i = 0; i < container.getContainerSize(); ++i) {
                if (item.is(container.getItem(i).getItem())
                        && container.getItem(i).hasTag()
                        && containsTag(item, container.getItem(i))) {
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

    private boolean containsTag(ItemStack reference, ItemStack test){
        CompoundTag testTag = test.getTag();
        if(testTag == null) return false;
        ArrayList<String> keys = new ArrayList<>(reference.getTag().getAllKeys().stream().toList());
        ArrayList<String> testKeys = new ArrayList<>(test.getTag().getAllKeys().stream().toList());
        if(testKeys.size() < keys.size()) return false;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if(!reference.getTag().get(key).equals(testTag.get(key))){
                return false;
            }
        }
        return true;
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
        return this.recipeData.waterLevel();
    }

    public boolean needsFire(){
        return this.recipeData.fire();
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
            NonNullList<ItemStack> mainIngredients = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "main_ingredients"));
            NonNullList<ItemStack> suppIngredients = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "supplementary_ingredients"));
            int waterLevel = GsonHelper.getAsInt(pJson, "water_level");
            boolean needsFire = GsonHelper.getAsBoolean(pJson, "needs_fire");


            if ((count(mainIngredients) + count(suppIngredients)) > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is 9");
            } else {
                PotionContentData output = getOutputFromJson(GsonHelper.getAsJsonObject(pJson, "output"));

                return new PotionCauldronRecipe(output, pRecipeId,
                                new PotionRecipeData(new ArrayList<>(mainIngredients), new ArrayList<>(suppIngredients),
                                waterLevel, needsFire, isNumeric(output.name) ? Integer.parseInt(output.name) : -1));
            }
        }

        private static boolean isNumeric(String str) {
            try {
                Double.parseDouble(str);
                return true;
            } catch(NumberFormatException e){
                return false;
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
            mainIngredients.replaceAll(ignored -> friendlyByteBuf.readItem());

            NonNullList<ItemStack> suppIngredients = NonNullList.withSize(friendlyByteBuf.readInt(), ItemStack.EMPTY);
            suppIngredients.replaceAll(ignored -> friendlyByteBuf.readItem());

            int waterLevel = friendlyByteBuf.readInt();

            boolean needsFire = friendlyByteBuf.readBoolean();

            PotionContentData output = PotionContentData.readFromByteBuf(friendlyByteBuf);

            NonNullList<ItemStack> altMainIngredients = NonNullList.withSize(friendlyByteBuf.readInt(), ItemStack.EMPTY);
            altMainIngredients.replaceAll(ignored -> friendlyByteBuf.readItem());

            NonNullList<ItemStack> altSuppIngredients = NonNullList.withSize(friendlyByteBuf.readInt(), ItemStack.EMPTY);
            altSuppIngredients.replaceAll(ignored -> friendlyByteBuf.readItem());

            int altWaterLevel = friendlyByteBuf.readInt();

            boolean altNeedsFire = friendlyByteBuf.readBoolean();

            int id = isNumeric(output.name) ? Integer.parseInt(output.name) : -1;

            return new PotionCauldronRecipe(output, resourceLocation,
                    new PotionRecipeData(new ArrayList<>(mainIngredients), new ArrayList<>(suppIngredients), waterLevel, needsFire, id),
                    new PotionRecipeData(new ArrayList<>(altMainIngredients), new ArrayList<>(altSuppIngredients), altWaterLevel, altNeedsFire, id));
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, PotionCauldronRecipe potionCauldronRecipe) {
            friendlyByteBuf.writeInt(potionCauldronRecipe.recipeData.main().size());
            for (ItemStack i: potionCauldronRecipe.recipeData.main()) {
                friendlyByteBuf.writeItemStack(i, false);
            }

            friendlyByteBuf.writeInt(potionCauldronRecipe.recipeData.supplementary().size());
            for (ItemStack i: potionCauldronRecipe.recipeData.supplementary()) {
                friendlyByteBuf.writeItemStack(i, false);
            }
            friendlyByteBuf.writeInt(potionCauldronRecipe.getWaterLevel());

            friendlyByteBuf.writeBoolean(potionCauldronRecipe.needsFire());

            potionCauldronRecipe.output.writeIntoByteBuf(friendlyByteBuf);

            friendlyByteBuf.writeInt(potionCauldronRecipe.alternateRecipeData.main().size());
            for (ItemStack i: potionCauldronRecipe.alternateRecipeData.main()) {
                friendlyByteBuf.writeItemStack(i, false);
            }

            friendlyByteBuf.writeInt(potionCauldronRecipe.alternateRecipeData.supplementary().size());
            for (ItemStack i: potionCauldronRecipe.alternateRecipeData.supplementary()) {
                friendlyByteBuf.writeItemStack(i, false);
            }

            friendlyByteBuf.writeInt(potionCauldronRecipe.alternateRecipeData.waterLevel());

            friendlyByteBuf.writeBoolean(potionCauldronRecipe.alternateRecipeData.fire());

    }
    }
}
