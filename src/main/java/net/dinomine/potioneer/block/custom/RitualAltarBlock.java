package net.dinomine.potioneer.block.custom;

import net.dinomine.potioneer.block.entity.ModBlockEntities;
import net.dinomine.potioneer.block.entity.PotionCauldronBlockEntity;
import net.dinomine.potioneer.block.entity.RitualAltarBlockEntity;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class RitualAltarBlock extends BaseEntityBlock {

    public static final BooleanProperty INCENSE = BooleanProperty.create("incense");
    public static final BooleanProperty PAPER = BooleanProperty.create("paper");
    public static final IntegerProperty CANDLES = IntegerProperty.create("ritual_candles", 0, 3);
    public static final DirectionProperty DIRECTION = HorizontalDirectionalBlock.FACING;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(INCENSE, PAPER, CANDLES, DIRECTION);
    }

    public RitualAltarBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(INCENSE, false)
                .setValue(PAPER, false)
                .setValue(CANDLES, 0)
                .setValue(DIRECTION, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(DIRECTION, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    private static final VoxelShape SHAPE_EW = Block.box(2, 0, 0, 14, 8, 16);
    private static final VoxelShape SHAPE_NS = Block.box(0, 0, 2, 16, 8, 14);
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(DIRECTION) == Direction.WEST || pState.getValue(DIRECTION) == Direction.EAST ? SHAPE_EW : SHAPE_NS;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pLevel.isClientSide()) return InteractionResult.SUCCESS;
        pLevel.setBlock(pPos, defaultBlockState()
                .setValue(CANDLES, (pLevel.getBlockState(pPos).getValue(CANDLES) + 1)% 4)
                .setValue(DIRECTION, pState.getValue(DIRECTION)),
                Block.UPDATE_NONE);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if(pState.getBlock() != pNewState.getBlock() && !pLevel.isClientSide()){
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if(be instanceof RitualAltarBlockEntity ritualEntity){
                ritualEntity.dropIngredients(pLevel, pPos);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }


//    @Override
//    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
//        boolean result = state.getValue(RESULT);
//        if(!result && !level.isClientSide()){
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
        return new RitualAltarBlockEntity(blockPos, blockState);
    }

//    @Override
//    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
//        if(pLevel.isClientSide()){
//            return createTickerHelper(pBlockEntityType, ModBlockEntities.POTION_CAULDRON_BLOCK_ENTITY.get(), PotionCauldronBlockEntity::particleTick);
//        }
//
//        return createTickerHelper(pBlockEntityType, ModBlockEntities.POTION_CAULDRON_BLOCK_ENTITY.get(),
//                ((pLevel1, pPos1, pState1, be1) -> be1.tick(pLevel1, pPos1, pState1)));
//    }

}
