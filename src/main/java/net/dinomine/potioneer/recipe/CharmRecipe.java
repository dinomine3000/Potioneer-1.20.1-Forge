package net.dinomine.potioneer.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.util.BufferUtils;
import net.dinomine.potioneer.util.PotionIngredient;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CharmRecipe implements Recipe<RitualContainer> {

    private final NonNullList<PotionIngredient> ingredients;

    public PotionIngredient getBaseMaterial() {
        return baseMaterial;
    }

    private final PotionIngredient baseMaterial;
    private final int pathwayId;
    private final String resultingEffectId;
    private final ResourceLocation id;
    private final int scaling;
    private final int durationScaling;

    public CharmRecipe(NonNullList<PotionIngredient> ingredients, PotionIngredient baseMaterial, int pathwayId, String resultingEffectId, int scaling, int durationScaling, ResourceLocation id) {
        this.ingredients = ingredients;
        this.baseMaterial = baseMaterial;
        this.pathwayId = pathwayId;
        this.resultingEffectId = resultingEffectId;
        this.id = id;
        this.scaling = scaling;
        this.durationScaling = durationScaling;
    }

    public String getEffectId(){
        return resultingEffectId;
    }

    public int getPathwayId(){
        return this.pathwayId;
    }

    public NonNullList<PotionIngredient> getRitualIngredients() {
        return ingredients;
    }

    @Override
    public String toString() {
        return this.resultingEffectId + baseMaterial.toString();
    }


    @Override
    public boolean matches(@NotNull RitualContainer ritual, Level level) {
        if(level.isClientSide()) return false;
        if(ritual.getPathwayId() != this.pathwayId) return false;
        for (PotionIngredient ingredient: ingredients) {
            if (!PotionIngredient.contains(ritual, ingredient, false)) {
                return false;
            }
        }
        return PotionIngredient.contains(ritual, baseMaterial, false);
    }

    @Override
    public ItemStack assemble(RitualContainer simpleContainer, RegistryAccess registryAccess) {
        int baseCount = 0;
        for(int i = 0; i < simpleContainer.getContainerSize(); i++){
            ItemStack stack = simpleContainer.getItem(i);
            if(baseMaterial.is(stack)){
                baseCount += 1;
            }
        }
        int sequenceLevel = Mth.clamp((int) (9 - scaling*simpleContainer.getReputationPercent()), 0, 9);
        return MysticalItemHelper.makeCharm(new ItemStack(ModItems.CHARM.get()), resultingEffectId,
                simpleContainer.getPathwayId()*10 + sequenceLevel, durationScaling*baseCount*20);
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return MysticalItemHelper.makeCharm(new ItemStack(ModItems.CHARM.get()), resultingEffectId, 17, 40*5);
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

    public static class Type implements RecipeType<CharmRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "ritual_charm_crafting";
    }

    public static class Serializer implements RecipeSerializer<CharmRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(Potioneer.MOD_ID, "ritual_charm_crafting");

        @Override
        public CharmRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String effectId = GsonHelper.getAsString(pJson, "effectId");
            NonNullList<PotionIngredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "ingredients"));
            if (ingredients.size() > 6)
                throw new JsonParseException("Too many ingredients for charm crafting recipe. The maximum is 6");
            PotionIngredient baseMaterial = PotionIngredient.fromJson(GsonHelper.getNonNull(pJson, "base")).withCount(1);
            int pathwayId = GsonHelper.getAsInt(pJson, "pathwayId");
            int scaling = Mth.clamp(GsonHelper.getAsInt(pJson, "levelScaling"), 0, 8);
            int durScaling = Math.max(GsonHelper.getAsInt(pJson, "durationScaling"), 1);
            return new CharmRecipe(ingredients, baseMaterial, pathwayId, effectId, scaling, durScaling, pRecipeId);
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
        public void toNetwork(FriendlyByteBuf buf, CharmRecipe charmRecipe) {
            charmRecipe.baseMaterial.writeToBuffer(buf);
            int size = charmRecipe.ingredients.size();
            buf.writeInt(size);
            for(int i = 0; i < size; i++){
                charmRecipe.ingredients.get(i).writeToBuffer(buf);
            }
            buf.writeInt(charmRecipe.pathwayId);
            buf.writeInt(charmRecipe.scaling);
            buf.writeInt(charmRecipe.durationScaling);
            BufferUtils.writeStringToBuffer(charmRecipe.resultingEffectId, buf);
        }

        @Override
        public @Nullable CharmRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
            PotionIngredient base = PotionIngredient.readFromBuffer(buf);
            int size = buf.readInt();
            NonNullList<PotionIngredient> ingredients = NonNullList.create();
            for(int i = 0; i < size; i++){
                ingredients.add(PotionIngredient.readFromBuffer(buf));
            }
            int pathwayId = buf.readInt();
            int scaling = buf.readInt();
            int durScaling = buf.readInt();
            String effectId = BufferUtils.readString(buf);
            return new CharmRecipe(ingredients, base, pathwayId, effectId, scaling, durScaling, resourceLocation);
        }
    }
}
