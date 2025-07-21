package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.menus.CrafterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;

public class CraftingGuiAbility extends Ability {

    public CraftingGuiAbility(int sequence){
        this.info = new AbilityInfo(109, 32, "Crafting Gui", 40 + sequence, 10, this.getCooldown(), "crafting_gui");
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost() && target instanceof ServerPlayer player){
            NetworkHooks.openScreen(
                    player,
                    new SimpleMenuProvider((i, inventory, player1) ->
                            new CrafterMenu(i, inventory, ContainerLevelAccess.create(player1.level(), player1.getOnPos()), getSequence()),
                            Component.translatable("potioneer.menu.crafter_menu")),
                    buff -> buff.writeInt(getSequence()));

            cap.requestActiveSpiritualityCost(info.cost());
            return true;
        }
        return false;
    }

    //Credit to ars nouveau for this class
    public static class CustomWorkbench extends CraftingMenu {
        private int sequence;
        public CustomWorkbench(int id, Inventory playerInventory, ContainerLevelAccess p_i50090_3_, int sequence) {
            super(id, playerInventory, p_i50090_3_);
            this.sequence = sequence;
        }

        @Override
        public boolean stillValid(Player playerIn) {
            return true;
        }

        @Override
        public void slotsChanged(Container pInventory) {
            this.access.execute((p_39386_, p_39387_) -> slotChangedCraftingGrid(this, p_39386_, this.player, this.craftSlots, this.resultSlots, this.sequence));
        }

        protected static void slotChangedCraftingGrid(AbstractContainerMenu pMenu, Level pLevel, Player pPlayer, CraftingContainer pContainer, ResultContainer pResult, int sequence) {
            if (!pLevel.isClientSide) {
                ServerPlayer player = (ServerPlayer)pPlayer;
                ItemStack result = ItemStack.EMPTY;
                Optional<CraftingRecipe> recipe = pLevel.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, pContainer, pLevel);
                if (recipe.isPresent()) {
                    CraftingRecipe successfulRecipe = (CraftingRecipe)recipe.get();
                    if (pResult.setRecipeUsed(pLevel, player, successfulRecipe)) {
                        ItemStack assembledItem = successfulRecipe.assemble(pContainer, pLevel.registryAccess());
                        if (assembledItem.isItemEnabled(pLevel.enabledFeatures())) {
                            result = assembledItem;
                        }
                    }
                }

                result.setCount((int)Math.round(result.getCount()*(1+(10-sequence)*0.4)-0.5f));

                pResult.setItem(0, result);
                pMenu.setRemoteSlot(0, result);
                player.connection.send(new ClientboundContainerSetSlotPacket(pMenu.containerId, pMenu.incrementStateId(), 0, result));
            }
        }
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }
}
