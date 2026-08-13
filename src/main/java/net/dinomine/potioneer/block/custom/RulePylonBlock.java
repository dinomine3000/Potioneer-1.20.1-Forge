package net.dinomine.potioneer.block.custom;

import net.dinomine.potioneer.beyonder.abilities.tyrant.RulePylonAbility;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.entity.ModBlockEntities;
import net.dinomine.potioneer.block.entity.RulePylonBlockEntity;
import net.dinomine.potioneer.savedata.DimensionChunkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class RulePylonBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    private static final VoxelShape SHAPE = Block.box(2, 2, 2, 14, 14, 14);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(WATERLOGGED);
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
        return true;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluidState = pContext.getLevel().getFluidState(pContext.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);

    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource().defaultFluidState() : super.getFluidState(pState);
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }


    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) return null;
        return createTickerHelper(pBlockEntityType, ModBlockEntities.RULE_PYLON_BLOCK_ENTITY.get(),
                ((pLevel1, pPos1, pState1, be1) -> be1.tick((ServerLevel) pLevel1, pPos1, pState1)));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return ItemStack.EMPTY;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if(be instanceof RulePylonBlockEntity pylonEntity){
            if(pylonEntity.isOwner(pPlayer)){
                if(pPlayer instanceof ServerPlayer sPlayer)
                    pylonEntity.openScreen(sPlayer);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public RulePylonBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RulePylonBlockEntity(blockPos, blockState);
    }

    public static boolean placePylon(ServerLevel dimensionLevel, BlockPos pos, Map<RulePylonAbility.Rule, RulePylonAbility.Punishment> rules, LivingEntity owner, int sequenceLevel){
        DimensionChunkSavedData data = DimensionChunkSavedData.from(dimensionLevel);
        if(!data.claimChunk(dimensionLevel, new ChunkPos(pos), pos, owner.getUUID(), sequenceLevel)) return false;

        boolean water = dimensionLevel.getFluidState(pos).getType() == Fluids.WATER;
        dimensionLevel.setBlockAndUpdate(pos,
                ModBlocks.RULE_PYLON.get().defaultBlockState().setValue(WATERLOGGED, water));
        RulePylonBlockEntity be = (RulePylonBlockEntity) dimensionLevel.getBlockEntity(pos);
        if(be != null) {
            be.setPlacedByPlayer(dimensionLevel, owner.getUUID(), sequenceLevel);
            be.setRules(rules);
        }
        return true;
    }

    public static boolean placePylon(ServerLevel dimensionLevel, BlockPos pos, Set<RulePylonAbility.Law> laws, LivingEntity owner, int sequenceLevel){
        DimensionChunkSavedData data = DimensionChunkSavedData.from(dimensionLevel);
        if(!data.claimChunk(dimensionLevel, new ChunkPos(pos), pos, owner.getUUID(), sequenceLevel)) return false;

        boolean water = dimensionLevel.getFluidState(pos).getType() == Fluids.WATER;
        dimensionLevel.setBlockAndUpdate(pos,
                ModBlocks.RULE_PYLON.get().defaultBlockState().setValue(WATERLOGGED, water));
        RulePylonBlockEntity be = (RulePylonBlockEntity) dimensionLevel.getBlockEntity(pos);
        if(be != null) {
            be.setPlacedByPlayer(dimensionLevel, owner.getUUID(), sequenceLevel);
            be.setLaws(laws);
        }
        return true;
    }


    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if(be instanceof RulePylonBlockEntity rulePylonBlock && pLevel instanceof ServerLevel sLevel){
            rulePylonBlock.onDestroy(sLevel);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

}
