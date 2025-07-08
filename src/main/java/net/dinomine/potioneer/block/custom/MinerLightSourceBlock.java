package net.dinomine.potioneer.block.custom;

import net.dinomine.potioneer.block.entity.MinerLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MinerLightSourceBlock extends BaseLightSourceBlock {

    public MinerLightSourceBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MinerLightBlockEntity(blockPos, blockState);
    }
}
