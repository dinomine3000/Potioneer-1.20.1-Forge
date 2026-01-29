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

import java.util.Map;
import java.util.Optional;

public class RitualRecipePage extends RecipePage {
    public RitualRecipePage(Chapter chapter, Component title, Component topText, Component bottomText, ResourceLocation craftingRecipeLocation) {
        super(chapter, title, topText, bottomText, craftingRecipeLocation);
    }

    public RitualRecipePage(Chapter chapter, Component title, String id, ResourceLocation craftingRecipeLocation){
        super(chapter, title, id, craftingRecipeLocation);
        setDrawBottom(false);
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

        pGuiGraphics.blit(texture, leftPos + 20, topPos + 65, 206, 182, 76, 72, textureWidth, textureHeight);
        NonNullList<PotionIngredient> ingredients = getIngredients();
        PotionIngredient base = getBaseMaterial();
        pGuiGraphics.renderFakeItem(base.getRepresentativeStack(), leftPos + 50, topPos + 93);
        for(int i = 0; i < ingredients.size(); i++) {
            PotionIngredient ingredient = ingredients.get(i);
            int px = leftPos, py = topPos;
            switch (i) {
                case 0:
                    px += 50;
                    py += 72;
                    break;
                case 1:
                    px += 72;
                    py += 82;
                    break;
                case 2:
                    px += 72;
                    py += 104;
                    break;
                case 3:
                    px += 50;
                    py += 114;
                    break;
                case 4:
                    px += 28;
                    py += 104;
                    break;
                case 5:
                    px += 28;
                    py += 82;
                    break;
                default:
                    px += 50;
                    py += 93;
            }
            pGuiGraphics.renderFakeItem(ingredient.getRepresentativeStack(), px, py);
            if(ingredient.getCount() > 1)
                pGuiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(ingredient.getCount()), px + 13, py + 13, 0xFFFFFF, true);
        }
//            int px = leftPos + 32 + (i%3)*17;
//            int py = topPos + 45 + Math.floorDiv(i, 3)*17;
//            if(i < 3){
//                if(i == 1)
//                continue;
//            }
//            PotionIngredient ingredient = ingredients.get(i - 3);
//            if(ingredient.isEmpty()) continue;
//            ItemStack stack = ingredient.getRepresentativeStack();
//            pGuiGraphics.renderFakeItem(stack, px, py);
//            if(ingredient.getCount() > 1)
//                pGuiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(ingredient.getCount()), px + 12, py + 12, 0xFFFFFF, true);
//        }
//        for(int i = 0; i < ingredients.size(); i++){
//            if(ingredients.get(i).getCount() == 0) continue;
//            ItemStack stack = ingredients.get(i).getRepresentativeStack();
//            pGuiGraphics.renderFakeItem(stack, leftPos + 20 + (i%3)*17, topPos + 45 + Math.floorDiv(i, 3)*17);
//        }
//        ItemStack result = getResult(getRecipe());
//
//        pGuiGraphics.renderFakeItem(result, leftPos + xOffset + 75, topPos + 45 + 17);

//        pGuiGraphics.blit(texture, leftPos + 32, topPos + 45, 173, 182, 50, 50, textureWidth, textureHeight);
    }
}
