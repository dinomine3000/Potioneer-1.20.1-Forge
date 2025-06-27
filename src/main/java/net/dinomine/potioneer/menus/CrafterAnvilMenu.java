package net.dinomine.potioneer.menus;

import net.dinomine.potioneer.item.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;

public class CrafterAnvilMenu extends AnvilMenu {
    private static final int enchantment_id = 2;
    private final ParagonFuelSlotContainer fuelContainer;
    private final int sequence;
    private final DataSlot enchantmentSeed;
    public boolean consumeFuel = false;
    private final RandomSource random;

    //client constructor
    public CrafterAnvilMenu(int i, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(i, inventory, friendlyByteBuf.readInt());
    }

    public CrafterAnvilMenu(int pContainerId, Inventory pPlayerInventory, int sequence) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL, sequence);
    }

    public CrafterAnvilMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pLevelAccess, int sequence) {
        super(pContainerId, pPlayerInventory, pLevelAccess);
        this.menuType = ModMenuTypes.CRAFTER_ANVIL_MENU.get();

        fuelContainer = new ParagonFuelSlotContainer(this, 1, 1);
        this.addSlot(new Slot(fuelContainer, 0, 80, 65));
        this.sequence = sequence;
        this.enchantmentSeed = DataSlot.standalone();
        this.random = RandomSource.create();
        this.addDataSlot(this.enchantmentSeed).set(pPlayerInventory.player.getEnchantmentSeed());
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        Slot slot = this.slots.get(pIndex);
        if(pIndex > 2 && pIndex < 39 && slot.hasItem() && fuelContainer.canPlaceItem(0, slot.getItem())){
            ItemStack item = slot.getItem();
            if(!this.moveItemStackTo(item, 39, 40, true)){
                return ItemStack.EMPTY;
            }
            if (slot.getItem().isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }
            slot.setChanged();
            createResult();
            //slot.onTake(pPlayer, slot.getItem());
        }
        if(pIndex == 39 && slot.hasItem() ){
            ItemStack item = slot.getItem();
            if(!this.moveItemStackTo(item, 3, 39, true)){
                return ItemStack.EMPTY;
            }
            if (slot.getItem().isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            slot.onTake(pPlayer, slot.getItem());
        }



        return super.quickMoveStack(player, pIndex);

    }

    @Override
    protected boolean mayPickup(Player pPlayer, boolean pHasStack) {
        return fuelContainer.hasFuel() || super.mayPickup(pPlayer, pHasStack);
    }

    @Override
    public void createResult() {
        // instabuild ability necessary for the anvil to generate the result everytime, regardless of cost
        boolean oldBuild = this.player.getAbilities().instabuild;
        this.player.getAbilities().instabuild = fuelContainer.hasFuel();
        super.createResult();
        this.player.getAbilities().instabuild = oldBuild;

        if(fuelContainer.hasFuel()){
            this.cost.set(0);
        }
        if(this.inputSlots.getItem(0).is(Items.BOOK) && this.inputSlots.getItem(1).isEmpty()){
            enchantBook(fuelContainer.getFuelAmount()*2);
        } else {
            if (fuelContainer.hasFuel() && this.inputSlots.getItem(0).is(Items.BOOK) && this.inputSlots.getItem(1).is(Items.ENCHANTED_BOOK)){
                this.resultSlots.setItem(0, this.inputSlots.getItem(1).copy());
            }
        }
    }

    @Override
    protected void onTake(Player pPlayer, ItemStack pStack) {
        boolean oldBuild = pPlayer.getAbilities().instabuild;

        ItemStack book = ItemStack.EMPTY;
        ItemStack originalBooks = ItemStack.EMPTY;
        ItemStack itemL = this.inputSlots.getItem(0);
        ItemStack itemR = this.inputSlots.getItem(1);

        pPlayer.getAbilities().instabuild = fuelContainer.hasFuel();

        if(itemL.getItem().isEnchantable(itemL) && itemR.is(Items.ENCHANTED_BOOK)){
            book = itemR.copy();
        } else if (itemL.is(Items.BOOK) && itemR.isEmpty()){
            originalBooks = itemL.copyWithCount(itemL.getCount() - 1);
            onTakeEnchantedBook(pPlayer, pStack);

            //ability line here is necessary so the upperclass anvil code doesnt consume xp -> that happens in onTakeEnchantedBook
            pPlayer.getAbilities().instabuild = true;
        } else if (itemL.is(Items.BOOK) && itemR.is(Items.ENCHANTED_BOOK)){
            originalBooks = itemL.copyWithCount(itemL.getCount() - resultSlots.getItem(0).getCount());
            book = itemR.copy();
        }

        super.onTake(pPlayer, pStack);
        fuelContainer.consumeFuel();
        this.inputSlots.setItem(0, originalBooks);
        this.inputSlots.setItem(1, book);

        pPlayer.getAbilities().instabuild = oldBuild;
    }



    private void enchantBook(int bookshelfLevel){
        this.access.execute((pLevel, pPos) -> {
            System.out.println("Enchanting a book");

            ItemStack book = new ItemStack(Items.BOOK);
            this.random.setSeed(this.enchantmentSeed.get());
            int pId = enchantment_id;

            this.cost.set(EnchantmentHelper.getEnchantmentCost(this.random, pId, bookshelfLevel, book));
            if (this.cost.get() < pId + 1) {
                this.cost.set(0);
            }
            int enchantmentLevel = ForgeEventFactory.onEnchantmentLevelSet(pLevel, pPos, pId, (int)bookshelfLevel, book, this.cost.get());


            this.random.setSeed(this.enchantmentSeed.get() + pId);
            List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, book, enchantmentLevel, false);
            if (list.size() > 1) {
                list.remove(this.random.nextInt(list.size()));
            }

            if (!list.isEmpty()) {

                book = new ItemStack(net.minecraft.world.item.Items.ENCHANTED_BOOK);
                CompoundTag compoundtag = inputSlots.getItem(0).getTag();
                if (compoundtag != null) {
                    book.setTag(compoundtag.copy());
                }

                for (EnchantmentInstance enchantmentInstance : list) {
                    EnchantedBookItem.addEnchantment(book, enchantmentInstance);
                }

                //this.enchantSlots.setChanged();

                //this.slotsChanged(this.enchantSlots);
            }
            this.resultSlots.setItem(0, book);

            this.broadcastChanges();
        });
    }

    private void onTakeEnchantedBook(Player pPlayer, ItemStack pStack){
        System.out.println("Taking enchanted book");
        //pPlayer.onEnchantmentPerformed(pStack, 0);
        pPlayer.onEnchantmentPerformed(pStack, fuelContainer.hasFuel() ? 0 : enchantment_id+1);
        pPlayer.awardStat(Stats.ENCHANT_ITEM);
        if (pPlayer instanceof ServerPlayer) {
            CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)pPlayer, pStack, enchantment_id+1);
        }
        this.enchantmentSeed.set(pPlayer.getEnchantmentSeed());
        pPlayer.level().playSound(null, pPlayer.getOnPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, pPlayer.level().random.nextFloat() * 0.1F + 0.9F);

    }

    @Override
    public void slotsChanged(Container pInventory) {
        super.slotsChanged(pInventory);
        if(pInventory == fuelContainer){
            createResult();
        }
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((p_39796_, p_39797_) -> this.clearContainer(pPlayer, this.fuelContainer));
    }

    public boolean hasFuel(){
        return this.fuelContainer.hasFuel();
    }
}
