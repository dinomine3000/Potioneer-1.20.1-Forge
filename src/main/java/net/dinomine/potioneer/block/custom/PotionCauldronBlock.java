package net.dinomine.potioneer.block.custom;

import net.dinomine.potioneer.block.entity.ModBlockEntities;
import net.dinomine.potioneer.block.entity.PotionCauldronBlockEntity;
import net.dinomine.potioneer.util.ModTags;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

public class PotionCauldronBlock extends BaseEntityBlock {

    public static final BooleanProperty RESULT = BlockStateProperties.TRIGGERED;
    public static final IntegerProperty WATER_LEVEL = BlockStateProperties.LEVEL_CAULDRON;
    public static final DirectionProperty DIRECTION = BlockStateProperties.FACING;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(new Property[]{RESULT, WATER_LEVEL, DIRECTION});
    }


    public PotionCauldronBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATER_LEVEL, 1)
                .setValue(RESULT, false)
                .setValue(DIRECTION, Direction.NORTH));
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return (BlockState)this.defaultBlockState().setValue(DIRECTION, pContext.getHorizontalDirection().getOpposite());
    }

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 14, 16);
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack heldItemStack = pPlayer.getItemInHand(pHand);

        Item item = heldItemStack.getItem();
        int level = pState.getValue(WATER_LEVEL);

        BlockEntity be = pLevel.getBlockEntity(pPos);
        if(be instanceof PotionCauldronBlockEntity cauldron){
            if(!pLevel.isClientSide() && cauldron.state == PotionCauldronBlockEntity.State.STANDBY){
                if(item == Items.WATER_BUCKET){
                    if(level < 3){
                        if(!pPlayer.isCreative()){
                            pPlayer.setItemInHand(pHand, new ItemStack(Items.BUCKET));
                        }
                        pPlayer.awardStat(Stats.FILL_CAULDRON);
                        changeWaterLevel(pLevel, pPos, pState, 1);
                        pLevel.playSound(null, pPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1f, 1f);
                    }
                    cauldron.craft();
                    return InteractionResult.SUCCESS;
                }
                else if(item == Items.BUCKET){
                    if(level > 1){
                        if(!pPlayer.isCreative()){
                            heldItemStack.shrink(1);
                            if(heldItemStack.isEmpty()){
                                pPlayer.setItemInHand(pHand, new ItemStack(Items.WATER_BUCKET));
                            } else if(!pPlayer.getInventory().add(new ItemStack(Items.WATER_BUCKET))){
                                pPlayer.drop(new ItemStack(Items.WATER_BUCKET), false);
                            }
                        }

                        pPlayer.awardStat(Stats.USE_CAULDRON);
                        changeWaterLevel(pLevel, pPos, pState, -1);
                        pLevel.playSound(null, pPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                    }
                    cauldron.craft();
                    return InteractionResult.SUCCESS;
                }
                else if(!pLevel.isClientSide()){
                    if(heldItemStack.isEmpty()){
                        ItemStack rem = cauldron.removeItem();
                        if(!pPlayer.getInventory().add(rem)){
                            pPlayer.drop(rem, false);
                        }
                    } else if (item != Items.GLASS_BOTTLE){
                        if(heldItemStack.is(ModTags.Items.POTION_INGREDIENTS)){
                            if(pPlayer.isCreative()){
                                cauldron.addIngredient(heldItemStack, false);
                            } else {
                                cauldron.addIngredient(heldItemStack, true);
                            }
                        }
                        return InteractionResult.SUCCESS;
                    }
                }

            }
            else if(item == Items.GLASS_BOTTLE && !pLevel.isClientSide()){
                if(cauldron.hasResult()){
                    ItemStack res = cauldron.extractResult();
                    heldItemStack.shrink(1);
                    setWaterLevel(pLevel, pPos, 1);
                    pLevel.playSound(null, pPos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1f, 1f);
                        /*
                        use one of these sounds for beyonder potions
                        pLevel.playSound(null, pPos, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.BLOCKS, 1f, 1f);
                        */
                    pLevel.playSound(null, pPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1f, 1f);

                    if(heldItemStack.isEmpty()){
                        pPlayer.setItemInHand(pHand, res);
                    } else if(!pPlayer.getInventory().add(res)){
                        pPlayer.drop(res, false);
                    }
                }
            }
            else {
                return InteractionResult.SUCCESS;
            }
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }


    private void setWaterLevel(Level pLevel, BlockPos pPos, int level){
        pLevel.setBlockAndUpdate(pPos, pLevel.getBlockState(pPos).setValue(WATER_LEVEL, level));
    }

    private void changeWaterLevel(Level pLevel, BlockPos pPos, BlockState pState, int diff){
        //pState.trySetValue(LEVEL, diff);
        setWaterLevel(pLevel, pPos, pLevel.getBlockState(pPos).getValue(WATER_LEVEL)+ diff);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        boolean result = pState.getValue(RESULT);
        if(pState.getBlock() != pNewState.getBlock() && !result && !pLevel.isClientSide()){
            PotionCauldronBlockEntity be = (PotionCauldronBlockEntity) pLevel.getBlockEntity(pPos);
            if(be instanceof PotionCauldronBlockEntity){
                be.dropIngredients(pLevel, pPos);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }


    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean result = state.getValue(RESULT);
        if(!result && !level.isClientSide()){
            PotionCauldronBlockEntity be = (PotionCauldronBlockEntity) level.getBlockEntity(pos);
            assert be != null;
            be.dropIngredients(level, pos);
        }


        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PotionCauldronBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()){
            return createTickerHelper(pBlockEntityType, ModBlockEntities.POTION_CAULDRON_BLOCK_ENTITY.get(), PotionCauldronBlockEntity::particleTick);
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.POTION_CAULDRON_BLOCK_ENTITY.get(),
                ((pLevel1, pPos1, pState1, be1) -> be1.tick(pLevel1, pPos1, pState1)));
    }
}
