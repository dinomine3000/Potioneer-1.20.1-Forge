package net.dinomine.potioneer.savedata;

import net.dinomine.potioneer.recipe.PotionCauldronContainer;
import net.dinomine.potioneer.recipe.PotionCauldronRecipe;
import net.dinomine.potioneer.recipe.PotionRecipe;
import net.dinomine.potioneer.recipe.PotionRecipeData;
import net.dinomine.potioneer.util.PotionIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
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

//        //add new recipes in json
//        this.recipes.addAll(JSONParserHelper.loadNewFormulas());
//
//        //modify existing recipes based on json
//        for (PotionRecipeData changedData : JSONParserHelper.loadChangedFormulas()) {
//            int id = changedData.id();
//            if(id < 0) continue;
//            for (int i = 0; i < this.recipes.size(); i++) {
//                PotionRecipe recipe = this.recipes.get(i);
//                if (recipe.input().id() == id) {
//                    this.recipes.set(i, new PotionRecipe(changedData, recipe.output()));
//                    break; // Assuming IDs are unique, break to avoid unnecessary checks
//                }
//            }
//        }
        setDirty();
    }

    public PotionFormulaSaveData(ServerLevel level){
        //this is the standard initialization of the formulas, aka, just reads the json files for the recipes
//        System.out.println("reading jsons");
//        boolean generateRandom = PotioneerCommonConfig.RANDOM_FORMULAS.get();
//        if(generateRandom){
//            totalIngredients = new ArrayList<>(
//                    PotioneerCommonConfig.INGREDIENTS.get()
//                    .stream().map(string -> {
//
//                        //ItemStack stack = ItemParser.parseForItem(this.items, pReader);
//                        CompoundTag temp = new CompoundTag();
//                        temp.putString("id", string);
//                        temp.putInt("Count", 1);
//                        return ItemStack.of(temp);
//                    }).toList()
//            );
//
//            int maxIterations = 20;
//            int mainIngredientsNumber = 2;
//            int attemptLimit = 5;
//            int attempt = 0;
//            int suppIngredientsNumber;
//            outerlopp:
//            for (int i = 0; i < recipes.size(); i++) {
//                if(i == 0){
//                    attempt++;
//                    if(attempt > attemptLimit){
//                        generateRandom = false;
//                        break outerlopp;
//                    }
//                }
//                System.out.println("Generating recipe " + i);
//                ArrayList<ItemStack> newMain = null;
//                ArrayList<ItemStack> newSupp = null;
//                boolean found = false;
//                int iteration = 0;
//
//                suppIngredientsNumber = level.random.nextInt(3);
//
//                while(!found){
////                    System.out.println("Iteration: " + (iteration + 1));
//                    newMain = new ArrayList<>();
//                    newSupp = new ArrayList<>();
//                    if(iteration++ > maxIterations){
//                        i = -1;
//                        formulas = new ArrayList<>();
//                        continue outerlopp;
//                    }
//
//                    for(int idx = 0; idx < mainIngredientsNumber; idx++){
//                        int ingId = level.random.nextInt(totalIngredients.size());
//                        if(contains(newMain, totalIngredients.get(ingId))){
////                            System.out.println("generated the same ingredient. adding on to self main.");
//                            int copyIndex = indexOf(newMain, totalIngredients.get(ingId));
//                            newMain.set(copyIndex,
//                                    newMain.get(copyIndex).copyWithCount(newMain.get(copyIndex).getCount() + 1));
//                        } else {
//                            newMain.add(totalIngredients.get(ingId).copy());
//                        }
//                    }
//
//                    for(int idx = 0; idx < suppIngredientsNumber; idx++){
//                        int ingId = level.random.nextInt(totalIngredients.size());
//                        if(contains(newSupp, totalIngredients.get(ingId))){
////                            System.out.println("generated the same ingredient. adding on to self supp.");
//                            int copyIndex = indexOf(newSupp, totalIngredients.get(ingId));
//                            newSupp.set(copyIndex,
//                                    newSupp.get(copyIndex).copyWithCount(newSupp.get(copyIndex).getCount() + 1));
//                        } else if(contains(newMain, totalIngredients.get(ingId))){
////                            System.out.println("generated the same ingredient. adding on to main.");
//                            int copyIndex = indexOf(newMain, totalIngredients.get(ingId));
//                            newMain.set(copyIndex,
//                                    newMain.get(copyIndex).copyWithCount(newMain.get(copyIndex).getCount() + 1));
//                        } else {
//                            newSupp.add(totalIngredients.get(ingId).copy());
//                        }
//                    }
//
//                    ArrayList<ItemStack> total = new ArrayList<>(newMain);
//                    total.addAll(newSupp);
//
//                    found = true;
//                    for (PotionRecipeData form : formulas){
//
//                        ArrayList<ItemStack> tempTotal = new ArrayList<>(form.main());
//                        tempTotal.addAll(new ArrayList<>(form.supplementary()));
//                        if (isContainedIn(form.main(), total) || isContainedIn(newMain, tempTotal)) {
////                            System.out.println("Found a conflict");
//                            found = false;
//                            break;
//                        }
//                    }
//                }
////                System.out.println("Valid recipe");
//                formulas.add(new PotionRecipeData(new ArrayList<>(newMain), new ArrayList<>(newSupp), level.random.nextInt(3) + 1,
//                        level.random.nextBoolean(), recipes.get(i).recipeData.id()));
//            }
//
//        }

        refreshFormulas(level);
//        ResourceLocation minerP = new ResourceLocation(Potioneer.MOD_ID, "9_potion.json");
//        ResourceLocation swimP = new ResourceLocation(Potioneer.MOD_ID, "19_potion.json");
//        ResourceLocation trickP = new ResourceLocation(Potioneer.MOD_ID, "29_potion.json");
//        ResourceLocation warriorP= new ResourceLocation(Potioneer.MOD_ID, "39_potion.json");
//        ResourceLocation craftP = new ResourceLocation(Potioneer.MOD_ID, "49_potion.json");
//
//        try{
//            ArrayList<JsonObject> readFormulas = new ArrayList<>();
//            readFormulas.add(readJsonObject(minerP));
//            readFormulas.add(readJsonObject(trickP));
//            readFormulas.add(readJsonObject(swimP));
//            readFormulas.add(readJsonObject(warriorP));
//            readFormulas.add(readJsonObject(craftP));
//            formulas = new ArrayList<>(readFormulas.stream().map(PotionRecipeData::convertFromJson).toList());
//        } catch (IOException e) {
//            System.out.println("File name doesnt exist");
//        }
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
}
