package net.dinomine.potioneer.datagen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    //TODO generate potion cauldron recipes

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SAPPHIRE_BLOCK.get())
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SSS")
                .define('S', ModItems.SAPPHIRE.get())
                .unlockedBy(getHasName(ModItems.SAPPHIRE.get()), has(ModItems.SAPPHIRE.get()))
                .save(consumer);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.POTION_CAULDRON.get())
                .pattern("# #")
                .pattern("# #")
                .pattern("S#S")
                .define('S', ModItems.SAPPHIRE.get())
                .define('#', Items.IRON_INGOT)
                .unlockedBy(getHasName(ModItems.SAPPHIRE.get()), has(ModItems.SAPPHIRE.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.VIAL.get())
                .pattern("#  ")
                .pattern("#  ")
                .pattern("S  ")
                .define('S', ModItems.SAPPHIRE.get())
                .define('#', Items.GLASS)
                .unlockedBy(getHasName(ModItems.SAPPHIRE.get()), has(ModItems.SAPPHIRE.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FLASK.get())
                .pattern("#  ")
                .pattern("S  ")
                .pattern("S  ")
                .define('S', ModItems.SAPPHIRE.get())
                .define('#', Items.GLASS)
                .unlockedBy(getHasName(ModItems.SAPPHIRE.get()), has(ModItems.SAPPHIRE.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DIVINATION_ROD.get())
                .pattern("ICI")
                .pattern("IW ")
                .pattern(" W ")
                .define('I', Items.IRON_INGOT)
                .define('W', ItemTags.PLANKS)
                .define('C', Items.CHARCOAL)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(consumer);

        //straw man / voodoo doll
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.VOODOO_DOLL.get())
                .pattern("CHC")
                .pattern("RSR")
                .pattern("RCR")
                .define('C', Items.COAL)
                .define('R', Items.BLAZE_ROD)
                .define('S', Items.SOUL_TORCH)
                .define('H', Tags.Items.HEADS)
                .unlockedBy(getHasName(Items.SOUL_TORCH), has(Items.SOUL_TORCH))
                .save(consumer);

        //coin
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIN_ITEM.get())
                .pattern(" I ")
                .pattern("INI")
                .pattern(" I ")
                .define('N', Items.GOLD_NUGGET)
                .define('I', Items.IRON_NUGGET)
                .unlockedBy(getHasName(Items.GOLD_NUGGET), has(Items.GOLD_NUGGET))
                .save(consumer);

        //dagger
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.RITUAL_DAGGER.get())
                .pattern(" S ")
                .pattern(" S ")
                .pattern("TBT")
                .define('S', ModItems.SAPPHIRE.get())
                .define('B', Items.BLAZE_ROD)
                .define('T', Items.STRING)
                .unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
                .save(consumer);

        //pedestal
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RITUAL_PEDESTAL.get())
                .pattern("DPD")
                .pattern("PSP")
                .pattern("PSP")
                .define('S', ModItems.SAPPHIRE.get())
                .define('P', Blocks.POLISHED_DIORITE)
                .define('D', Blocks.DIORITE)
                .unlockedBy(getHasName(Items.DIORITE), has(Items.DIORITE))
                .save(consumer);

        //altar
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RITUAL_ALTAR.get())
                .pattern("CSC")
                .pattern("PPP")
                .pattern("OOO")
                .define('S', ModItems.SAPPHIRE.get())
                .define('C', ItemTags.WOOL_CARPETS)
                .define('O', Blocks.CRYING_OBSIDIAN)
                .define('P', ItemTags.PLANKS)
                .unlockedBy(getHasName(Items.CRYING_OBSIDIAN), has(ItemTags.WOOL))
                .save(consumer);

        //sapphire from block
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SAPPHIRE.get(), 9)
                .requires(ModBlocks.SAPPHIRE_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.SAPPHIRE_BLOCK.get()), has(ModBlocks.SAPPHIRE_BLOCK.get()))
                .save(consumer);

        //ink bottle
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INK_BOTTLE.get())
                .requires(Items.INK_SAC)
                .requires(ModItems.SAPPHIRE.get())
                .requires(Items.FEATHER)
                .unlockedBy(getHasName(Items.INK_SAC), has(Items.INK_SAC))
                .save(consumer);
    }


    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult,
                    pExperience, pCookingTime, pCookingSerializer)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer, Potioneer.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }

    }

}
