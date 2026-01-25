package net.dinomine.potioneer.beyonder.pages;

import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Optional;

public abstract class RecipePage extends SplitPage {
    protected final ResourceLocation craftingLocation;
    public RecipePage(Chapter chapter, Component title, Component topText, Component bottomText, ResourceLocation craftingRecipeLocation) {
        super(chapter, title, topText, bottomText);
        this.craftingLocation = craftingRecipeLocation;
    }
    public RecipePage(Chapter chapter, String id, ResourceLocation craftingRecipeLocation) {
        super(chapter, id);
        this.craftingLocation = craftingRecipeLocation;
    }
    public RecipePage(Chapter chapter, Component title, String id, ResourceLocation craftingRecipeLocation){
        super(chapter, title, id);
        this.craftingLocation = craftingRecipeLocation;
    }

    protected Optional<? extends Recipe<?>> getRecipe(){
        if (!FMLEnvironment.dist.isClient()) {
            return Optional.empty();
        }
        if(ClientStatsData.getLevel() == null) return Optional.empty();
        return ClientStatsData.getLevel().getRecipeManager().byKey(craftingLocation);
    }
}
