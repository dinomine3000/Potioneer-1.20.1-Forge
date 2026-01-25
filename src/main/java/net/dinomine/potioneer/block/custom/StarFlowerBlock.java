package net.dinomine.potioneer.block.custom;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class StarFlowerBlock extends FlowerBlock {
    public static final BooleanProperty OPEN;

    public StarFlowerBlock(Supplier<MobEffect> effectSupplier, int effectDuration, Properties Properties) {
        super(effectSupplier, effectDuration, Properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(OPEN, false));
    }

    private void clientSetup(final FMLClientSetupEvent event){;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(OPEN, !pContext.getLevel().isDay());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(OPEN);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            pLevel.scheduleTick(pPos, this, 20);
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);
        if(pLevel.isDay() && pState.getValue(OPEN)){
            pLevel.setBlockAndUpdate(pPos, defaultBlockState().setValue(OPEN, false));
        }
        else if(!pLevel.isDay() && !pState.getValue(OPEN)){
            pLevel.setBlockAndUpdate(pPos, defaultBlockState().setValue(OPEN, true));
        }
        pLevel.scheduleTick(pPos, this, 20);
    }

    static {
        OPEN = BooleanProperty.create(Potioneer.MOD_ID + "_open");
    }
}
