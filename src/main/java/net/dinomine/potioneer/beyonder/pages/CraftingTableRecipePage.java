package net.dinomine.potioneer.beyonder.pages;

import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CraftingTableRecipePage extends RecipePage {
    public CraftingTableRecipePage(Chapter chapter, Component title, Component topText, Component bottomText, ResourceLocation craftingRecipeLocation) {
        super(chapter, title, topText, bottomText, craftingRecipeLocation);
    }

    @Override
    public void drawMiddle(GuiGraphics pGuiGraphics, ResourceLocation texture, int leftPos, int topPos, int imageWidth, int imageHeight, int textureWidth, int textureHeight) {
        pGuiGraphics.blit(texture, leftPos + 32, topPos + 46, 173, 182, 50, 50, textureWidth, textureHeight);
        NonNullList<Ingredient> ingredients = getIngredients(ClientStatsData.getLevel());
        for(int i = 0; i < ingredients.size(); i++){
            if(ingredients.get(i).getItems().length == 0) continue;
            ItemStack stack = ingredients.get(i).getItems()[0];
            pGuiGraphics.renderFakeItem(stack, leftPos + 32 + (i%3)*17, topPos + 46 + Math.floorDiv(i, 3)*17);
        }
    }
}
