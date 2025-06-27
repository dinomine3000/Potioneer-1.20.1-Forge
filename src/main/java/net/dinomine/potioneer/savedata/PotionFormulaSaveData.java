package net.dinomine.potioneer.savedata;

import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.recipe.PotionCauldronRecipe;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class PotionFormulaSaveData extends SavedData {
    private ArrayList<PotionRecipeData> formulas = new ArrayList<>();
    public boolean refresh;
    public ArrayList<PotionRecipeData> getFormulas() {
        return formulas;
    }
    private ArrayList<ItemStack> totalIngredients = new ArrayList<>();

    public void setFormulas(ArrayList<PotionRecipeData> formulas) {
        this.formulas = formulas;
    }


    public void requestRefresh(boolean refresh) {
        this.refresh = refresh;
        setDirty();
    }

    public PotionFormulaSaveData(ServerLevel level){
        //this is the standard initialization of the formulas, aka, just reads the json files for the recipes
//        System.out.println("reading jsons");
        refresh = false;

        ArrayList<PotionCauldronRecipe> recipes = new ArrayList<>(level.getRecipeManager().getAllRecipesFor(PotionCauldronRecipe.Type.INSTANCE));

        boolean generateRandom = PotioneerCommonConfig.RANDOM_FORMULAS.get();

        if(generateRandom){
            totalIngredients = new ArrayList<>(
                    PotioneerCommonConfig.INGREDIENTS.get()
                    .stream().map(string -> {

                        //ItemStack stack = ItemParser.parseForItem(this.items, pReader);
                        CompoundTag temp = new CompoundTag();
                        temp.putString("id", string);
                        temp.putInt("Count", 1);
                        return ItemStack.of(temp);
                    }).toList()
            );

            int maxIterations = 20;
            int mainIngredientsNumber = 2;
            int attemptLimit = 5;
            int attempt = 0;
            int suppIngredientsNumber;
            outerlopp:
            for (int i = 0; i < recipes.size(); i++) {
                if(i == 0){
                    attempt++;
                    if(attempt > attemptLimit){
                        generateRandom = false;
                        break outerlopp;
                    }
                }
                System.out.println("Generating recipe " + i);
                ArrayList<ItemStack> newMain = null;
                ArrayList<ItemStack> newSupp = null;
                boolean found = false;
                int iteration = 0;

                suppIngredientsNumber = level.random.nextInt(3);

                while(!found){
//                    System.out.println("Iteration: " + (iteration + 1));
                    newMain = new ArrayList<>();
                    newSupp = new ArrayList<>();
                    if(iteration++ > maxIterations){
                        i = -1;
                        formulas = new ArrayList<>();
                        continue outerlopp;
                    }

                    for(int idx = 0; idx < mainIngredientsNumber; idx++){
                        int ingId = level.random.nextInt(totalIngredients.size());
                        if(contains(newMain, totalIngredients.get(ingId))){
//                            System.out.println("generated the same ingredient. adding on to self main.");
                            int copyIndex = indexOf(newMain, totalIngredients.get(ingId));
                            newMain.set(copyIndex,
                                    newMain.get(copyIndex).copyWithCount(newMain.get(copyIndex).getCount() + 1));
                        } else {
                            newMain.add(totalIngredients.get(ingId).copy());
                        }
                    }

                    for(int idx = 0; idx < suppIngredientsNumber; idx++){
                        int ingId = level.random.nextInt(totalIngredients.size());
                        if(contains(newSupp, totalIngredients.get(ingId))){
//                            System.out.println("generated the same ingredient. adding on to self supp.");
                            int copyIndex = indexOf(newSupp, totalIngredients.get(ingId));
                            newSupp.set(copyIndex,
                                    newSupp.get(copyIndex).copyWithCount(newSupp.get(copyIndex).getCount() + 1));
                        } else if(contains(newMain, totalIngredients.get(ingId))){
//                            System.out.println("generated the same ingredient. adding on to main.");
                            int copyIndex = indexOf(newMain, totalIngredients.get(ingId));
                            newMain.set(copyIndex,
                                    newMain.get(copyIndex).copyWithCount(newMain.get(copyIndex).getCount() + 1));
                        } else {
                            newSupp.add(totalIngredients.get(ingId).copy());
                        }
                    }

                    ArrayList<ItemStack> total = new ArrayList<>(newMain);
                    total.addAll(newSupp);

                    found = true;
                    for (PotionRecipeData form : formulas){

                        ArrayList<ItemStack> tempTotal = new ArrayList<>(form.main());
                        tempTotal.addAll(new ArrayList<>(form.supplementary()));
                        if (isContainedIn(form.main(), total) || isContainedIn(newMain, tempTotal)) {
//                            System.out.println("Found a conflict");
                            found = false;
                            break;
                        }
                    }
                }
//                System.out.println("Valid recipe");
                formulas.add(new PotionRecipeData(new ArrayList<>(newMain), new ArrayList<>(newSupp), level.random.nextInt(3) + 1,
                        level.random.nextBoolean(), recipes.get(i).recipeData.id()));
            }

        } if(!generateRandom) {


            recipes.forEach(rec -> {
                if(rec.recipeData.id() > -1){
                    formulas.add(rec.recipeData.copy());
                }
            });

        }


        updateEveryRecipe(this, level);
        setDirty();
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

    public ItemStack getRandomItemFromFormulaFor(int targetSequence){
        for (PotionRecipeData formula : formulas) {
            if (formula.id() == targetSequence) {
                ArrayList<ItemStack> ingredients = new ArrayList<>(formula.supplementary());
                ingredients.addAll(new ArrayList<>(formula.main()));
                return ingredients.get((new Random()).nextInt(ingredients.size()));
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isIngredientForSequence(ItemStack item, int sequenceId){
        for(PotionRecipeData formula: formulas){
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
        for(PotionRecipeData formula: formulas){
            if(contains(formula.main(), item) && formula.id()%10 <= bestSequence%10){
                flag = true;
                bestSequence = formula.id();
            }
        }
        return !flag ? -1 : bestSequence;
    }

    public boolean isFormulaCorrect(PotionRecipeData data){
        for(PotionRecipeData formula: formulas){
            if(formula.equals(data)) return true;
        }
        return false;
    }

    public String getClueForIngredient(ItemStack item){
        if(!contains(totalIngredients, item)) return "";
        return "Insert Ingredient Clue Logic Here";
    }

    private int indexOf(ArrayList<ItemStack> list, ItemStack item){
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).is(item.getItem())) return i;
        }
        return -1;
    }

    public static boolean contains(ArrayList<ItemStack> list, ItemStack item){
        for (ItemStack stack : list){
            if(stack.is(item.getItem())) return true;
        }
        return false;
    }

    /**
     * returns true if list contains the elements of main, each with at least that very count (per item)
     * @param main
     * @param list
     * @return
     */
    public static boolean isContainedIn(ArrayList<ItemStack> main, ArrayList<ItemStack> list){
        for(ItemStack stack : main){
            int match = 0;
            for(ItemStack ing : list){
                if(ing.is(stack.getItem())) match += ing.getCount();
            }
            if(match < stack.getCount() || match == 0) return false;
        }
        return true;
    }

    public PotionFormulaSaveData(Boolean readFiles){
        refresh = false;
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
//        System.out.println("saving to nbt");
        compoundTag.putBoolean("refresh", refresh);

        compoundTag.putInt("size", formulas.size());
        for(int i = 0; i < formulas.size(); i++){
            CompoundTag temp = new CompoundTag();
            formulas.get(i).save(temp);
            compoundTag.put("formula_" + i, temp);
        }

        return compoundTag;
    }

    public static PotionFormulaSaveData load(CompoundTag nbt, Level level){
//        System.out.println("loading nbt...");
        //refresh variable
        boolean refresh = nbt.getBoolean("refresh");
        if(refresh){
            return null;
        }
        //formulas data
        int size = nbt.getInt("size");
        ArrayList<PotionRecipeData> found = new ArrayList<>();
        if(size != 0){
            for(int i = 0; i < size; i++){
                found.add(PotionRecipeData.load(nbt.getCompound("formula_" + i)));
            }
        } else {
//            System.out.println("initiating WSD with standard values");
            return null;
        }

        PotionFormulaSaveData res = new PotionFormulaSaveData(false);
        res.setFormulas(found);
        res.refresh = false;
        updateEveryRecipe(res, level);
        return res;
    }

    private static void updateEveryRecipe(PotionFormulaSaveData data, Level level){

        ArrayList<PotionCauldronRecipe> recipes = new ArrayList<>(level.getRecipeManager().getAllRecipesFor(PotionCauldronRecipe.Type.INSTANCE));
        recipes.forEach(rec -> {
            if(rec.recipeData.id() > -1){
                PotionRecipeData result = data.getDataFromId(rec.recipeData.id());
                if(result != null){
                    rec.alternateRecipeData = result.copy();
                } else {
                    level.players().forEach(player -> {
                        player.sendSystemMessage(Component.literal("Recipe wasn't found in old data. Will refresh on next NBT data save"));
                    });
                    data.refresh = true;
                    System.out.println("ERROR: Could not update potion recipe. Recipe was not fData read was null.");
                }
            }
        });
    }

    public static PotionFormulaSaveData from(ServerLevel level){
        return level.getServer().overworld().getDataStorage().computeIfAbsent((tag) -> load(tag, level),
                () -> new PotionFormulaSaveData(level), "potioneer_formulas");
    }

    public PotionRecipeData getDataFromId(int id){
        for (PotionRecipeData data : formulas){
            if(data.id() == id) return data;
        }
        return null;
    }
}
