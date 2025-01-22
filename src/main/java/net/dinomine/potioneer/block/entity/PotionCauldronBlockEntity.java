package net.dinomine.potioneer.block.entity;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.custom.PotionCauldronBlock;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.particle.ModParticles;
import net.dinomine.potioneer.recipe.PotionCauldronContainer;
import net.dinomine.potioneer.recipe.PotionCauldronRecipe;
import net.dinomine.potioneer.recipe.PotionContentData;
import net.dinomine.potioneer.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

import static net.dinomine.potioneer.block.custom.PotionCauldronBlock.RESULT;
import static net.dinomine.potioneer.block.custom.PotionCauldronBlock.WATER_LEVEL;

public class PotionCauldronBlockEntity extends BlockEntity {

    private static final int MAX_CAPACITY= 9;
    private PotionContentData result;
    private PotionContentData tempResult = PotionContentData.EMPTY;
    //private boolean conflict = false;
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
        this.result = PotionContentData.EMPTY.copy();
    }

    public PotionCauldronBlockEntity(BlockPos pPos, BlockState pState){
        super(ModBlockEntities.POTION_CAULDRON_BLOCK_ENTITY.get(), pPos, pState);
        state = State.STANDBY;
        this.result = PotionContentData.EMPTY.copy();
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

    public InteractionResult onPlayerInteract(Item item, ItemStack heldItemStack, PotionCauldronBlock cauldron, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand){
        //if on standby, checks if you can add the item to the inventory
        if(this.state == State.STANDBY){
            if(this.itemHandler.isItemValid(0, heldItemStack)){
                addIngredient(heldItemStack, !pPlayer.isCreative());
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;

        //if on finished, checks if there is a result and you can extract it
        } if(this.state == State.FINISHED && hasResult()){
            PotionContentData output = result.copy();
            if(output.isValidContainer(heldItemStack)){
                if(item == ModItems.VIAL.get()){
                    cauldron.changeWaterLevel(pLevel, pPos, -1);
                    result.amount = result.amount - 1;
                } else {
                    if(heldItemStack.getTag() != null && item == ModItems.FLASK.get()){
                        CompoundTag info = heldItemStack.getTag().getCompound("potion_info");
                        int level = info.getInt("amount");
                        result.amount = result.amount - 2 + level;
                        cauldron.changeWaterLevel(pLevel, pPos, -2 + level);

                    }
                    cauldron.changeWaterLevel(pLevel, pPos, -2);
                    result.amount = 0;
                }
                //if the cauldron becomes empty then clear the result variable
                if(level.getBlockState(getBlockPos()).getValue(WATER_LEVEL) < 2) {
                    this.state = State.STANDBY;
                    result = PotionContentData.EMPTY.copy();
                    countDown = 0;
                    level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(RESULT, false));
                    setChanged();
                }

                pLevel.playSound(null, pPos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1f, 1f);
                pLevel.playSound(null, pPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1f, 1f);

                //adding result to inventory
                ItemStack res;
                if(heldItemStack.is(Items.GLASS_BOTTLE)){
                    res = new ItemStack(ModItems.BEYONDER_POTION.get());
                } else {
                    res = heldItemStack.copy();
                }
                res.setCount(1);
                applyTags(res, output);
                heldItemStack.shrink(1);
                if(heldItemStack.isEmpty()){
                    pPlayer.setItemInHand(pHand, item == Items.GLASS_BOTTLE ? addRandomNbtTags(res) : res);
                } else if(!pPlayer.getInventory().add(res)){
                    pPlayer.drop(res, false);
                }

                return InteractionResult.SUCCESS;
            }
        }
        //fix for stuff like redstone being placed if the cauldron is finished
        if(this.itemHandler.isItemValid(0, heldItemStack)) return InteractionResult.SUCCESS;

        return InteractionResult.PASS;
    }

    private void applyTags(ItemStack stack, PotionContentData data){
        if(stack.hasTag()){
            CompoundTag info = stack.getTag().getCompound("potion_info");
            int level = info.getInt("amount");
            int newLevel = Math.max(2, level + data.amount);
            info.putInt("amount", newLevel);

            CompoundTag modData = stack.getTag();
            modData.put("potion_info", info);
            stack.setTag(modData);
        } else {
            CompoundTag tag = new CompoundTag();
            tag.putString("name", data.name);
            tag.putInt("amount", stack.is(ModItems.VIAL.get()) ? 1 : data.amount);
            tag.putInt("color", data.color);
            CompoundTag modData = new CompoundTag();
            modData.put("potion_info", tag);
            stack.setTag(modData);
        }
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

    public PotionContentData getResult(){
        return this.result;
    }

    public void craft(){
        List<PotionCauldronRecipe> recipe = getCurrentRecipe();
        if(recipe.isEmpty()){
            return;
        }

        if(state == State.STANDBY) {
            System.out.println("Attempting craft...");
            //list of all recipes that can conflict
            List<PotionCauldronRecipe> possibleConflicts = recipe.stream().filter(rec -> rec.getOutput().canConflict).toList();
            if(possibleConflicts.size() > 1) {
                System.out.println("Found a conflict");
                tempResult = PotionContentData.getConflictingResult(possibleConflicts.get(0).getOutput().bottle);
            }
                //no conflicts found so itll save the result
            else {
                //prioritizes the recipes that can conflict, so every other recipe is ignored
                System.out.println("attempting to craft conflicts...");
                if(!possibleConflicts.isEmpty()
                    && possibleConflicts.get(0).canCraft(getContainer(), level)){
                    //checks if it can craft the recipe
                    System.out.println("conflict crafted");
                    tempResult = possibleConflicts.get(0).getOutput();
                } else {
                    System.out.println("no conflicts works. checking standard matches...");
                    List<PotionCauldronRecipe> matches = recipe.stream().filter(pRec -> pRec.canCraft(getContainer(), level)).toList();
                    if(matches.isEmpty()) {
                        System.out.println("no matches found, exiting");
                        return;
                    }
                    else {
                        System.out.println("match crafted");
                        tempResult = matches.get(0).getOutput();
                    }
                }

            }

            System.out.println("concocting");
            concoct();
            setChanged();

        }

    }

    private PotionCauldronContainer getContainer(){
        int waterLevel = level.getBlockState(getBlockPos()).getValue(WATER_LEVEL);
        boolean onFire = blockBelowIsHot();
        PotionCauldronContainer items = new PotionCauldronContainer(onFire, waterLevel);

        for(int i = 0; i < itemHandler.getSlots(); i++){
            items.setItem(i, itemHandler.getStackInSlot(i));
        }
        return items;
    }

    private List<PotionCauldronRecipe> getCurrentRecipe() {
        return level.getRecipeManager().getRecipesFor(PotionCauldronRecipe.Type.INSTANCE, getContainer(), level);
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
        this.result = PotionContentData.load(modData.getCompound("result"));
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
        state = State.CONCOCTING;
        countDown = 0;
        sendData();
        setChanged();
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        switch(state){
            case STANDBY:
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
        //this.result = conflict ? PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POISON) : tempResult;
        this.result = tempResult.copy();
        pLevel.playSound(null, pPos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 1f, 1f);
        pLevel.setBlockAndUpdate(pPos, pState.setValue(RESULT, true));
        tempResult = PotionContentData.EMPTY.copy();
        clearContent();
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

    private ItemStack addRandomNbtTags(ItemStack stack){
        int num = (int) Math.ceil(Math.random()*3);
        CompoundTag nbt = stack.getTag();
        for (int i = 0; i < num; i++) {
            nbt.putInt("filler" + String.valueOf(i), i);
        }
        stack.setTag(nbt);
        return stack;
    }

    public void sendData() {
        if (level instanceof ServerLevel serverLevel)
            serverLevel.getChunkSource().blockChanged(getBlockPos());
    }
}
