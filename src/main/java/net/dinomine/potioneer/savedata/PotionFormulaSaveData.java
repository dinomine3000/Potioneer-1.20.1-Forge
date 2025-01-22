package net.dinomine.potioneer.savedata;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.recipe.ModRecipes;
import net.dinomine.potioneer.recipe.PotionCauldronRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.ForgeConfig;
import org.apache.logging.log4j.core.jmx.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PotionFormulaSaveData extends SavedData {
    private ArrayList<PotionRecipeData> formulas = new ArrayList<>();
    public boolean refresh = false;
    public ArrayList<PotionRecipeData> getFormulas() {
        return formulas;
    }
    public void setFormulas(ArrayList<PotionRecipeData> formulas) {
        this.formulas = formulas;
    }

    public void requestRefresh(boolean refresh) {
        this.refresh = refresh;
        setDirty();
    }

    public PotionFormulaSaveData(ServerLevel level){
        //this is the standard initialization of the formulas, aka, just reads the json files for the recipes
        System.out.println("reading jsons");
        refresh = false;

        ArrayList<PotionCauldronRecipe> recipes = new ArrayList<>(level.getRecipeManager().getAllRecipesFor(PotionCauldronRecipe.Type.INSTANCE));

        recipes.forEach(rec -> {
            if(rec.recipeData.id() > -1){
                formulas.add(rec.recipeData.copy());
            }
        });


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
        System.out.println("saving to nbt");
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
        System.out.println("loading nbt...");
        boolean refresh = nbt.getBoolean("refresh");
        if(refresh){
            return null;
        }
        int size = nbt.getInt("size");
        ArrayList<PotionRecipeData> found = new ArrayList<>();
        if(size != 0){
            for(int i = 0; i < size; i++){
                found.add(PotionRecipeData.load(nbt.getCompound("formula_" + i)));
            }
        } else {
            System.out.println("initiating WSD with standard values");
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
                    System.out.println("ERROR: Could not update potion recipe. Data read was null.");
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
