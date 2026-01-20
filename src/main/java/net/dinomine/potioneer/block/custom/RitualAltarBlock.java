package net.dinomine.potioneer.block.custom;

import net.dinomine.potioneer.block.entity.ModBlockEntities;
import net.dinomine.potioneer.block.entity.RitualAltarBlockEntity;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
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
        if(pPlayer.getItemInHand(pHand).is(ModItems.RITUAL_DAGGER.get())){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof RitualAltarBlockEntity be){
                be.onTriggerRitual(pPlayer);
            }
//            int pathwaySequenceId = -1;
//            Optional<LivingEntityBeyonderCapability> cap = pPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
//            if(cap.isPresent()) pathwaySequenceId = cap.get().getPathwayId();
//            pathwaySequenceId = 48;
//            ArrayList<ItemStack> items = new ArrayList<>();
//            items.add(new ItemStack(Items.APPLE));
//            items.add(new ItemStack(Items.BLAZE_POWDER));
//            RitualSpiritsSaveData data = RitualSpiritsSaveData.from((ServerLevel) pLevel);
//            RitualInputData inputData = new RitualInputData(RitualInputData.FIRST_VERSE.DEFERENT, RitualInputData.SECOND_VERSE.ARROGANT,
//                    pPlayer, pPlayer, pathwaySequenceId, items, RitualInputData.ACTION.TRIGGER_LUCK_EVENT, "nop");
//
//            data.findSpiritForRitual(inputData);
        } else {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof RitualAltarBlockEntity be && be.state == RitualAltarBlockEntity.STATE.STANDBY){
                NetworkHooks.openScreen((ServerPlayer) pPlayer, be, be::writeStringsToBuffer);
            }
        }
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

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RitualAltarBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()){
            return createTickerHelper(pBlockEntityType, ModBlockEntities.RITUAL_ALTAR_BLOCK_ENTITY.get(), ((pLevel1, pPos1, pState1, be1) -> be1.clientTick(pLevel1, pPos1, pState1)));
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.RITUAL_ALTAR_BLOCK_ENTITY.get(),
                ((pLevel1, pPos1, pState1, be1) -> be1.serverTick(pLevel1, pPos1, pState1)));
    }

}
