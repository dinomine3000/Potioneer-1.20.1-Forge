package net.dinomine.potioneer.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RitualInk extends Block {

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = Map.of(
            Direction.NORTH, NORTH,
            Direction.SOUTH, SOUTH,
            Direction.EAST, EAST,
            Direction.WEST, WEST
    );

    public RitualInk(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState blockstate = pLevel.getBlockState(blockpos);
        return canBePlacedOn(pLevel, pPos, blockstate);
    }

    public static boolean canBePlacedOn(LevelReader pLevel, BlockPos pPos, BlockState blockstate) {
        return blockstate.isFaceSturdy(pLevel, pPos, Direction.UP) || blockstate.is(Blocks.HOPPER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if(!canBePlacedOn(pLevel, pPos.below(), pLevel.getBlockState(pPos.below()))){
            pLevel.destroyBlock(pPos, false);
            return;
        }
        BlockState newState = pState
                .setValue(NORTH, connectsTo(pLevel.getBlockState(pPos.north())))
                .setValue(SOUTH, connectsTo(pLevel.getBlockState(pPos.south())))
                .setValue(EAST, connectsTo(pLevel.getBlockState(pPos.east())))
                .setValue(WEST, connectsTo(pLevel.getBlockState(pPos.west())));
        pLevel.setBlock(pPos, newState, Block.UPDATE_ALL_IMMEDIATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return defaultBlockState()
                .setValue(NORTH, connectsTo(level.getBlockState(pos.north())))
                .setValue(SOUTH, connectsTo(level.getBlockState(pos.south())))
                .setValue(EAST, connectsTo(level.getBlockState(pos.east())))
                .setValue(WEST, connectsTo(level.getBlockState(pos.west())));
    }

    private boolean connectsTo(BlockState state) {
        return state.getBlock() instanceof RitualInk; // or allow more connections
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D); // 1 pixel tall
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return false;
    }
}
