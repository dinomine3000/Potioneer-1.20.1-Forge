package net.dinomine.potioneer.beyonder.pages;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Optional;

public abstract class RecipePage extends SplitPage {
    private final ResourceLocation craftingLocation;
    public RecipePage(Chapter chapter, Component title, Component topText, Component bottomText, ResourceLocation craftingRecipeLocation) {
        super(chapter, title, topText, bottomText);
        this.craftingLocation = craftingRecipeLocation;
    }

    protected NonNullList<Ingredient> getIngredients(Level level){
        if (!FMLEnvironment.dist.isClient()) {
            return NonNullList.create();
        }
        if(level == null) return NonNullList.create();
        Optional<? extends Recipe<?>> optionalRecipe = level.getRecipeManager().byKey(craftingLocation);
        return optionalRecipe.map(Recipe::getIngredients).orElseGet(NonNullList::create);
    }
}
