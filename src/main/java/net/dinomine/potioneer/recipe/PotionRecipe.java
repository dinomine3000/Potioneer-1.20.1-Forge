package net.dinomine.potioneer.recipe;

import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public record PotionRecipe(PotionRecipeData input, PotionContentData output) {

    public PotionRecipe(PotionCauldronRecipe ogRecipe){
        this(ogRecipe.getDefaultRecipeData().copy(), ogRecipe.getOutput().copy());
    }

    public boolean matches(PotionCauldronContainer container){
        //will return true if it finds a valid characteristic-like item
        for(int i = 0; i < container.getContainerSize(); i++){
            if(container.getItem(i).hasTag()
                    && container.getItem(i).getTag().contains(MysticalItemHelper.BEYONDER_TAG_ID)){
                CompoundTag beyonderTag = container.getItem(i).getTag().getCompound(MysticalItemHelper.BEYONDER_TAG_ID);
                if(input.id() > -1 && beyonderTag.getInt("id") == input.id()) return true;
            }
        }

        //if there are no main ingredients, it means its a special beyonder potion (seq 1 or 0) and should be based on other criteria,
        //primarily, using the actual characteristics as main ingredients.
        //so to not cause any meaningless conflicts, if a potion has no main ingredients it can only match
        //if the cauldron contains its respective characteristic
        //TODO make the code above actually check for 3 characteristics and uniqueness for seq0
        if(input.main().isEmpty()) return false;
        //will return true if every item in the recipe is contained in the container
        for(ItemStack recipeItem: input.main()){
            if(!contains(container, recipeItem)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean canCraft(PotionCauldronContainer container){
        if(!matches(container)) return false;
        if(input.fire() && !container.isOnFire()) return false;
        if(input.waterLevel() != container.getWaterLevel()) return false;
        for(ItemStack recipeItem: input.supplementary()){
            if(!contains(container, recipeItem)) return false;
        }
        return true;
    }



    private boolean contains(PotionCauldronContainer container, ItemStack item){
        int hold = 0;
        if(item.hasTag()){
            for(int i = 0; i < container.getContainerSize(); ++i) {
                if (item.is(container.getItem(i).getItem())
                        && container.getItem(i).hasTag()
                        && containsTag(item, container.getItem(i))) {
                    hold++;
                }
            }
        } else {
            for(int i = 0; i < container.getContainerSize(); ++i) {
                if (item.is(container.getItem(i).getItem())) {
                    hold++;
                }
            }
        }
        return hold >= item.getCount();
    }

    private boolean containsTag(ItemStack reference, ItemStack test){
        CompoundTag testTag = test.getTag();
        if(testTag == null) return false;
        ArrayList<String> keys = new ArrayList<>(reference.getTag().getAllKeys().stream().toList());
        ArrayList<String> testKeys = new ArrayList<>(test.getTag().getAllKeys().stream().toList());
        if(testKeys.size() < keys.size()) return false;
        for (String key : keys) {
            if (!reference.getTag().get(key).equals(testTag.get(key))) {
                return false;
            }
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
