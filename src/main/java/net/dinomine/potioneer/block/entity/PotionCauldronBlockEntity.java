package net.dinomine.potioneer.block.entity;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.dinomine.potioneer.block.custom.PotionCauldronBlock.RESULT;
import static net.dinomine.potioneer.block.custom.PotionCauldronBlock.WATER_LEVEL;

public class PotionCauldronBlockEntity extends BlockEntity {

    private static final int MAX_CAPACITY= 9;
    private ItemStack result;
    private final ItemStackHandler itemHandler = new ItemStackHandler(MAX_CAPACITY){
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    //public static final Capability<IItemHandler> ITEM_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});

    public PotionCauldronBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);


        this.result = ItemStack.EMPTY;
    }

    public PotionCauldronBlockEntity(BlockPos pPos, BlockState pState){
        super(ModBlockEntities.POTION_CAULDRON_BLOCK_ENTITY.get(), pPos, pState);

        this.result = ItemStack.EMPTY;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return ModBlockEntities.POTION_CAULDRON_BLOCK_ENTITY.get().create(pos, state);
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            return handler.cast();
        }

        return super.getCapability(cap);
    }

    public ItemStack addIngredient(ItemStack itemStack, boolean shrink){
        ItemStack res = addItem(itemStack, shrink);
        craft();
        return res;
    }

    private int caretPosition(){
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if(itemHandler.getStackInSlot(i).isEmpty()){
                return i;
            }
        }
        return MAX_CAPACITY;
    }

    private ItemStack addItem(ItemStack itemStack, boolean shrink){
        int numItems = caretPosition();
        if(itemStack != ItemStack.EMPTY && numItems >= 0 && numItems < MAX_CAPACITY){
            itemHandler.setStackInSlot(numItems, shrink ? itemStack.split(1) : itemStack.copyWithCount(1));
            level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 1f, 1f);
            setChanged();
            /*
            System.out.println("-----Items in Item Handler-----");
            System.out.println(itemHandler.getStackInSlot(0));
            System.out.println(itemHandler.getStackInSlot(1));
            System.out.println(itemHandler.getStackInSlot(2));
            System.out.println(itemHandler.getStackInSlot(3));
            System.out.println(itemHandler.getStackInSlot(4));*/
        }
        return itemStack;
    }

    public ItemStack removeItem(){
        if(!hasResult() && !isEmpty()){
            int pos = caretPosition() - 1;
            ItemStack rem = itemHandler.getStackInSlot(pos).copy();
            itemHandler.setStackInSlot(pos, ItemStack.EMPTY);
            return rem;
        }
        return ItemStack.EMPTY;
    }

    public boolean hasResult(){
        return !this.result.isEmpty();
    }

    public ItemStack getResult(){
        return this.result;
    }

    public ItemStack extractResult(){
        ItemStack output = getResult();
        this.result = ItemStack.EMPTY;
        clearContent();
        level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(RESULT, false));
        return output;
    }

    public void craft(){
        NonNullList<ItemStack> items = NonNullList.withSize(itemHandler.getSlots(), ItemStack.EMPTY);
        for(int i = 0; i < itemHandler.getSlots(); i++){
            items.set(i, itemHandler.getStackInSlot(i));
        }
        if(compare(items, Items.IRON_INGOT)
                && compare(items, ModItems.SAPPHIRE.get())
                && level.getBlockState(getBlockPos()).getValue(WATER_LEVEL) > 2
                && this.result.isEmpty()){
            this.result = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.NIGHT_VISION);
            level.playSound(null, worldPosition, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 1f, 1f);
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(RESULT, true));

        }
        setChanged();
    }

    private boolean compare(NonNullList<ItemStack> items, Item test){
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getItem() == test){
                return true;
            }
        }
        return false;
    }

    public void dropIngredients(Level pLevel,BlockPos pPos){
        NonNullList<ItemStack> items = NonNullList.withSize(itemHandler.getSlots(), ItemStack.EMPTY);
        for(int i = 0; i < itemHandler.getSlots(); i++){
            items.set(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(pLevel, pPos, items);
    }

    public boolean isEmpty(){
        return caretPosition() == 0;
    }

    private void clearContent() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        handler = LazyOptional.of(() -> itemHandler);
    }



    @Override
    protected void saveAdditional(CompoundTag pTag) {

        CompoundTag modData = new CompoundTag();
        modData.put("inventory", itemHandler.serializeNBT());

        CompoundTag result = new CompoundTag();
        this.result.save(result);

        modData.put("result", result);
        pTag.put(Potioneer.MOD_ID, modData);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        CompoundTag modData = pTag.getCompound(Potioneer.MOD_ID);
        this.result = ItemStack.of(modData.getCompound("result"));
        itemHandler.deserializeNBT(modData.getCompound("inventory"));
    }

    private NonNullList<ItemStack> getItemList(){
        NonNullList<ItemStack> res = NonNullList.withSize(itemHandler.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            res.set(i, itemHandler.getStackInSlot(i));
        }
        return res;
    }

    public NonNullList<ItemStack> getRenderStack(){
        return (hasResult() ? NonNullList.withSize(itemHandler.getSlots(), ItemStack.EMPTY): getItemList());
    }

    @Override
    public @org.jetbrains.annotations.Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
