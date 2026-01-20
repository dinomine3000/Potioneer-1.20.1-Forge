package net.dinomine.potioneer.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.item.custom.CharacteristicItem;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.minecraft.core.RegistryAccess;
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

public class CharacteristicRecipe extends CustomRecipe {
    public CharacteristicRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
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

        if(list.size() != 1) return false;

        ItemStack stack = list.get(0);
        return stack.is(ModItems.BEYONDER_POTION.get()) && stack.hasTag() && stack.getTag().contains("potion_info")
                && PotioneerMathHelper.isInteger(stack.getTag().getCompound("potion_info").getString("name"));


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


        if(list.size() != 1) return ItemStack.EMPTY;
        ItemStack stack = list.get(0);
        if(!stack.is(ModItems.BEYONDER_POTION.get()) || !stack.hasTag() || !stack.getTag().contains("potion_info")) return ItemStack.EMPTY;
        String name = stack.getTag().getCompound("potion_info").getString("name");
        if(PotioneerMathHelper.isInteger(name))
            return CharacteristicItem.createCharacteristic(Integer.parseInt(name));

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


    public static class Serializer implements RecipeSerializer<CharacteristicRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(Potioneer.MOD_ID, "characteristic_recipe");

        @Override
        public CharacteristicRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return new CharacteristicRecipe(resourceLocation, CraftingBookCategory.MISC);
        }

        @Override
        public @Nullable CharacteristicRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return new CharacteristicRecipe(resourceLocation, CraftingBookCategory.MISC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, CharacteristicRecipe potionCauldronRecipe) {

        }
    }
}
