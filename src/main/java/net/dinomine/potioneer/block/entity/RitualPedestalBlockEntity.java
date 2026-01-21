package net.dinomine.potioneer.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RitualPedestalBlockEntity extends BlockEntity {
    public RitualPedestalBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RITUAL_PEDESTAL_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    private boolean processing = false;
    private final ItemStackHandler itemHandler = new ItemStackHandler(1){
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
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return true;
        }
    };
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            return handler.cast();
        }
        return super.getCapability(cap);
    }

    public void dropIngredients(Level pLevel,BlockPos pPos){
        if(processing) return;
        NonNullList<ItemStack> items = NonNullList.withSize(itemHandler.getSlots(), ItemStack.EMPTY);
        for(int i = 0; i < itemHandler.getSlots(); i++){
            items.set(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(pLevel, pPos, items);
    }

    public ItemStack addOrRemoveItem(ItemStack stack) {
        ItemStack resultHandItem = stack.copy();
        if(processing) return resultHandItem;
        if(itemHandler.isItemValid(0, stack)){
            resultHandItem = itemHandler.extractItem(0, 1, false);
            if(!stack.isEmpty()){
                itemHandler.insertItem(0, stack.copyWithCount(1), false);
            }
            setChanged();
        }
        return resultHandItem;
    }

    public void forcefullySetItem(ItemStack stack){
        itemHandler.insertItem(0, stack, false);
    }

    public ItemStack getRenderStack(){
        return itemHandler.getStackInSlot(0);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putBoolean("processing", processing);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        processing = pTag.getBoolean("processing");
    }

    public void particleTick(Level level, BlockPos blockPos, BlockState blockState) {
        if(processing){
            level.addParticle(ParticleTypes.SMOKE, true, blockPos.getCenter().x, blockPos.getCenter().y, blockPos.getCenter().z, 0, 0.3, 0);
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag res = new CompoundTag();
        saveAdditional(res);
        return res;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
    }

    private void updateClient(){
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
    }

    public void ritualOver(){
        processing = false;
        updateClient();
    }

    public void beginRitual() {
        //itemHandler.extractItem(0, 1, false);
        processing = true;
        updateClient();
    }
}
