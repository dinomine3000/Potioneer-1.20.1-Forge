package net.dinomine.potioneer.beyonder.pages;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.recipe.CharmRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

public class CraftingTableRecipePage extends RecipePage {
    public CraftingTableRecipePage(Chapter chapter, Component title, Component topText, Component bottomText, ResourceLocation craftingRecipeLocation) {
        super(chapter, title, topText, bottomText, craftingRecipeLocation);
    }

    public CraftingTableRecipePage(Chapter chapter, String id, ResourceLocation craftingRecipeLocation) {
        super(chapter, id, craftingRecipeLocation);
    }

    public CraftingTableRecipePage(Chapter chapter, String id){
        this(chapter, id, new ResourceLocation(Potioneer.MOD_ID, id));
    }

    private NonNullList<Ingredient> getIngredients(Optional<? extends Recipe<?>> optionalRecipe){
        return optionalRecipe.map(Recipe::getIngredients).orElseGet(NonNullList::create);
    }

    private ItemStack getResult(Optional<? extends Recipe<?>> optionalRecipe){
        return optionalRecipe.map(recipe -> recipe.getResultItem(ClientStatsData.getLevel().registryAccess())).orElse(ItemStack.EMPTY);
    }

    @Override
    public void drawMiddle(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight) {
        int xOffset = 10;
        pGuiGraphics.blit(texture, leftPos + xOffset, topPos + 45, 156, 182, 50, 50, textureWidth, textureHeight);
        NonNullList<Ingredient> ingredients = getIngredients(getRecipe());
        for(int i = 0; i < ingredients.size(); i++){
            if(ingredients.get(i).getItems().length == 0) continue;
            ItemStack stack = ingredients.get(i).getItems()[0];
            pGuiGraphics.renderFakeItem(stack, leftPos + xOffset + (i%3)*17, topPos + 45 + Math.floorDiv(i, 3)*17);
        }
        ItemStack result = getResult(getRecipe());

        pGuiGraphics.renderFakeItem(result, leftPos + xOffset + 75, topPos + 45 + 17);
    }
}
