package net.dinomine.potioneer.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.item.ModItems;
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
            if(stack.is(ModItems.VIAL.get()) || stack.is(ModItems.FLASK.get())){
                if(stack.hasTag()){
                    return !stack.getTag().getCompound("potion_info").isEmpty();
                }
            }
        }

        //checking if its two vials with identical contents
        if(list.size() == 2){
            ItemStack i0 = list.get(0);
            ItemStack i1 = list.get(1);
            if((i0.is(ModItems.VIAL.get()) || i0.is(ModItems.FLASK.get()))
                    && (i1.is(ModItems.FLASK.get()) || i1.is(ModItems.VIAL.get())) ){
                if(i0.hasTag() && i1.hasTag()){
                    return i0.getTag().getCompound("potion_info").equals(i1.getTag().getCompound("potion_info"));
                }
            }
        }
        return false;
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
            if(stack.is(ModItems.VIAL.get())){
                CompoundTag tag = stack.getTag().copy();
                ItemStack res = new ItemStack(ModItems.FLASK.get());
                res.setTag(tag);
                return res;
            } else if (stack.is(ModItems.FLASK.get())){
                CompoundTag tag = stack.getTag().copy();
                ItemStack res = new ItemStack(ModItems.VIAL.get());
                res.setCount(tag.getCompound("potion_info").getInt("amount"));
                tag.getCompound("potion_info").putInt("amount", 1);
                res.setTag(tag);
                return res;
            }
        } else {
            ItemStack i0 = list.get(0);
            CompoundTag tag0 = i0.getTag().copy();
            tag0.getCompound("potion_info").putInt("amount", 2);
            ItemStack res = new ItemStack(ModItems.FLASK.get());
            res.setTag(tag0);
            return res;
        }
        return ItemStack.EMPTY;
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
