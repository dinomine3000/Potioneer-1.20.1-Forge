package net.dinomine.potioneer.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dinomine.potioneer.recipe.PotionContentData;
import net.dinomine.potioneer.savedata.PotionRecipe;
import net.dinomine.potioneer.savedata.PotionRecipeData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class JSONParserHelper {
    private static final Path newRecipesConfigPath = FMLPaths.CONFIGDIR.get().resolve("potioneer/new_formulas.json");
    private static final File NEW_RECIPES = newRecipesConfigPath.toFile();
    private static final Path changedRecipesConfigPath = FMLPaths.CONFIGDIR.get().resolve("potioneer/changed_formulas.json");
    private static final File CHANGED_RECIPES = changedRecipesConfigPath.toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type ITEM_LIST_TYPE = new TypeToken<List<ItemWithNBT>>() {}.getType();

    public static List<PotionRecipeData> loadChangedFormulas() {
        try {
            if (!CHANGED_RECIPES.exists()) {
                createDefaultChangedRecipes();
                System.out.println("[Potioneer] JSON config not found: " + CHANGED_RECIPES.getAbsolutePath());
                return Collections.emptyList();
            }
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(PotionRecipeData.class, new ChangedRecipesDeserializer())
                    .setPrettyPrinting()
                    .create();
            String jsonString = Files.readString(Path.of(CHANGED_RECIPES.toString()));
            Type listType = new TypeToken<List<PotionRecipeData>>() {}.getType();
            List<PotionRecipeData> recipes = gson.fromJson(jsonString, listType);

            System.out.println("[Potioneer] Loaded " + recipes.size() + " changed formulas:");
            recipes.forEach(f -> System.out.println("  -> " + f.id()));
            return recipes;
        } catch (Exception e) {
            System.err.println("[Potioneer] Failed to load formulas: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<PotionRecipe> loadNewFormulas() {
        try {
            if (!NEW_RECIPES.exists()) {
                createDefaultNewRecipes();
                System.out.println("[Potioneer] JSON config not found: " + NEW_RECIPES.getAbsolutePath());
                return Collections.emptyList();
            }
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(PotionRecipe.class, new NewRecipesDeserializer())
                    .setPrettyPrinting()
                    .create();
            String jsonString = Files.readString(NEW_RECIPES.toPath());
            Type listType = new TypeToken<List<PotionRecipe>>() {}.getType();
            List<PotionRecipe> recipes = gson.fromJson(jsonString, listType);

            System.out.println("[Potioneer] Loaded " + recipes.size() + " new formulas:");
            recipes.forEach(f -> System.out.println("  -> " + f.output().name));
            return recipes;
        } catch (Exception e) {
            System.err.println("[Potioneer] Failed to load formulas: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static void createDefaultNewRecipes() {
        try {
            // Ensure directory exists
            File parentDir = NEW_RECIPES.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Build a default ItemWithNBT
            ItemWithNBT defaultItem = new ItemWithNBT();
            defaultItem.id = "minecraft:blaze_powder";
            defaultItem.Count = 1;
            defaultItem.nbt = new JsonObject(); // No NBT by default

            // Output object
            JsonObject output = new JsonObject();
            output.addProperty("name", "fire_boost");
            output.addProperty("amount", 1);
            output.addProperty("color", 0xFF4500); // Orange-red
            output.addProperty("bottle", false);
            output.addProperty("canConflict", false);

            // Root recipe object
            JsonObject recipe = new JsonObject();
            recipe.addProperty("needsFire", true);
            recipe.addProperty("waterLevel", 3);
            recipe.add("mainIngredients", GSON.toJsonTree(List.of(defaultItem), ITEM_LIST_TYPE));
            recipe.add("supplementaryIngredients", new JsonArray()); // Empty
            recipe.add("output", output);

            // Wrap in an array to represent a list of recipes
            JsonArray root = new JsonArray();
            root.add(recipe);

            // Write to file
            Files.writeString(NEW_RECIPES.toPath(), GSON.toJson(root));
            System.out.println("[Potioneer] Default new_formulas.json created.");
        } catch (Exception e) {
            System.err.println("[Potioneer] Failed to create default new_formulas.json: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void createDefaultChangedRecipes() {
        try {
            // Ensure directory exists
            File parentDir = CHANGED_RECIPES.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Root JSON array
            JsonArray root = new JsonArray();

            // Recipe object
            JsonObject recipe = new JsonObject();
            recipe.addProperty("id", -1);
            recipe.addProperty("needsFire", true);
            recipe.addProperty("waterLevel", 2);

            // mainIngredients array
            JsonArray mainIngredients = new JsonArray();

            JsonObject main1 = new JsonObject();
            main1.addProperty("id", "potioneer:sapphire");
            main1.addProperty("Count", 1);
            mainIngredients.add(main1);

            JsonObject main2 = new JsonObject();
            main2.addProperty("id", "minecraft:crafting_table");
            main2.addProperty("Count", 2);
            mainIngredients.add(main2);

            recipe.add("mainIngredients", mainIngredients);

            // supplementaryIngredients array
            JsonArray supplementaryIngredients = new JsonArray();

            JsonObject supp1 = new JsonObject();
            supp1.addProperty("id", "minecraft:iron_nugget");
            supp1.addProperty("Count", 2);
            supplementaryIngredients.add(supp1);

            JsonObject supp2 = new JsonObject();
            supp2.addProperty("id", "potioneer:pecan_leaf");
            supp2.addProperty("Count", 2);
            supplementaryIngredients.add(supp2);

            JsonObject supp3 = new JsonObject();
            supp3.addProperty("id", "potioneer:vial");
            supp3.addProperty("Count", 1);

            // NBT object inside supp3
            JsonObject nbt = new JsonObject();
            JsonObject potionInfo = new JsonObject();
            potionInfo.addProperty("name", "cactus_sap");
            potionInfo.addProperty("amount", 1);
            potionInfo.addProperty("color", 65280);
            nbt.add("potion_info", potionInfo);

            supp3.add("nbt", nbt);
            supplementaryIngredients.add(supp3);

            recipe.add("supplementaryIngredients", supplementaryIngredients);

            // Add recipe object to root array
            root.add(recipe);

            // Write JSON to file
            Files.writeString(CHANGED_RECIPES.toPath(), GSON.toJson(root));
            System.out.println("[Potioneer] Default changed_formulas.json created.");

        } catch (Exception e) {
            System.err.println("[Potioneer] Failed to create default changed_formulas.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<ItemStack> parseItemStacks(JsonArray array) {
        List<ItemWithNBT> items = GSON.fromJson(array, ITEM_LIST_TYPE);
        return items.stream().map(ItemWithNBT::toItemStack).toList();
    }

    private static int count(List<ItemStack> stacks){
        int hold = 0;
        for(ItemStack stc : stacks){
            hold += stc.getCount();
        }
        return hold;
    }

    public static class ItemWithNBT{
        public String id;
        public int Count;
        public JsonObject nbt;

        public ItemStack toItemStack(){
            CompoundTag tag = new CompoundTag();
            tag.putString("id", id);
            tag.putInt("Count", Count);
            if(nbt != null){
                CompoundTag compoundTag;
                try {
                    compoundTag = TagParser.parseTag(nbt.toString());
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
                tag.put("tag", compoundTag);
            }
            return ItemStack.of(tag);
        }

    }

    public static class ChangedRecipesDeserializer implements JsonDeserializer<PotionRecipeData>{
        @Override
        public PotionRecipeData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){
            JsonObject obj = json.getAsJsonObject();
            int id = obj.has("id") ? obj.get("id").getAsInt() : -1;
            boolean needsFire = obj.has("needsFire") && obj.get("needsFire").getAsBoolean();
            int waterLevel = obj.has("waterLevel") ? obj.get("waterLevel").getAsInt() : 2;
            List<ItemStack> mainIngs = parseItemStacks(obj.getAsJsonArray("mainIngredients"));
            List<ItemStack> suppIngs = parseItemStacks(obj.getAsJsonArray("supplementaryIngredients"));

            if ((count(mainIngs) + count(suppIngs)) > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is 9");
            }

            return new PotionRecipeData(new ArrayList<>(mainIngs), new ArrayList<>(suppIngs), waterLevel, needsFire, id);
        }
    }

    public static class NewRecipesDeserializer implements JsonDeserializer<PotionRecipe> {

        @Override
        public PotionRecipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            //int id = obj.has("id") ? obj.get("id").getAsInt() : -1;
            boolean needsFire = obj.has("needsFire") && obj.get("needsFire").getAsBoolean();
            int waterLevel = obj.has("waterLevel") ? obj.get("waterLevel").getAsInt() : 2;
            List<ItemStack> mainIngs = parseItemStacks(obj.getAsJsonArray("mainIngredients"));
            List<ItemStack> suppIngs = parseItemStacks(obj.getAsJsonArray("supplementaryIngredients"));

            if ((count(mainIngs) + count(suppIngs)) > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is 9");
            }
            JsonObject outputObj = obj.getAsJsonObject("output");
            String name = outputObj.get("name").getAsString();
            int amount = outputObj.has("amount") ? Mth.clamp(outputObj.get("amount").getAsInt(), 0, 2) : 1;
            int color = outputObj.has("color") ? outputObj.get("color").getAsInt() : (new Random()).nextInt(0xFFFFFF);
            boolean bottle = outputObj.has("bottle") && outputObj.get("bottle").getAsBoolean();
            boolean canConflict = outputObj.has("canConflict") && outputObj.get("bottle").getAsBoolean();

            return new PotionRecipe(new PotionRecipeData(new ArrayList<>(mainIngs), new ArrayList<>(suppIngs), waterLevel, needsFire, -1),
                    new PotionContentData(name, amount, bottle, color, canConflict));
        }
    }

}
