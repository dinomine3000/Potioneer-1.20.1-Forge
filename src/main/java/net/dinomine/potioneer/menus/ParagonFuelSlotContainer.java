package net.dinomine.potioneer.menus;

import net.dinomine.potioneer.item.ModItems;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

public class ParagonFuelSlotContainer extends TransientCraftingContainer {
    public ParagonFuelSlotContainer(AbstractContainerMenu pMenu, int pWidth, int pHeight) {
        super(pMenu, pWidth, pHeight);
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        if(pStack.is(ModItems.GOLDEN_DROP.get())) return super.canPlaceItem(pIndex, pStack);
        return false;
    }

    public void consumeFuel(){
        consumeFuel(1);
    }

    public void consumeFuel(int amount){
        getItem(0).shrink(amount);
    }

    public int getFuelAmount(){
        return getItem(0).getCount();
    }

    public boolean hasFuel(){
        return !isEmpty() && getItem(0).is(ModItems.GOLDEN_DROP.get());
    }
}
