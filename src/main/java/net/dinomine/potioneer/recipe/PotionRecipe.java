package net.dinomine.potioneer.recipe;

import net.dinomine.potioneer.util.misc.ModTags;
import net.dinomine.potioneer.util.PotionIngredient;
import net.minecraft.nbt.CompoundTag;

public record PotionRecipe(PotionRecipeData input, PotionContentData output) {

    public PotionRecipe(PotionCauldronRecipe ogRecipe) {
        this(ogRecipe.getDefaultRecipeData().copy(), ogRecipe.getOutput().copy());
    }

    public boolean matches(PotionCauldronContainer container) {
        //TODO adjust logic to account for sequence 1-like formulas
        if (input.main().isEmpty()) return false;
        if (input.id() > -1) {
            //will return true if it finds a valid characteristic-like item
            for (int i = 0; i < container.getContainerSize(); i++) {
                CompoundTag charTag = ModTags.getTagFromItem(ModTags.TAGS.BEYONDER, container.getItem(i));
                if (charTag != null) {
                    if (charTag.getInt("id") == input.id()) return true;
                }
            }
        }
        for (PotionIngredient ingredient : input.main()) {
            if (!PotionIngredient.contains(container, ingredient, false)) {
                return false;
            }
        }
        return true;
    }

    public boolean canCraftSuccessfully(PotionCauldronContainer container) {
        if (!matches(container)) return false;
        if (input.fire() != container.isOnFire()) return false;
        if (input.waterLevel() != container.getWaterLevel()) return false;
        for (PotionIngredient recipeItem : input.supplementary()) {
            if (!PotionIngredient.contains(container, recipeItem, false)) return false;
        }
        return true;
    }


    public void save(CompoundTag compound) {
        compound.put("input", input.save(new CompoundTag()));
        compound.put("output", output.save(new CompoundTag()));
    }

    public static PotionRecipe load(CompoundTag compound) {
        PotionRecipeData input = PotionRecipeData.load(compound.getCompound("input"));
        PotionContentData output = PotionContentData.load(compound.getCompound("output"));
        return new PotionRecipe(input, output);
    }

}
