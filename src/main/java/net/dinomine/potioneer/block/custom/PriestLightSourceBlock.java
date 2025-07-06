package net.dinomine.potioneer.block.custom;

import net.dinomine.potioneer.block.entity.MinerLightBlockEntity;
import net.dinomine.potioneer.block.entity.PriestLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PriestLightSourceBlock extends BaseLightSourceBlock {

    public PriestLightSourceBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PriestLightBlockEntity(blockPos, blockState);
    }
}
