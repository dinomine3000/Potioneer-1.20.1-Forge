package net.dinomine.potioneer.block.entity;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.particle.ModParticles;
import net.dinomine.potioneer.recipe.PotionCauldronRecipe;
import net.dinomine.potioneer.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Optional;

import static net.dinomine.potioneer.block.custom.PotionCauldronBlock.RESULT;
import static net.dinomine.potioneer.block.custom.PotionCauldronBlock.WATER_LEVEL;

public class PotionCauldronBlockEntity extends BlockEntity {

    private static final int MAX_CAPACITY= 9;
    private ItemStack result;
    private ItemStack tempResult = ItemStack.EMPTY;
    private boolean recipeSuccess = false;
    private boolean conflict = false;
    public int countDown;
    public enum State {
        STANDBY,
        CONCOCTING,
        FINISHED,
        FINISHING
    }
    public State state;
    private static final boolean checkCraftsEveryTick = false;

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
                craft();
            }
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return(stack.is(ModTags.Items.POTION_INGREDIENTS));
        }
    };
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    //public static final Capability<IItemHandler> ITEM_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});

    public PotionCauldronBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);

        state = State.STANDBY;
        this.result = ItemStack.EMPTY;
    }

    public PotionCauldronBlockEntity(BlockPos pPos, BlockState pState){
        super(ModBlockEntities.POTION_CAULDRON_BLOCK_ENTITY.get(), pPos, pState);

        state = State.STANDBY;
        this.result = ItemStack.EMPTY;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return ModBlockEntities.POTION_CAULDRON_BLOCK_ENTITY.get().create(pos, state);
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        System.out.println("Capability goten");
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            return handler.cast();
        }

        return super.getCapability(cap);
    }

    public void addIngredient(ItemStack itemStack, boolean shrink){
        addItem(itemStack, shrink);
        craft();
    }

    public int caretPosition(){
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if(itemHandler.getStackInSlot(i).isEmpty()){
                return i;
            }
        }
        return MAX_CAPACITY;
    }

    private void addItem(ItemStack itemStack, boolean shrink){
        int numItems = caretPosition();
        if(itemStack != ItemStack.EMPTY && numItems >= 0 && numItems < MAX_CAPACITY && state == State.STANDBY){
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
        setChanged();
    }

    public ItemStack removeItem(){
        if(!hasResult() && !isEmpty()){
            int pos = caretPosition() - 1;
            return itemHandler.extractItem(pos, 1, false);
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
        countDown = 0;
        level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(RESULT, false));
        this.state = State.STANDBY;
        setChanged();
        return output;
    }

    public void craft(){
        List<PotionCauldronRecipe> recipe = getCurrentRecipe();
        if(state == State.STANDBY) {
            recipe.forEach(iRecipe -> {
                int waterLevel = level.getBlockState(getBlockPos()).getValue(WATER_LEVEL);
                boolean onFire = blockBelowIsHot();
                ItemStack output = iRecipe.getResultItem(null);
                /*System.out.println("output: " + output);
                System.out.println("Water level: " + waterLevel);
                System.out.println("Is hot: " + onFire);*/

                if (iRecipe.getWaterLevel() == waterLevel && !(iRecipe.needsFire() && !onFire)) {
                    //result = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.NIGHT_VISION);

                    if (!recipeSuccess) {
                        //System.out.println("Recipe succeeded");
                        recipeSuccess = true;
                        tempResult = output.copy();
                    } else {
                        //System.out.println("Result is already defined...");
                        if (!level.isClientSide()) {
                            if(this.tempResult.is(output.getItem())){
                                //System.out.println("Its the same result");
                            } else {
                                conflict = true;
                                /*System.out.println("Incoming shit result - " + iRecipe.getId() +
                                        " " + iRecipe.getResultItem(null).getDisplayName().getString() +
                                        " " + this.tempResult.getDisplayName().getString());*/
                            }
                        }

                    }
                }
            });

            if(recipeSuccess){
                concoct();
            }
            setChanged();

        }

    }

    private List<PotionCauldronRecipe> getCurrentRecipe() {
        SimpleContainer items = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++){
            items.setItem(i, itemHandler.getStackInSlot(i));
        }

        return level.getRecipeManager().getRecipesFor(PotionCauldronRecipe.Type.INSTANCE, items, level);
    }

    public boolean blockBelowIsHot(){
        if (level == null){
           return false;
        } else  {
            BlockState state = level.getBlockState(worldPosition.below());
            if(state.getBlock() instanceof BaseFireBlock) return true;
            if(state.is(ModTags.Blocks.FIRE_BLOCKS)) return true;
            if(!state.hasProperty(BlockStateProperties.LIT)) return false;
            return state.getValue(BlockStateProperties.LIT);
        }
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

        modData.putString("state", state.name());
        modData.putInt("countdown", countDown);
        pTag.put(Potioneer.MOD_ID, modData);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        CompoundTag modData = pTag.getCompound(Potioneer.MOD_ID);
        this.result = ItemStack.of(modData.getCompound("result"));
        itemHandler.deserializeNBT(modData.getCompound("inventory"));
        this.countDown = modData.getInt("countdown");
        String name = modData.getString("state");
        for (State state1 : State.values())
            if (state1.name().equals(name))
                this.state = state1;

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

    private void concoct(){
        //System.out.println("Concocting...");
        recipeSuccess = false;
        state = State.CONCOCTING;
        countDown = 0;
        sendData();
        setChanged();
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        switch(state){
            case STANDBY:
                if(checkCraftsEveryTick) craft();
                break;
            case CONCOCTING:
                countDown++;
                if(countDown >= 30){
                    countDown = 30;
                    state = State.FINISHING;
                    finishPotion(pLevel, pPos, pState);
                }
                sendData();
                break;
            case FINISHING:
                state = State.FINISHED;
                sendData();
        }

    }

    private void finishPotion(Level pLevel, BlockPos pPos, BlockState pState){
        this.result = conflict ? PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON) : tempResult;
        pLevel.playSound(null, pPos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 1f, 1f);
        pLevel.setBlockAndUpdate(pPos, pState.setValue(RESULT, true));
        conflict = false;
        tempResult = ItemStack.EMPTY;
        setChanged();
    }

    public static void particleTick(Level pLevel, BlockPos pPos, BlockState pState, PotionCauldronBlockEntity pBlockEntity) {
        RandomSource rnd = pLevel.random;
        if(pBlockEntity.state == State.FINISHING){
            int lim = 12;
            for (int i = 0; i < lim; i++) {
                pLevel.addParticle(ModParticles.POTION_CAULDRON_PARTICLES.get(),
                        pPos.getX()+ rnd.nextFloat(),
                        pPos.getY() + 1,
                        pPos.getZ() + rnd.nextFloat(),
                        0.2*Math.cos(i*Math.PI/6), 8.0E-2, 0.2*Math.sin(i*Math.PI/6));
            }
        }

    }


    public void sendData() {
        if (level instanceof ServerLevel serverLevel)
            serverLevel.getChunkSource().blockChanged(getBlockPos());
    }
}
