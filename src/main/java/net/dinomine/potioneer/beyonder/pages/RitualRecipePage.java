package net.dinomine.potioneer.beyonder.pages;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.recipe.CharmRecipe;
import net.dinomine.potioneer.recipe.ModRecipes;
import net.dinomine.potioneer.util.PotionIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Optional;

public class RitualRecipePage extends RecipePage {
    public RitualRecipePage(Chapter chapter, Component title, Component topText, Component bottomText, ResourceLocation craftingRecipeLocation) {
        super(chapter, title, topText, bottomText, craftingRecipeLocation);
    }

    public RitualRecipePage(Chapter chapter, Component title, String id, ResourceLocation craftingRecipeLocation){
        super(chapter, title, id, craftingRecipeLocation);
    }

    public RitualRecipePage(Chapter chapter, Component title, String id){
        this(chapter, title, id, new ResourceLocation(Potioneer.MOD_ID, id));
    }

    protected PotionIngredient getBaseMaterial(){
        Optional<? extends Recipe<?>> optionalRecipe = getRecipe();
        return optionalRecipe.map(rec -> ((CharmRecipe) rec).getBaseMaterial()).orElse(PotionIngredient.EMPTY);
    }

    protected NonNullList<PotionIngredient> getIngredients(){
        Optional<? extends Recipe<?>> optionalRecipe = getRecipe();
        return optionalRecipe.map(rec -> ((CharmRecipe) rec).getRitualIngredients()).orElseGet(NonNullList::create);
    }

    protected void validateRecipe(){
        Optional<? extends Recipe<?>> optionalRecipe = getRecipe();
        if(optionalRecipe.isPresent() && optionalRecipe.get().getType() != CharmRecipe.Type.INSTANCE){
            throw new RuntimeException("Invalid recipe type given to Ritual Recipe Page: " + optionalRecipe.get().getType());
        }
    }

    @Override
    public void drawMiddle(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight) {
        validateRecipe();
        pGuiGraphics.blit(texture, leftPos + 32, topPos + 45, 173, 182, 50, 50, textureWidth, textureHeight);
        NonNullList<PotionIngredient> ingredients = getIngredients();
        PotionIngredient base = getBaseMaterial();
        for(int i = 0; i < Math.min(ingredients.size() + 3, 9); i++){
            int px = leftPos + 32 + (i%3)*17;
            int py = topPos + 45 + Math.floorDiv(i, 3)*17;
            if(i < 3){
                if(i == 1)
                    pGuiGraphics.renderFakeItem(base.getRepresentativeStack(), px, py);
                continue;
            }
            PotionIngredient ingredient = ingredients.get(i - 3);
            if(ingredient.isEmpty()) continue;
            ItemStack stack = ingredient.getRepresentativeStack();
            pGuiGraphics.renderFakeItem(stack, px, py);
            if(ingredient.getCount() > 1)
                pGuiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(ingredient.getCount()), px + 12, py + 12, 0xFFFFFF, true);
        }
    }
}
