package net.dinomine.potioneer.block.custom;

import net.dinomine.potioneer.block.entity.ModBlockEntities;
import net.dinomine.potioneer.block.entity.PotionCauldronBlockEntity;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
//            if(pLevel.isClientSide()) return InteractionResult.SUCCESS;
            if(cauldron.state == PotionCauldronBlockEntity.State.STANDBY){
                if(heldItemStack.isEmpty()){
                    ItemStack rem = cauldron.removeItem();
                    if(!pPlayer.getInventory().add(rem)){
                        pPlayer.drop(rem, false);
                    }
                    return InteractionResult.SUCCESS;
                }
                if(item == Items.WATER_BUCKET){
                    if(level < 3){
                        if(!pPlayer.isCreative()){
                            pPlayer.setItemInHand(pHand, new ItemStack(Items.BUCKET));
                        }
                        pPlayer.awardStat(Stats.FILL_CAULDRON);
                        changeWaterLevel(pLevel, pPos, 1);
                        pLevel.playSound(null, pPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1f, 1f);
                    }
                    cauldron.craft();
                    return InteractionResult.SUCCESS;
                }
                if(item == Items.BUCKET){
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
                        changeWaterLevel(pLevel, pPos, -1);
                        pLevel.playSound(null, pPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                    }
                    cauldron.craft();
                    return InteractionResult.SUCCESS;
                }
            }

            return cauldron.onPlayerInteract(item, heldItemStack, this, pLevel, pPos, pPlayer, pHand);
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }


    public void setWaterLevel(Level pLevel, BlockPos pPos, int level){
        pLevel.setBlockAndUpdate(pPos, pLevel.getBlockState(pPos).setValue(WATER_LEVEL, level));
    }

    public void changeWaterLevel(Level pLevel, BlockPos pPos, int diff){
        //pState.trySetValue(LEVEL, diff);
        setWaterLevel(pLevel, pPos, Mth.clamp(pLevel.getBlockState(pPos).getValue(WATER_LEVEL) + diff, 1, 3));
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if(pState.getBlock() != pNewState.getBlock() && !pLevel.isClientSide()){
            PotionCauldronBlockEntity be = (PotionCauldronBlockEntity) pLevel.getBlockEntity(pPos);
            if(be instanceof PotionCauldronBlockEntity){
                be.dropIngredients(pLevel, pPos);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }


//    @Override
//    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
//        if(!level.isClientSide()){
//            PotionCauldronBlockEntity be = (PotionCauldronBlockEntity) level.getBlockEntity(pos);
//            assert be != null;
//            be.dropIngredients(level, pos);
//        }
//
//
//        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
//    }

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

    @OnlyIn(Dist.CLIENT)
    public static class PotionCauldronTint implements BlockColor {

        @Override
        public int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int i) {
            if(i != 1) return -1;
            if(blockState.getValue(RESULT)){
                BlockEntity be = blockAndTintGetter.getBlockEntity(blockPos);
                if(be instanceof PotionCauldronBlockEntity cauldron){
                    return cauldron.getResult().color;
                }
                return 0x00D91EFF;
            }
            return 0x003F76E4;
        }
    }

}
