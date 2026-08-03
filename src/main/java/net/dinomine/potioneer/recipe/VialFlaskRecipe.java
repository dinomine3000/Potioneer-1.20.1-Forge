package net.dinomine.potioneer.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.util.misc.ModTags;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VialFlaskRecipe extends CustomRecipe {
    public VialFlaskRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        List<ItemStack> list = Lists.newArrayList();

        //filling list
        for(int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemstack = craftingContainer.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
            }
        }

        //checking if is lone flask or vial
        if(list.size() == 1){
            ItemStack stack = list.get(0);
            if(isVialOrFlask(stack)){
                if(ModTags.hasTag(ModTags.TAGS.POTION, stack)){
                    return true;
                }
            }
        }

        //checking if its two vials with identical contents OR 2 flasks with half empty contents
        if(list.size() == 2){
            ItemStack i0 = list.get(0);
            ItemStack i1 = list.get(1);
            if(!isVialOrFlask(i0) || !isVialOrFlask(i1) ) return false;
            if(!ModTags.hasTag(ModTags.TAGS.POTION, i0) || !ModTags.hasTag(ModTags.TAGS.POTION, i1)) return false;
            CompoundTag t0 = ModTags.getTagFromItem(ModTags.TAGS.POTION, i0);
            CompoundTag t1 = ModTags.getTagFromItem(ModTags.TAGS.POTION, i1);

            if(!t0.equals(t1)) return false;
            return ModTags.PotionInfoTag.sumAmountsLessThan(t0, t1, ModTags.PotionInfoTag.MAX_FLASK_AMOUNT);
        }
        return false;
    }

    private boolean isVialOrFlask(ItemStack stack){
        return stack.is(ModItems.VIAL.get()) || stack.is(ModItems.FLASK.get());
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        List<ItemStack> list = Lists.newArrayList();

        //filling list
        for(int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemstack = craftingContainer.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
            }
        }

        if(list.size() == 1){
            ItemStack stack = list.get(0);
            return ModTags.PotionInfoTag.convertStack(stack);
        } else {
            ItemStack i0 = list.get(0);
            ItemStack i1 = list.get(1);
            return ModTags.PotionInfoTag.sumContentsIntoFlask(i0, i1);
        }
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }


    public static class Serializer implements RecipeSerializer<VialFlaskRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(Potioneer.MOD_ID, "vial_flask_craft");

        @Override
        public VialFlaskRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return new VialFlaskRecipe(resourceLocation, CraftingBookCategory.MISC);
        }

        @Override
        public @Nullable VialFlaskRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return new VialFlaskRecipe(resourceLocation, CraftingBookCategory.MISC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, VialFlaskRecipe potionCauldronRecipe) {

        }
    }
}
