package net.dinomine.potioneer.menus;

import net.dinomine.potioneer.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class CrafterMenu extends CraftingMenu {
    private final ParagonFuelSlotContainer fuelContainer;
    private final int sequence;
    private boolean quickCrafted = false;
    public boolean consumeFuel = false;

    //client constructor
    public CrafterMenu(int i, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(i, inventory, friendlyByteBuf.readInt());
    }

    public CrafterMenu(int pContainerId, Inventory pPlayerInventory, int sequence) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL, sequence);
    }

    public CrafterMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pLevelAccess, int sequence) {
        super(pContainerId, pPlayerInventory, pLevelAccess);
        this.menuType = ModMenuTypes.CRAFTER_MENU.get();

        fuelContainer = new ParagonFuelSlotContainer(this, 1, 1);
        this.addSlot(new Slot(fuelContainer, 0, 124, 60));
        this.sequence = sequence;
    }

    public void consumeFuelIfAvailable(Player player, ItemStack crafting){
        if(consumeFuel){
            fuelContainer.removeItem(0, 1);
            unstackOverstackedStack(player, crafting);
        }
    }

    private void unstackOverstackedStack(Player player, ItemStack crafting){
        ItemStack base = crafting.copy();
        while(base.getCount() > base.getMaxStackSize()){
            crafting.setCount(crafting.getMaxStackSize());

            ItemStack extra = base.copy();
            extra.setCount(base.getCount() - base.getMaxStackSize());

            base.setCount(base.getMaxStackSize());
            int test = player.getInventory().getSlotWithRemainingSpace(base);
            int test2 = test == -1 ? player.getInventory().getFreeSlot() : test;
            if(test2 == -1){
                player.drop(base, false, false);
            } else {
                player.getInventory().add(test2, base);
            }
            base = extra;
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot slot = this.slots.get(pIndex);
        if(pIndex != 46 && slot.hasItem() && slot.getItem().is(ModItems.GOLDEN_DROP.get()) && fuelContainer.canPlaceItem(0, slot.getItem())){
            ItemStack item = slot.getItem();
            if(this.moveItemStackTo(item, 46, 47, true)){
                return ItemStack.EMPTY;
            }
        }

        ItemStack overStackCopy = ItemStack.EMPTY;
        boolean quickMoveFlag = pIndex == 0 && slot.hasItem() && slot.getItem().getCount() > slot.getItem().getMaxStackSize();
        if(quickMoveFlag){
            overStackCopy = slot.getItem().copy();
            slot.getItem().setCount(slot.getItem().getMaxStackSize());
        }
        ItemStack res = super.quickMoveStack(pPlayer, pIndex);
        if(quickMoveFlag && !res.isEmpty()) unstackOverstackedStack(pPlayer, overStackCopy);
        slotsChanged(fuelContainer);
        return res;
    }

    @Override
    public void slotsChanged(Container pInventory) {
        this.access.execute((p_39386_, p_39387_) -> slotChangedCraftingGrid(this, p_39386_, this.player, this.craftSlots, this.resultSlots, this.fuelContainer));
    }


    protected static void slotChangedCraftingGrid(CrafterMenu pMenu, Level pLevel, Player pPlayer, CraftingContainer pContainer, ResultContainer pResult, TransientCraftingContainer fuelContainer) {
        if (!pLevel.isClientSide) {
            ServerPlayer player = (ServerPlayer)pPlayer;
            ItemStack result = ItemStack.EMPTY;
            Optional<CraftingRecipe> recipe = pLevel.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, pContainer, pLevel);
            pMenu.consumeFuel = false;
            if (recipe.isPresent()) {
                CraftingRecipe successfulRecipe = (CraftingRecipe)recipe.get();
                if (pResult.setRecipeUsed(pLevel, player, successfulRecipe)) {
                    ItemStack assembledItem = successfulRecipe.assemble(pContainer, pLevel.registryAccess());
                    if (assembledItem.isItemEnabled(pLevel.enabledFeatures())) {
                        result = assembledItem;

                        if(fuelContainer.getItem(0).is(ModItems.GOLDEN_DROP.get()) && !(recipe.get() instanceof CustomRecipe)){
                            int count = (int)Math.round(result.getCount()*(1+(10-pMenu.sequence)*0.4)-0.5f);
                            pMenu.consumeFuel = count != result.getCount();
                            result.setCount(count);
                        }
                    }
                }
            }


            pResult.setItem(0, result);
            pMenu.setRemoteSlot(0, result);
            player.connection.send(new ClientboundContainerSetSlotPacket(pMenu.containerId, pMenu.incrementStateId(), 0, result));
        }
    }

    @Override
    public void clearCraftingContent() {
        super.clearCraftingContent();
        this.fuelContainer.clearContent();
    }

    @Override
    public boolean recipeMatches(Recipe<? super CraftingContainer> pRecipe) {
        return pRecipe.matches(this.craftSlots, this.player.level());
    }

    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((p_39371_, p_39372_) -> {
            this.clearContainer(pPlayer, this.fuelContainer);
        });
    }

    @Override
    public int getSize() {
        return 11;
    }
}
