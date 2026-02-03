package net.dinomine.potioneer.savedata;

import net.dinomine.potioneer.recipe.PotionCauldronContainer;
import net.dinomine.potioneer.recipe.PotionCauldronRecipe;
import net.dinomine.potioneer.recipe.PotionRecipe;
import net.dinomine.potioneer.recipe.PotionRecipeData;
import net.dinomine.potioneer.util.PotionIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PotionFormulaSaveData extends SavedData {
    private ArrayList<PotionRecipe> recipes = new ArrayList<>();

    public List<PotionRecipeData> getFormulas(){
        return recipes.stream().map(PotionRecipe::input).toList();
    }

    public void setRecipes(ArrayList<PotionRecipe> recipes) {
        this.recipes = recipes;
    }


    public void refreshFormulas(ServerLevel level) {
        //gets default recipes
        this.recipes = new ArrayList<>();
        ArrayList<PotionCauldronRecipe> recipes = new ArrayList<>(level.getRecipeManager().getAllRecipesFor(PotionCauldronRecipe.Type.INSTANCE));
        recipes.forEach(rec -> {
            this.recipes.add(new PotionRecipe(rec));
        });
        setDirty();
    }

    public PotionFormulaSaveData(ServerLevel level){

        refreshFormulas(level);
    }

    public ItemStack getRandomItemFromFormulaFor(int targetSequence, RandomSource random){
        for (PotionRecipeData formula : getFormulas()) {
            if (formula.id() == targetSequence) {
                ArrayList<PotionIngredient> ingredients = new ArrayList<>(formula.supplementary());
                ingredients.addAll(new ArrayList<>(formula.main()));
                List<PotionIngredient> testList = ingredients.stream().filter(PotionIngredient::isItemIngredient).toList();
                return testList.isEmpty() ? ItemStack.EMPTY : testList.get(random.nextInt(testList.size())).getStack();
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isIngredientForSequence(ItemStack item, int sequenceId){
        for(PotionRecipeData formula: getFormulas()){
            if(formula.id() == sequenceId
                    && (contains(formula.main(), item) || contains(formula.supplementary(), item))){
                return true;
            }
        }
        return false;
    }

    public int getHighestSequenceForItem(ItemStack item){
        boolean flag = false;
        int bestSequence = 9;
        for(PotionRecipeData formula: getFormulas()){
            if(contains(formula.main(), item) && formula.id()%10 <= bestSequence%10){
                flag = true;
                bestSequence = formula.id();
            }
        }
        return !flag ? -1 : bestSequence;
    }

    public boolean isFormulaCorrect(PotionRecipeData data){
        for(PotionRecipeData formula: getFormulas()){
            if(formula.equals(data)) return true;
        }
        return false;
    }

    public String getClueForIngredient(ItemStack item){
        return "Insert Ingredient Clue Logic Here";
    }

//    private int indexOf(ArrayList<ItemStack> list, ItemStack item){
//        for (int i = 0; i < list.size(); i++) {
//            if(list.get(i).is(item.getItem())) return i;
//        }
//        return -1;
//    }

    public static boolean contains(ArrayList<PotionIngredient> list, ItemStack item){
        for (PotionIngredient ingredient : list){
            if(ingredient.is(item)) return true;
        }
        return false;
    }

    /**
     * returns true if list contains the elements of main, each with at least that very count (per item)
     * @param main
     * @param list
     * @return
     */
    public static boolean isContainedIn(ArrayList<PotionIngredient> main, ArrayList<PotionIngredient> list){
        for(PotionIngredient stack : main){
            int match = 0;
            for(PotionIngredient ing : list){
                if(ing.is(stack.getStack())) match += ing.getCount();
            }
            if(match < stack.getCount() || match == 0) return false;
        }
        return true;
    }

    public PotionFormulaSaveData(Boolean readFiles){
    }
//
//    private static JsonObject readJsonObject(ResourceLocation path) throws IOException {
//        ResourceLocation newPath = path.withPrefix("../../data/" + Potioneer.MOD_ID + "/recipes/");
//        InputStream in = Minecraft.getInstance().getResourceManager().open(newPath);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        Gson gson = new Gson();
//        JsonElement je = gson.fromJson(reader, JsonElement.class);
//        JsonObject json = je.getAsJsonObject();
//        in.close();
//        return json;
//    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        System.out.println("[PotionFormulaSaveData] saving to nbt");
        compoundTag.putInt("size", recipes.size());
        for(int i = 0; i < recipes.size(); i++){
            CompoundTag temp = new CompoundTag();
            recipes.get(i).save(temp);
            compoundTag.put("formula_" + i, temp);
        }

        return compoundTag;
    }

    public static PotionFormulaSaveData load(CompoundTag nbt, Level level){
//        System.out.println("loading nbt...");
        //refresh variable
        //formulas data
        int size = nbt.getInt("size");
        ArrayList<PotionRecipe> found = new ArrayList<>();
        if(size != 0){
            for(int i = 0; i < size; i++){
                found.add(PotionRecipe.load(nbt.getCompound("formula_" + i)));
            }
        } else {
//            System.out.println("initiating WSD with standard values");
            return null;
        }

        PotionFormulaSaveData res = new PotionFormulaSaveData(false);
        res.setRecipes(found);
//        updateEveryRecipe(res, level);
        return res;
    }

//    private static void updateEveryRecipe(PotionFormulaSaveData data, Level level){
//
//        ArrayList<PotionCauldronRecipe> recipes = new ArrayList<>(level.getRecipeManager().getAllRecipesFor(PotionCauldronRecipe.Type.INSTANCE));
//        recipes.forEach(rec -> {
//            if(rec.getDefaultRecipeData().id() > -1){
//                PotionRecipeData result = data.getDataFromId(rec.getDefaultRecipeData().id());
//                if(result != null){
//                    rec.alternateRecipeData = result.copy();
//                } else {
//                    level.players().forEach(player -> {
//                        player.sendSystemMessage(Component.literal("Recipe wasn't found in old data. Will refresh on next NBT data save"));
//                    });
//                    System.out.println("ERROR: Could not update potion recipe. Recipe was not found. Data read was null.");
//                }
//            }
//        });
//    }

    public static PotionFormulaSaveData from(ServerLevel level){
        return level.getServer().overworld().getDataStorage().computeIfAbsent((tag) -> load(tag, level),
                () -> new PotionFormulaSaveData(level), "potioneer_formulas");
    }

    public PotionRecipeData getFormulaDataFromId(int pathwaySequenceId, ServerLevel level){
        if(getFormulas().isEmpty()) refreshFormulas(level);
        if(getFormulas().isEmpty()) return new PotionRecipeData(new ArrayList<>(), new ArrayList<>(), 0, false, -1, false, "invalid");
        for (PotionRecipeData data : getFormulas()){
            if(data.id() == pathwaySequenceId) return data;
        }
        return null;
    }

    public List<PotionRecipe> getRecipesFor(PotionCauldronContainer container) {
        return recipes.stream().filter(recipe -> recipe.matches(container)).toList();
    }

    public Collection<? extends Item> getAllItems() {
        List<Item> resItems = new ArrayList<>();
        for(PotionRecipe formula: recipes){
            PotionRecipeData recipeData = formula.input();
            resItems.addAll(recipeData.main().stream().map(ingredient -> ingredient.getRepresentativeStack().getItem()).toList());
            resItems.addAll(recipeData.supplementary().stream().map(ingredient -> ingredient.getRepresentativeStack().getItem()).toList());
        }
        return resItems;
    }
}
