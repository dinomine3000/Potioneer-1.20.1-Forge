package net.dinomine.potioneer.block.entity;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.custom.PotionCauldronBlock;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.particle.ModParticles;
import net.dinomine.potioneer.recipe.PotionCauldronContainer;
import net.dinomine.potioneer.recipe.PotionContentData;
import net.dinomine.potioneer.recipe.PotionRecipe;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.dinomine.potioneer.util.ModTags;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.dinomine.potioneer.util.misc.CharacteristicHelper;
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
import net.minecraft.util.Mth;
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
import java.util.ArrayList;
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
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if(stack.is(ModItems.BEYONDER_POTION.get()) || stack.is(ModBlocks.POTION_CAULDRON.get().asItem())) return false;
            return true;
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
            if(heldItemStack.is(ModItems.CAULDRON_ROD.get())){
                craft();
                return InteractionResult.SUCCESS;
            }
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
                    result.amount -= 1;
                } else {
                    if(heldItemStack.getTag() != null && heldItemStack.getTag().contains("potion_info")){
                        CompoundTag info = heldItemStack.getTag().getCompound("potion_info");
                        int level = info.getInt("amount");
                        result.amount += - 2 + level;
                        cauldron.changeWaterLevel(pLevel, pPos, -2 + level);
                    } else {
                        cauldron.changeWaterLevel(pLevel, pPos, -2);
                        result.amount = 0;
                    }
                }
                //if the cauldron becomes empty then clear the result variable
                if(result.amount < 1) {
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
                    pPlayer.setItemInHand(pHand, res);
                } else if(!pPlayer.getInventory().add(res)){
                    pPlayer.drop(res, false);
                }

                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        //fix for stuff like redstone being placed if the cauldron is finished
        if(this.itemHandler.isItemValid(0, heldItemStack)) return InteractionResult.SUCCESS;

        return InteractionResult.PASS;
    }

    private void applyTags(ItemStack stack, PotionContentData data){
        if(stack.hasTag() && stack.getTag().contains("potion_info")){
            CompoundTag info = stack.getTag().getCompound("potion_info");
            int level = info.getInt("amount");
            int newLevel = Math.max(2, level + data.amount);
            info.putInt("amount", newLevel);
            boolean prevVal = info.contains("isComplete") ? info.getBoolean("isComplete") : true;
            info.putBoolean("isComplete", data.isComplete && prevVal);

            CompoundTag modData = stack.getTag();
            modData.put("potion_info", info);
            stack.setTag(modData);
        } else {
            CompoundTag tag = new CompoundTag();
            tag.putString("name", data.name);
            tag.putInt("amount", stack.is(ModItems.VIAL.get()) ? 1 : data.amount);
            tag.putInt("color", data.color);
            tag.putBoolean("isComplete", data.isComplete);
            CompoundTag modData = new CompoundTag();
            modData.put("potion_info", tag);
            stack.setTag(modData);
        }
    }

    public void addIngredient(ItemStack itemStack, boolean shrink){
        addItem(itemStack, shrink);
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
        List<PotionRecipe> validRecipes = getMatchingRecipes();
        if(validRecipes.isEmpty()){
            return;
        }

        if(state == State.STANDBY) {
            //list of all recipes that can conflict
            List<PotionRecipe> possibleConflicts = validRecipes.stream().filter(rec -> rec.output().canConflict).toList();
            if(possibleConflicts.size() > 1) {
                tempResult = PotionContentData.getConflictingResult(possibleConflicts.get(0).output().bottle);
            } else {
                //no conflicts found so itll save the result
                //prioritizes the recipes that can conflict, so every other recipe is ignored
                if(possibleConflicts.isEmpty()){
                    //validRecipes contains only non-conflicting results, so get how many can successfuly craft
                    List<PotionRecipe> safeMatches = validRecipes.stream().filter(rec -> rec.canCraftSuccessfully(getContainer())).toList();
                    if(safeMatches.size() != 1){
                        //if it cant fully craft a single recipe (either cant craft any OR can craft too many) return an awkward result.
                        tempResult = PotionContentData.getIncompleteResult(validRecipes.get(0).output().bottle);
                    } else {
                        //if it can fully craft a single recipe, return that result.
                        tempResult = safeMatches.get(0).output().copy();
                    }
                } else {
                    //validRecipes contains exactly 1 possibly-conflicting recipe, so craft that one
                    PotionRecipe validRecipe = possibleConflicts.get(0);
                    tempResult = validRecipe.output().setCompletionStatus(validRecipe.canCraftSuccessfully(getContainer()));
                }

            }
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

    private List<PotionRecipe> getMatchingRecipes() {
        if(level.isClientSide()){
            return new ArrayList<>();
        }
        else{
            PotionFormulaSaveData data = PotionFormulaSaveData.from(((ServerLevel) level));
            return data.getRecipesFor(getContainer());
        }
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
        if(getBlockState().getValue(RESULT)){
            if(result != null && PotioneerMathHelper.isInteger(result.name)){
                CharacteristicHelper.addCharacteristicToLevel(Integer.parseInt(result.name), pLevel, null, pPos.getCenter(), level.random);
            }
        } else {
            NonNullList<ItemStack> items = NonNullList.withSize(itemHandler.getSlots(), ItemStack.EMPTY);
            for(int i = 0; i < itemHandler.getSlots(); i++){
                items.set(i, itemHandler.getStackInSlot(i));
            }
            Containers.dropContents(pLevel, pPos, items);
        }
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
        this.result = tempResult.copy();
        pLevel.playSound(null, pPos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 1f, 1f);
        pLevel.setBlockAndUpdate(pPos, pState.setValue(RESULT, true).setValue(WATER_LEVEL, Mth.clamp(pState.getValue(WATER_LEVEL), 2, 3)));
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

    public void sendData() {
        if (level instanceof ServerLevel serverLevel)
            serverLevel.getChunkSource().blockChanged(getBlockPos());
    }
}
