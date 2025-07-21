package net.dinomine.potioneer.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RitualAltarBlockEntity extends BlockEntity {
    public RitualAltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RITUAL_ALTAR_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void dropIngredients(Level pLevel, BlockPos pPos) {
    }
}
